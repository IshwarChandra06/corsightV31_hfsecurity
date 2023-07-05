package com.eikona.mata.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DefaultConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Department;
import com.eikona.mata.entity.Designation;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.OrganizationRepository;

@Component
public class ExcelEmployeeImport {
	@Autowired
	private DepartmentRepository departmentrepository;
	
	@Autowired
	private BranchRepository branchrepository;
	
	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private DesignationRepository designationrepository;

	@Autowired
	private OrganizationRepository organizationrepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	
	
	public Employee excelRowToEmployee(Row currentRow,Organization org,Map<String, Branch> branchMap,Map<String, Designation> designationMap,Map<String, Department> deptMap) {
		
		Employee employeeObj = null;
		
		Iterator<Cell> cellsInRow = currentRow.iterator();
		int cellIndex = NumberConstants.ZERO;
		employeeObj = new Employee();

		

		while (cellsInRow.hasNext()) {
			Cell currentCell = cellsInRow.next();

			if (null == employeeObj) {
				break;
			}

			else if (cellIndex == NumberConstants.ZERO) {
				setEmpId(employeeObj, currentCell);
				employeeObj.setOrganization(org);
			} else if (cellIndex == NumberConstants.ONE) {
				employeeObj.setName(currentCell.getStringCellValue());
			}
			else if (cellIndex == NumberConstants.TWO) {
				setBranch(org, branchMap, employeeObj, currentCell);
			}
			else if (cellIndex == NumberConstants.THREE) {
				setArea(org, employeeObj, currentCell);
			}
			else if (cellIndex == NumberConstants.FOUR) {
				setDepartment(org, deptMap, employeeObj, currentCell);
			}

			else if (cellIndex == NumberConstants.FIVE) {

				setDesignation(org, designationMap, employeeObj, currentCell);
			}

			else if (cellIndex == NumberConstants.SIX) {
				setMobile(employeeObj, currentCell);
			}

			cellIndex++;
		}
		return employeeObj;
		
	}
	
	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private void setMobile(Employee employeeObj, Cell currentCell) {
		currentCell.setCellType(CellType.STRING);
		if (currentCell.getCellType() == CellType.NUMERIC) {
			employeeObj.setMobile(String.valueOf(currentCell.getNumericCellValue()));
		} else if (currentCell.getCellType() == CellType.STRING) {
			employeeObj.setMobile(currentCell.getStringCellValue());
		}
	}

	private void setDesignation(Organization org, Map<String, Designation> designationMap, Employee employeeObj,
			Cell currentCell) {
		String str = currentCell.getStringCellValue();
		if (null != str && !str.isEmpty()) {
			
			Designation designation = designationMap.get(str);
			if (null == designation) {
				designation = new Designation();
				designation.setName(str);
				designation.setOrganization(org);
				designationrepository.save(designation);
				designationMap.put(designation.getName(), designation);

			}
			employeeObj.setDesignation(designation);
		}
	}

	private void setDepartment(Organization org, Map<String, Department> deptMap, Employee employeeObj,
			Cell currentCell) {
		String str = currentCell.getStringCellValue();

		if (null != str && !str.isEmpty()) {
			
			Department department = deptMap.get(str);
			if (null == department) {
				department = new Department();
				department.setName(str);
				department.setOrganization(org);
				departmentrepository.save(department);
				deptMap.put(department.getName(), department);
			}
			employeeObj.setDepartment(department);
		}
	}

	private void setArea(Organization org, Employee employeeObj, Cell currentCell) {
		if(null!=employeeObj.getBranch()) {
		String str = currentCell.getStringCellValue();
		if (null != str && !str.isEmpty()) {
			String[] areaList = str.split(ApplicationConstants.DELIMITER_COMMA);
			List<Area> areaEmployeeList=new ArrayList<Area>();
			for(String area:areaList) {
				Area areaObj=areaRepository.findByNameAndBranchAndIsDeletedFalse(area,employeeObj.getBranch());
				if(null==areaObj) {
					areaObj=new Area();
					areaObj.setName(area);
					
					areaObj.setBranch(employeeObj.getBranch());
					areaObj.setOrganization(org);
					areaRepository.save(areaObj);
				}
				areaEmployeeList.add(areaObj);
				
			}
			
			employeeObj.setArea(areaEmployeeList);
		}
  }
	}

	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private void setEmpId(Employee employeeObj, Cell currentCell) {
		currentCell.setCellType(CellType.STRING);
		if (currentCell.getCellType() == CellType.NUMERIC) {
			employeeObj.setEmpId(String.valueOf(currentCell.getNumericCellValue()));
		} else if (currentCell.getCellType() == CellType.STRING) {
			employeeObj.setEmpId(currentCell.getStringCellValue());
		}
	}

