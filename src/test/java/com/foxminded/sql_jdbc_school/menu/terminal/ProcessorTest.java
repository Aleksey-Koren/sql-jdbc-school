package com.foxminded.sql_jdbc_school.menu.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foxminded.sql_jdbc_school.domain.menu.terminal.Processor;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.ToStringFormatter;


@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    
    private Processor processor = new Processor(new ToStringFormatter());
    
    
    @Test
    void requestToUser_shouldReturnUserInput() throws IOException{
        String expected = "expected response";
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(expected);
        assertEquals(expected, processor.requestToUser(reader, "prompt"));
    }
    
    @Test
    void requestToUser_shouldPrintPromptToConsole() throws IOException {
        PrintStream defaultOutput = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        PrintStream outputWrapper = new PrintStream(testOutput);
        System.setOut(outputWrapper);
        String expected = "expected response";
        String prompt = "prompt";
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(expected);
        processor.requestToUser(reader, prompt);
        assertEquals(prompt + "\r\n", testOutput.toString());
        System.setOut(defaultOutput);
    }
    

}
