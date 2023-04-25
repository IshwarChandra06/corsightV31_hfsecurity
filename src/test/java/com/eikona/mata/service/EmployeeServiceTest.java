package com.eikona.mata.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.impl.model.EmployeeServiceImpl;
import com.eikona.mata.util.ExcelEmployeeImport;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {
	
	@Mock
    private EmployeeRepository employeeRepository;
	
	@Mock
	private ExcelEmployeeImport excelEmployeeImport;

    @InjectMocks
    private  EmployeeServiceImpl  employeeService;
    
    private Employee employee;
    private Optional<Employee> employeeOptional;
    private List<Employee> employees;
    
    @Before
    public void initObjects() {
    	
    	employee = new Employee();
    	employeeOptional = Optional.of(employee);
    	employees = new ArrayList<Employee>();
    }
    
//    getById(Long id)
    @Test
    public void shouldFindWorkById() {
    	employee = employeeRepository.getEmp();
        when(employeeRepository.findById(1l)).thenReturn(employeeOptional);
        assertEquals(employeeOptional.get(), employeeService.getById(1));
//        verify(employeeRepository, times(1)).findById(1l);
    }
    
//  storeCosecEmployeeList(MultipartFile file);
    @Test
	public void storeCosecEmployeeListTest() {
		
		MultipartFile file = new MultipartFile() {
			
			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				
				
			}
			
			@Override
			public boolean isEmpty() {
				
				return false;
			}
			
			@Override
			public long getSize() {
				
				return 0;
			}
			
			@Override
			public String getOriginalFilename() {
				
				return null;
			}
			
			@Override
			public String getName() {
				
				return null;
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				
				return null;
			}
			
			@Override
			public String getContentType() {
				
				return null;
			}
			
			@Override
			public byte[] getBytes() throws IOException {
				
				return null;
			}
		};
		employeeService.storeCosecEmployeeList(file);
		
		verify(employeeRepository, times(1)).saveAll(employees);
	}
    
    
//	void saveEmployeeAreaAssociation(Employee employee, Long id, Principal principal);
    @Test
    public void saveEmployeeAreaAssociation() {
    	Principal principal = new Principal() {
			
			@Override
			public String getName() {
				return "System";
			}
		};
    	employeeService.saveEmployeeAreaAssociation(employee, 1l, principal);
    	
    	verify(employeeRepository, times(1)).save(employee);
    }
//	String saveEmployeeDeviceAssociation(Long deviceId, Long empId, Principal principal) throws Exception;
//	
//	String deleteEmployeeFromParticularDevice(Long deviceId, Long empId, Principal principal) throws Exception;
//	
//	PaginationDto<Employee> searchByField(Long id, String name, String empId, String branch, String department,
//			String designation, int pageno, String sortField, String sortDir);
//	
//	PaginationDto<EmployeeToDeviceAssociationDto> searchEmployeeToDevice(Long id, String device, String office,
//			String area, int pageno, String sortField, String sortDir);
    
    
}
