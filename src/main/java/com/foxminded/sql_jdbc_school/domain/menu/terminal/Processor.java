package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.EntityDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.dto.MenuDto;

public class Processor {
    
    private Formatter formatter;
    
    public Processor (Formatter formatter) {
        this.formatter = formatter;
    }
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]{2,30}$");
    private static final String BACK = "back";
    private static final StudentDao STUDENT_DAO = StudentDao.getInstance();
    private static final CourseDao COURSE_DAO = CourseDao.getInstance();
    private static final GroupDao GROUP_DAO = GroupDao.getInstance();
    
    public String requestToUser(BufferedReader reader, String prompt) {
        try {
            System.out.println(prompt);
            return reader.readLine();
        } catch (IOException e) {
            throw new DomainException(e);
        }
    }
    
    public MenuDto processAddNewStudent(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        dto = retriveNewName(reader, dto, "first name");
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        String firstName = dto.getNewName();
        dto = retriveNewName(reader, dto, "last name");
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        String lastName = dto.getNewName();
        dto.setStudent(Optional.of(STUDENT_DAO.save(new Student(firstName, lastName))));
        return dto;
    }
    
    private MenuDto retriveNewName(BufferedReader reader, MenuDto dto, String nameType) {
        String newName;
        do {
            newName = requestToUser(reader, "Enter " + nameType + "(2 to 30 latin letters)" + "\n"
                                          + "Enter \"back\" - to return to main menu");
            if(newName.equals(BACK)) {
                dto.setCanceled(true);
                return dto;
            }
        }while(!(match(NAME_PATTERN, newName)));
        dto.setNewName(newName);
        return dto;
    }
    
    private boolean match(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public MenuDto processDeleteById(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = STUDENT_DAO.getAll();
        dto = retriveEntityFromList(reader, dto, students, STUDENT_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        STUDENT_DAO.deleteById(dto.getStudent().get().getId());
        return dto;
    }

    public MenuDto processAddStudentCourse(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = STUDENT_DAO.getAll();
        dto = retriveEntityFromList(reader, dto, students, STUDENT_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        List<Course> courses = COURSE_DAO
                .getCoursesStudentDoesNotHave(dto.getStudent().get().getId()); 
        dto = retriveEntityFromList(reader, dto, courses, COURSE_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        }
           
        Set<Integer> courseId = new HashSet<>();
        courseId.add(dto.getCourse().get().getId());
        STUDENT_DAO.addStudentToCourses(dto.getStudent().get().getId(), courseId);
        return dto;
    }
   
    public MenuDto processDeleteStudentFromCourse(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = STUDENT_DAO.getAll();
        dto = retriveEntityFromList(reader, dto, students, STUDENT_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        List<Course> coursesOfStudent = COURSE_DAO
                .getCoursesByStudentId(dto.getStudent().get().getId());
        dto = retriveEntityFromList(reader, dto, coursesOfStudent, COURSE_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        STUDENT_DAO.deleteStudentFromCourse(dto.getStudent().get().getId(),
                                            dto.getCourse().get().getId());
        return dto;
    }
    
    public MenuDto processGetStudentsByCourse(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        List<Course> coursesList = COURSE_DAO.getAll();
        dto = retriveEntityFromList(reader, dto, coursesList, COURSE_DAO);
        
        if(dto.isCanceled()) {
            return dto;
        
        }
        List<Student> students = STUDENT_DAO
                .getAllByCourseName(dto.getCourse().get().getName());
        dto.setStudents(students);
        return dto;
    }
    
    public MenuDto processFindGroupsByStudentCount(BufferedReader reader) {
        MenuDto dto = new MenuDto();
        String prompt = "Enter students count (integer value)" + "\n"
                      + "Enter \"back\" to return to main menu" ;
        while(true) {
            String response =  requestToUser(reader, prompt);
            
            if(response.equals(BACK)) {
                dto.setCanceled(true);
                return dto;
            }
          
            if(isInteger(response)) {
                List<Group> groups = GROUP_DAO
                        .getAllByStudentsQuantity(Integer.valueOf(response));
                if(groups.isEmpty()) {
                    System.out.println("There is no groups with such or less students count");
                }else {
                    dto.setGroups(groups);
                    return dto; 
                }
            }else {
                System.out.println("You have to enter integer value!");
            }    
         }
    }

    private <T> MenuDto retriveEntityFromList(BufferedReader reader,
                                               MenuDto dto,
                                               List<T> list,
                                               EntityDao<T, Integer> entityDao) {
        String entityList = formatter.formatEntityList(list);
        String entityTypeName = retriveEntityTypeName(entityDao);
        String prompt =
               "===========================================================" + "\n"  
                + entityList + """
                
                ===========================================================
                
                Enter""" + " " + entityTypeName +  """
                 id from a list (integer value)
                Enter \"back\" to return to main menu""";
        while(true) {
           String response =  requestToUser(reader, prompt);
           
           if(response.equals(BACK)) {
               dto.setCanceled(true);
               return dto;
           }
         
           if(isInteger(response)) {
               Optional<T> entity = entityDao.getById(Integer.valueOf(response));
               if(entity.isPresent()) {
                   if(list.contains(entity.get())) {
                       dto.<T>setEntity(entity);
                       return dto;
                   }else {
                       System.out.println("You should chose from the list!");
                   }
               }else {
                   System.out.println("There is no such ID in school.");
               }     
           }else {
               System.out.println("ID should be integer!");
           }    
        }
    }
    
    private <T> String retriveEntityTypeName(EntityDao<T, Integer> entityDao) {
        if (entityDao instanceof StudentDao) {
            return "student";
        }
        if (entityDao instanceof CourseDao) {
            return "course";
        } 
    return null;
    }
    
    private boolean isInteger(String string) {
        try { 
            Integer.valueOf(string);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }
}