	private void setBranch(Organization org, Map<String, Branch> branchMap, Employee employeeObj, Cell currentCell) {
		String str = currentCell.getStringCellValue();

		if (null != str && !str.isEmpty()) {
			
			Branch branch =  branchMap.get(str);
			
			if (null == branch) {
				branch = new Branch();
				branch.setName(str);
				branch.setOrganization(org);
				branchrepository.save(branch);
				branchMap.put(branch.getName(), branch);
			}
			employeeObj.setBranch(branch);
		}
	}
	public List<Employee> parseExcelFileEmployeeList(InputStream inputStream, Organization org) {
		List<Employee> employeeList = new ArrayList<Employee>();
		try {

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(NumberConstants.ZERO);

			Iterator<Row> rows = sheet.iterator();

			

			int rowNumber = NumberConstants.ZERO;
			Map<String, Branch> branchMap = employeeObjectMap.getBranch();
			Map<String, Designation> designationMap = employeeObjectMap.getDesignation();
			Map<String, Department> deptMap = employeeObjectMap.getDepartment();
			List<String> empIdList=employeeRepository.getEmpIdAndIsDeletedFalseCustom();
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == NumberConstants.ZERO) {
					rowNumber++;
					continue;
				}

				rowNumber++;
				
				Employee employee=excelRowToEmployee(currentRow,org,branchMap,designationMap,deptMap);
				
				boolean isContains=empIdList.contains(employee.getEmpId());
				
				if(!isContains && null!=employee.getName() && !employee.getName().isEmpty() && null!=employee.getEmpId()&& !employee.getEmpId().isEmpty())
				employeeList.add(employee);
				
				if(rowNumber%NumberConstants.HUNDRED==NumberConstants.ZERO) {
					employeeRepository.saveAll(employeeList);
					employeeList.clear();
				}
					
					
			}
			
			if(!employeeList.isEmpty()) {
				employeeRepository.saveAll(employeeList);
				employeeList.clear();
			}
			
			workbook.close();

			return employeeList;
		} catch (IOException e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage());
		}
	}
	
	
public Employee cosecExcelRowToEmployee(Row currentRow,Organization org) {
		
		Employee employeeObj = null;
		
		Iterator<Cell> cellsInRow = currentRow.iterator();
		int cellIndex = NumberConstants.ZERO;
		employeeObj = new Employee();
		while (cellsInRow.hasNext()) {
			Cell currentCell = cellsInRow.next();

			if (null == employeeObj) {
				break;
			}

			else if (cellIndex == NumberConstants.ZERO) {
				setEmpId(employeeObj, currentCell);
			} else if (cellIndex == NumberConstants.ONE) {
				employeeObj.setName(currentCell.getStringCellValue());
				employeeObj.setOrganization(org);
			}

			cellIndex++;
		}
		return employeeObj;
		
	}
	public List<Employee> parseCosecExcelFileEmployeeList(InputStream inputStream, Organization org) throws InvalidFormatException {
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
		//OPCPackage pkg = OPCPackage.open(inputStream);

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(NumberConstants.ZERO);

			Iterator<Row> rows = sheet.iterator();

			

			int rowNumber = NumberConstants.ZERO;
			List<String> empIdList=employeeRepository.getEmpIdAndIsDeletedFalseCustom();
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == NumberConstants.ZERO) {
					rowNumber++;
					continue;
				}

				rowNumber++;
				
				Employee employee=cosecExcelRowToEmployee(currentRow,org);
				
				boolean isContains=empIdList.contains(employee.getEmpId());
				
				if(!isContains && null!=employee.getName() && !employee.getName().isEmpty() && null!=employee.getEmpId()&& !employee.getEmpId().isEmpty())
				employeeList.add(employee);
				
				if(rowNumber%NumberConstants.HUNDRED==NumberConstants.ZERO) {
					employeeRepository.saveAll(employeeList);
					employeeList.clear();
				}
					
					
			}
			
			if(!employeeList.isEmpty()) {
				employeeRepository.saveAll(employeeList);
				employeeList.clear();
			}
			
			workbook.close();

			return employeeList;
		} catch (IOException e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage());
		}
	}

	
	
}
