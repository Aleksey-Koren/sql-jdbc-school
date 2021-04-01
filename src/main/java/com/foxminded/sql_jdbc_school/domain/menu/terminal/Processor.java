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
    
    public String requestToUser(BufferedReader reader, String prompt) {
        try {
            System.out.println(prompt);
            return reader.readLine();
        } catch (IOException e) {
            throw new DomainException(e);
        }
    }
    
    public <T,I> MenuDto processAddNewStudent(BufferedReader reader, StudentDao dao) {
        MenuDto dto = new MenuDto();
        retriveNewName(reader, dto, "first name");
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        String firstName = dto.getNewName();
        retriveNewName(reader, dto, "last name");
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        String lastName = dto.getNewName();
        dto.setStudent(Optional.of(dao.save(new Student(firstName, lastName))));
        return dto;
    }
    
    private MenuDto retriveNewName(BufferedReader reader, MenuDto dto, String nameType) {
        String newName;
        do {                        
            newName = requestToUser(reader, formatter.formatNewNamePrompt(nameType));
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

    public MenuDto processDeleteById(BufferedReader reader, StudentDao studentDao) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = studentDao.getAll();
        selectEntityFromList(reader, dto, students, studentDao);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        studentDao.deleteById(dto.getStudent().get().getId());
        return dto;
    }

    public MenuDto processAddStudentToCourse(BufferedReader reader,
                                           StudentDao studentDao, CourseDao courseDao) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = studentDao.getAll();
        selectEntityFromList(reader, dto, students, studentDao);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        List<Course> courses = courseDao
                .getCoursesStudentDoesNotHave(dto.getStudent().get().getId()); 
        selectEntityFromList(reader, dto, courses, courseDao);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
           
        Set<Integer> courseId = new HashSet<>();
        courseId.add(dto.getCourse().get().getId());
        studentDao.addStudentToCourses(dto.getStudent().get().getId(), courseId);
        return dto;
    }
   
    public MenuDto processDeleteStudentFromCourse(BufferedReader reader,
                            StudentDao studentDao, CourseDao courseDao) {
        MenuDto dto = new MenuDto();
        
        List<Student> students = studentDao.getAll();
        selectEntityFromList(reader, dto, students, studentDao);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        List<Course> coursesOfStudent = courseDao
                .getCoursesByStudentId(dto.getStudent().get().getId());
        selectEntityFromList(reader, dto, coursesOfStudent, courseDao);
        
        if(dto.isCanceled()) {
            return dto;
        }
        
        studentDao.deleteStudentFromCourse(dto.getStudent().get().getId(),
                                            dto.getCourse().get().getId());
        return dto;
    }
    
    public MenuDto processGetStudentsByCourse(BufferedReader reader,
                                              StudentDao studentDao,
                                              CourseDao courseDao) {
        MenuDto dto = new MenuDto();
        List<Course> coursesList = courseDao.getAll();
        selectEntityFromList(reader, dto, coursesList, courseDao);
        
        if(dto.isCanceled()) {
            return dto;
        
        }
        List<Student> students = studentDao
                .getAllByCourseName(dto.getCourse().get().getName());
        dto.setStudents(students);
        return dto;
    }
    
    public MenuDto processFindGroupsByStudentCount(BufferedReader reader, GroupDao groupDao) {
        MenuDto dto = new MenuDto();

        while(true) {
            String response =  requestToUser(reader, Formatter.STUDENTS_COUNT_PROMPT);
            
            if(response.equals(BACK)) {
                dto.setCanceled(true);
                return dto;
            }
          
            if(isInteger(response)) {
                List<Group> groups = groupDao
                        .getAllByStudentsQuantity(Integer.valueOf(response));
                if(groups.isEmpty()) {
                    System.out.println(Formatter.NO_SUCH_GROUPS);
                }else {
                    dto.setGroups(groups);
                    return dto; 
                }
            }else {
                System.out.println(Formatter.ENTER_INTEGER);
            }    
         }
    }

    private <T> void selectEntityFromList(BufferedReader reader,
                                               MenuDto dto,
                                               List<T> list,
                                               EntityDao<T, Integer> entityDao) {
        String entityList = formatter.formatEntityList(list);
        String entityTypeName = retriveEntityTypeName(entityDao);
        String prompt = formatter.formatEntityFromListPrompt(entityList, entityTypeName);

        while(!dto.isSelected()) {
           String response =  requestToUser(reader, prompt);
           
           if(response.equals(BACK)) {
               dto.setCanceled(true);
               break;
           }    
           selectIfValidResponse(response, dto, list, entityDao);
        }
        dto.setSelected(false);
    }
    
    private <T> void selectIfValidResponse(String response,
                                           MenuDto dto,
                                           List<T> list,
                                           EntityDao<T, Integer> entityDao ) {
        if(isInteger(response)) {
            Optional<T> entity = entityDao.getById(Integer.valueOf(response));
            selectIfPresent(entity, dto, list);   
        }else {
            System.out.println(Formatter.ID_SHOULD_BE_INT);
        }   
    }
    
    private <T> void selectIfPresent(Optional<T> entity, MenuDto dto, List<T> list) {
        if(entity.isPresent()) {
            selectIfFromList(entity, dto, list);
        }else {
            System.out.println(Formatter.NO_SUCH_ID);
        } 
    }
    
    private <T> void selectIfFromList(Optional<T> entity, MenuDto dto, List<T> list) {
        if(list.contains(entity.get())) {
            dto.<T>setEntity(entity);
            dto.setSelected(true);
        }else {
            System.out.println(Formatter.CHOSE_FROM_LIST);
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