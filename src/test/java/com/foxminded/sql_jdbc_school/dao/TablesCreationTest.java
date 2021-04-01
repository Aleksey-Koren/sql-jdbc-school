package com.foxminded.sql_jdbc_school.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;

@TestInstance(Lifecycle.PER_CLASS)
class TablesCreationTest {
    
    private final static String STUDENTS_DATA = """
            ColumnsData:|
            |ID|INTEGER|NO|YES|GROUP_ID|INTEGER|YES|NO|FIRST_NAME|VARCHAR|NO|NO|LAST_NAME|VARCHAR|NO|NO|
            |Primary keys data:|
            |ID|
            |Exported keys data:|
            |STUDENTS|ID|STUDENTS_COURSES|STUDENT_ID|
            |Imported keys data:|
            |GROUPS|ID|STUDENTS|GROUP_ID""";
    private final static String GROUPS_DATA = """
            ColumnsData:|
            |ID|INTEGER|NO|YES|GROUP_NAME|VARCHAR|NO|NO|
            |Primary keys data:|
            |ID|
            |Exported keys data:|
            |GROUPS|ID|STUDENTS|GROUP_ID|
            |Imported keys data:|
            """;
    private final static String COURSES_DATA = """
            ColumnsData:|
            |ID|INTEGER|NO|YES|COURSE_NAME|VARCHAR|NO|NO|COURSE_DESCRIPTION|VARCHAR|NO|NO|
            |Primary keys data:|
            |ID|
            |Exported keys data:|
            |COURSES|ID|STUDENTS_COURSES|COURSE_ID|
            |Imported keys data:|
            """;

    private final static String STUDENTS_COURSES_DATA = """
            ColumnsData:|
            |STUDENT_ID|INTEGER|YES|NO|COURSE_ID|INTEGER|YES|NO|
            |Primary keys data:|
            |
            |Exported keys data:|
            |
            |Imported keys data:|
            |COURSES|ID|STUDENTS_COURSES|COURSE_ID|STUDENTS|ID|STUDENTS_COURSES|STUDENT_ID""";
 
    @BeforeAll
    private void createTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }
    
    @Test
    void create_shouldCreateCorrectTables() {
        Set<String> expectedTableNames = retriveTableNames();
        Set<String> tableNames = new HashSet<>();
        try(Connection connection = ConnectionManager.get()){
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet resultSet = metadata.getTables(null,"PUBLIC" , null, null);
            while(resultSet.next()) {
                tableNames.add(resultSet.getString("TABLE_NAME"));
            }
            assertEquals(expectedTableNames, tableNames);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
    
    private Set<String> retriveTableNames(){
        return new HashSet<>(Arrays.asList("COURSES",
                                           "GROUPS",
                                           "STUDENTS",
                                           "STUDENTS_COURSES"));     
    }
    
    @Test
    void create_shouldCreateStudentsTableWithCorrectFields() {
        assertEquals(STUDENTS_DATA, retriveTableData("STUDENTS"));
    }
    
    @Test
    void create_shouldCreateGroupsTableWithCorrectFields() {
        assertEquals(GROUPS_DATA, retriveTableData("GROUPS"));
    }
    
    @Test
    void create_shouldCreateCoursesTableWithCorrectFields() {
        assertEquals(COURSES_DATA, retriveTableData("COURSES"));
    }
    
    @Test
    void create_shouldCreateStudentsCoursesTableWithCorrectFields() {     
        assertEquals(STUDENTS_COURSES_DATA, retriveTableData("STUDENTS_COURSES"));
    }
    
    private String retriveTableData(String tableName) {    
        try(Connection connection = ConnectionManager.get()){
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, "PUBLIC", tableName, null);
            ResultSet pKeys = metaData.getPrimaryKeys(null, "PUBLIC", tableName);
            ResultSet importedKeys = metaData.getImportedKeys(null, "PUBLIC", tableName);
            ResultSet exportedKeys = metaData.getExportedKeys(null, "PUBLIC", tableName);
            StringJoiner tableData = new StringJoiner("|");
            tableData.add("ColumnsData:");
            tableData.add("\n");
            while (columns.next()){
                tableData.add(columns.getString("COLUMN_NAME"));
                tableData.add(columns.getString("TYPE_NAME"));
                tableData.add(columns.getString("IS_NULLABLE"));
                tableData.add(columns.getString("IS_AUTOINCREMENT"));
            }
            tableData.add("\n");
            tableData.add("Primary keys data:");
            tableData.add("\n");
            while(pKeys.next()) {
                tableData.add(pKeys.getString("COLUMN_NAME"));  
            }
            tableData.add("\n");
            tableData.add("Exported keys data:");
            tableData.add("\n");
            while(exportedKeys.next()) {
                tableData.add(exportedKeys.getString("PKTABLE_NAME"));
                tableData.add(exportedKeys.getString("PKCOLUMN_NAME"));
                tableData.add(exportedKeys.getString("FKTABLE_NAME"));
                tableData.add(exportedKeys.getString("FKCOLUMN_NAME"));
            }
            tableData.add("\n");
            tableData.add("Imported keys data:");
            tableData.add("\n");
            while(importedKeys.next()) {
                tableData.add(importedKeys.getString("PKTABLE_NAME"));
                tableData.add(importedKeys.getString("PKCOLUMN_NAME"));
                tableData.add(importedKeys.getString("FKTABLE_NAME"));
                tableData.add(importedKeys.getString("FKCOLUMN_NAME"));
            }  
            return tableData.toString();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
}