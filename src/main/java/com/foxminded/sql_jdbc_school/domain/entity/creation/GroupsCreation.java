package com.foxminded.sql_jdbc_school.domain.entity.creation;

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
        
        while(result.size() < 10) {
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
        return  randomInteger.ints(97, 123)
        .mapToObj(i -> (char) i)
        .limit(2)
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
    }
    
    private String retriveRandomNumber() throws NoSuchAlgorithmException {
        Random randomInteger = SecureRandom.getInstanceStrong();
        return "" + randomInteger.ints(10,99)
                                 .limit(1)
                                 .sum();
    }
}