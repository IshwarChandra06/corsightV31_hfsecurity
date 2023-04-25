package com.eikona.mata.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Department;
import com.eikona.mata.entity.Designation;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.ShiftRepository;

@Component
public class EmployeeObjectMap {
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DesignationRepository designationRepository;
	
	@Autowired
	private BranchRepository branchRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ShiftRepository shiftRepository;
	
	public Map<String, Department> getDepartment(){
		List<Department> departmentList = departmentRepository.findAllByIsDeletedFalse();
		Map<String, Department> deptMap = new HashMap<String, Department>();
		
		for(Department department: departmentList ) {
			deptMap.put(department.getName(), department);
		}
		return deptMap;
	}
	
	
	public Map<String, Designation> getDesignation(){
		
		List<Designation> desigList = designationRepository.findAllByIsDeletedFalse();
		Map<String, Designation> desigMap = new HashMap<String, Designation>();
		
		
		for(Designation designation: desigList) {
			desigMap.put(designation.getName(), designation);
		}
		
		return desigMap;
	}
	
	public Map<String, Branch> getBranch(){
		
		List<Branch> branchList = branchRepository.findAllByIsDeletedFalse();
		Map<String, Branch> branchMap = new HashMap<String, Branch>();
		
		
		for(Branch branch: branchList) {
			branchMap.put(branch.getName(), branch);
		}
		
		return branchMap;
	}
	
	public Map<Long, Employee> getDeletedEmployee(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedTrueAndIsSyncTrue();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}
	public Map<Long, Employee> getEmployee(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}
	public Map<Long, Employee> getEmployeeByIsDeletedFalseAndIsSyncFalse(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncFalse();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}


	public Map<Long, Employee> getEmployeeByIsDeletedFalseAndIsSyncTrue() {
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncTrue();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}
	
	public Map<String, Employee> getEmployeeByEmpId(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getEmpId(), employee);
		}
		return employeeMap;
	}
	
	public Map<String, Employee> getEmployeeByEmpName(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getName(), employee);
		}
		return employeeMap;
	}
	
	public Map<String, Shift> getShiftByName(){
		List<Shift> shiftList = shiftRepository.findAllByIsDeletedFalse();
		Map<String, Shift> shiftMap = new HashMap<String, Shift>();
		
		for(Shift shift: shiftList ) {
			shiftMap.put(shift.getName(), shift);
		}
		return shiftMap;
	}
}
