package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.foxminded.sql_jdbc_school.data.SchoolDto;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class GroupsAssignation {
    
    private static final int MIN_GROUP_SIZE = 10;
    private static final int MAX_GROUP_SIZE = 30;
    
    
    
    public SchoolDto assign(SchoolDto dto) {
        Map<Group, Integer> groupSizes = retriveGroupSizes(dto.getGroups());
        List<Student> assignedStudents = new ArrayList<>();
        for (Map.Entry<Group, Integer> entry : groupSizes.entrySet()){
            //Here i'll continue, when i put groups into table 
        }
        return null;
    }
    
    private Map<Group, Integer>  retriveGroupSizes(List<Group> groups) {
        Map<Group, Integer> result = new HashMap<>();
        for(Group group : groups) {
            result.put(group, retriveRandomBetween(MIN_GROUP_SIZE, MAX_GROUP_SIZE));
        }
        return result;
    }
    
    private int retriveRandomBetween(int min, int max) {
        Random random = new Random ();
        return random.ints(1, min, max + 1)
                     .sum();      
    }
}



























