package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.dto.SchoolDto;

public class RandomAssignment implements Assigment {
    
    private static final int MIN_GROUP_SIZE =
            Integer.parseInt(PropertiesUtil.get("min.group.size"));
    private static final int MAX_GROUP_SIZE =
            Integer.parseInt(PropertiesUtil.get("max.group.size"));
    private static final int MIN_COURSE_QUANTITY =
            Integer.parseInt(PropertiesUtil.get("min.course.quantity"));
    private static final int MAX_COURSE_QUANTITY =
            Integer.parseInt(PropertiesUtil.get("max.course.quantity"));
        
    public List<Student> assignGroups(SchoolDto dto) {
        List<Student> students = dto.getStudents();
        List<Student> studentsToDelete = new ArrayList<>(students);
        List<Group> groups = dto.getGroups();
        for(Group group : groups) {
            int groupId = group.getId();
            for(int i = 0; i < retriveRandomQuantity(MIN_GROUP_SIZE, MAX_GROUP_SIZE); i++) {
                int index = retriveRandomIndex(studentsToDelete.size());
                Student current = studentsToDelete.get(index);
                current.setGroupId(groupId);
                studentsToDelete.remove(index);
            }
        }  
        StudentDao studentDao = StudentDao.getInstance();
        studentDao.updateAll(students);
        return students;
    }
    
    public void assignCourses(SchoolDto dto) {
        List<Student> studentsInGroups = retriveStudentsAssignedToGroups(dto.getStudents());
        List<Integer> coursesIds = retriveCoursesIds(dto.getCourses());
        assignRandomCourses(studentsInGroups, coursesIds);
    }
    
    private List<Student> retriveStudentsAssignedToGroups(List<Student> students) {
        return students.stream()
                .filter(s -> s.getGroupId() != null)
                .collect(Collectors.toList());
    }
    
    private List<Integer> retriveCoursesIds(List<Course> courses) {
        return courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
    }
    
    private void assignRandomCourses(List<Student> students, List<Integer> courseId) {
        for(Student student : students) {
            Set<Integer> courseIdToAssign = retriveIdsToAssign(courseId);
            StudentDao studentDao = StudentDao.getInstance();
            studentDao.addStudentToCourses(student.getId(), courseIdToAssign);
        }
    }
    
    private Set<Integer> retriveIdsToAssign(List<Integer> courseId) {
        Set<Integer> result = new HashSet<>();
        int quantity = retriveRandomQuantity(MIN_COURSE_QUANTITY, MAX_COURSE_QUANTITY);
        while(result.size() < quantity) {
            result.add(courseId.get(retriveRandomIndex(courseId.size())));
        }
        return result;
    }

    private int retriveRandomIndex(int indexQuantity) {
        Random random = new Random ();
        return random.nextInt(indexQuantity);
    }
    
    private int retriveRandomQuantity(int min, int max) {
        Random random = new Random ();
        return random.ints(1, min, max + 1)
                     .sum();      
    }
}