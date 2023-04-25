package com.eikona.mata.service;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.eikona.mata.entity.Department;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.service.impl.model.DepartmentServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentServiceTest {
	
	@Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private  DepartmentServiceImpl  departmentService;
    
    private Department department;
    private Optional<Department> departmentOptional;
    private List<Department> departments;
    
    @Before
    public void initObjects() {
    	department = new Department();
        departmentOptional = Optional.of(department);
    }

    @Test
    public void saveDepartmentTest() {
    	departmentService.save(department);
        verify(departmentRepository, times(1)).save(department);
    }
}
