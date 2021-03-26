package com.foxminded.sql_jdbc_school.domain.data_generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;
import com.foxminded.sql_jdbc_school.domain.entity.Group;

class GroupsGeneratorTest {
    
    @Test
    void generate_shouldGenerateCorrectQuantityOfGroups() {
        int expectedQuantity = Integer.parseInt(PropertiesUtil.get("quantity.of.groups"));
        GroupsGenerator generator = new GroupsGenerator();
        List<Group> groups = generator.generate();
        assertEquals(expectedQuantity, groups.size());
    }
    
    @Test
    void generate_shouldGenerateGroupsWithCorrectNames() {
        Pattern pattern = Pattern.compile("^([a-z]{2})([-])([0-9]{2})$");
        GroupsGenerator generator = new GroupsGenerator();
        List<Group> groups = generator.generate();
        groups.forEach(s -> { Matcher matcher = pattern.matcher(s.getName());
                              assertTrue(matcher.matches());
                              });
    }
}