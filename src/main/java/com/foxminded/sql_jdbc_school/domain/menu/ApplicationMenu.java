package com.foxminded.sql_jdbc_school.domain.menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ApplicationMenu {
    
    public void findGroupsByStudentsCount() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter students count");
    }
}
