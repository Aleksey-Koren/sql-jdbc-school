package com.foxminded.sql_jdbc_school.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;

public class TablesCreation {

    private static final Path TABLE_CREATION = Path.of("src", "main", "resources"
                                                            , "sql", "TablesCreation.sql");
    
    public void create() {     
        try(Connection connection = ConnectionManager.get();
            PreparedStatement createTables = connection.prepareStatement(getScript(TABLE_CREATION))) {
            createTables.executeUpdate();
        } catch (SQLException e) {
            throw new DaoRuntimeException("Exception in tables creation", e);
        }
    }
    
    private static String getScript(Path path) {      
        try(Stream<String> lines = Files.lines(path)) {
            return lines.reduce("", (a,b) -> a + b);
        } catch (IOException e) {
            throw new DaoRuntimeException("Can't read sql script", e);
        } 
    }
}