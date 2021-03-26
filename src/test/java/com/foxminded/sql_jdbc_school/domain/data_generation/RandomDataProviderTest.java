package com.foxminded.sql_jdbc_school.domain.data_generation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class RandomDataProviderTest {
    
    @Mock
    StudentsGenerator studentGenerator = new StudentsGenerator();
    @Mock
    GroupsGenerator groupsGenerator = new GroupsGenerator();
    @Mock
    CourseGenerator courseGenerator = new CourseGenerator();
    @Mock
    RandomAssignment assignment = new RandomAssignment();
    

    
    @Test
    void provide_shouldWorcksCorrectly() {
        TablesCreation creation = new TablesCreation();
        creation.create();
        
        InOrder order = inOrder(studentGenerator, groupsGenerator, courseGenerator, assignment);
        RandomDataProvider provider = new RandomDataProvider(studentGenerator,
                                                             groupsGenerator,
                                                             courseGenerator,
                                                             assignment);
        
        provider.provide();
        
        order.verify(studentGenerator).generate();
        order.verify(courseGenerator).generate();
        order.verify(groupsGenerator).generate(); 
        order.verify(assignment).assignCourses(any());
        order.verifyNoMoreInteractions();
    }
}