package com.foxminded.sql_jdbc_school.dao.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;

class PropertiesUtilTest {
    
    @Test
    void properties_shouldBeLoadedCorrectly() {
        assertEquals("test_property", PropertiesUtil.get("test"));
    }

}