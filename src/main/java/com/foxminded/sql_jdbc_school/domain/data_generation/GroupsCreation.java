package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.foxminded.sql_jdbc_school.domain.entity.Group;

public class GroupsCreation implements EntityCreation<Group> {
    
    private static final String HYPHEN = "-";
    private static final int QUANTITY_OF_GROUPS = 10;
    private static final int CHAR_RANGE_BEGIN = 97;
    private static final int CHAR_RANGE_END = 123;
    private static final int NUM_RANGE_BEGIN = 10;
    private static final int NUM_RANGE_END = 100;

    @Override
    public List<Group> create() throws NoSuchAlgorithmException {
        Set<String> names = retriveSetOfNames();
        List<Group> groups = new ArrayList<>();
        for(String name : names) {
            groups.add(new Group(name));
        }
        return groups;
    }
    
    private  Set<String> retriveSetOfNames() throws NoSuchAlgorithmException {
        Set<String> result = new HashSet<>();
        
        while(result.size() < QUANTITY_OF_GROUPS) {
            result.add(retriveName());
        }
        return result;
    }
     
    private String retriveName() throws NoSuchAlgorithmException {
        String firstPart = retriveRandomString();
        String secondPart = retriveRandomNumber();
        return firstPart + HYPHEN + secondPart;
    }
    
    private String retriveRandomString() throws NoSuchAlgorithmException {
        Random randomInteger = SecureRandom.getInstanceStrong();
        return  randomInteger.ints(CHAR_RANGE_BEGIN, CHAR_RANGE_END)
        .mapToObj(i -> (char) i)
        .limit(2)
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
    }
    
    private String retriveRandomNumber() throws NoSuchAlgorithmException {
        Random randomInteger = SecureRandom.getInstanceStrong();
        return "" + randomInteger.ints(NUM_RANGE_BEGIN, NUM_RANGE_END)
                                 .limit(1)
                                 .sum();
    }
}