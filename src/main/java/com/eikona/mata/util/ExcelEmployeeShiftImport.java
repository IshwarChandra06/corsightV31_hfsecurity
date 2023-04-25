package com.eikona.mata.util;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.ShiftRosterValidationDto;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.repository.EmployeeShiftDailyAssociationRepository;

@Component
public class ExcelEmployeeShiftImport {

	@Autowired
	private EmployeeObjectMap employeeObjectMap;

	@Autowired
	private EmployeeShiftDailyAssociationRepository employeeShiftDailyAssociationRepository;

	@SuppressWarnings(ApplicationConstants.RESOURCE)
	public ShiftRosterValidationDto parseExcelFileEmployeeShiftList(InputStream inputStream) {
		try {
			Map<String, Employee> empIdMap = employeeObjectMap.getEmployeeByEmpId();
			Map<String, Employee> empNameMap = employeeObjectMap.getEmployeeByEmpName();
			Map<String, Shift> shiftMap = employeeObjectMap.getShiftByName();
			Set<String> mismatchShiftRow = new HashSet<String>();
			Set<String> mismatchEmployeeRow = new HashSet<String>();
			Set<Integer> emptyEmployeeIdRow = new HashSet<Integer>();
			Set<Integer> emptyEmployeeNameRow = new HashSet<Integer>();

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(NumberConstants.ZERO);
			Iterator<Row> rows = sheet.iterator();

			Map<Integer, Date> dateMap = new HashMap<>();
			setDateMapFromExcelHeader(sheet, dateMap);

			int rowNumber = NumberConstants.ZERO;
			List<EmployeeShiftDailyAssociation> employeeShiftDailyAssociationList = new ArrayList<EmployeeShiftDailyAssociation>();
			List<String> idDateList = new ArrayList<String>();
			Map<String, EmployeeShiftDailyAssociation> rosterDataMap = new HashMap<>();
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber <=NumberConstants.ONE) {
					rowNumber++;
					continue;
				}

				rowNumber++;

				Iterator<Cell> cellsInRow = currentRow.iterator();
				int cellIndex = NumberConstants.ZERO;

				String empId = null;
				String empName = null;
				Employee emp = null;
				Employee empNameObj = null;

				List<EmployeeShiftDailyAssociation> employeeRosterList = new ArrayList<EmployeeShiftDailyAssociation>();
				while (cellsInRow.hasNext()) {

					Cell currentCell = cellsInRow.next();

					if (cellIndex == NumberConstants.ONE) {
						empId = getEmpIdFromExcelCell(empId, currentCell);
						
						emp = empIdMap.get(empId);
						if (null == empId || empId.isEmpty())
							emptyEmployeeIdRow.add(currentRow.getRowNum());
						if (null == emp && null != empId && !empId.isEmpty())
							mismatchEmployeeRow.add(empId);
					}
					if (cellIndex == NumberConstants.TWO) {
						empName = getEmpNameFromExcelCell(empName, currentCell);
						
						empNameObj=empNameMap.get(empName);
						if (null == empName || empName.isEmpty())
							emptyEmployeeNameRow.add(currentRow.getRowNum());
						
					}

					else if (cellIndex == NumberConstants.SEVEN) {
						setEmployeeRosterInfo(shiftMap, mismatchShiftRow, dateMap,  emp, empNameObj,
								employeeRosterList, currentCell);
					} else if (cellIndex == NumberConstants.EIGHT) {
						setEmployeeRosterInfo(shiftMap, mismatchShiftRow, dateMap,  emp, empNameObj,
								employeeRosterList, currentCell);
					} else if (cellIndex == NumberConstants.NINE) {
						setEmployeeRosterInfo(shiftMap, mismatchShiftRow, dateMap,  emp, empNameObj,
								employeeRosterList, currentCell);
					} else if (cellIndex == NumberConstants.TEN) {
						setEmployeeRosterInfo(shiftMap, mismatchShiftRow, dateMap,  emp, empNameObj,
								employeeRosterList, currentCell);
					} else if (cellIndex == NumberConstants.ELEVEN) {
						setEmployeeRosterInfo(shiftMap, mismatchShiftRow, dateMap,  emp, empNameObj,
								employeeRosterList, currentCell);
					}

					cellIndex++;
				}
                //Filter the employee who are already assigned with same date and update the shift of employee if it is again changed.
				setFilterEmployeeRosterList(employeeShiftDailyAssociationList, idDateList, rosterDataMap,
						employeeRosterList);

				if (rowNumber % NumberConstants.FIVE_HUNDRED == NumberConstants.ZERO) {
					employeeShiftDailyAssociationRepository.saveAll(employeeShiftDailyAssociationList);
					employeeShiftDailyAssociationList.clear();
				}
			}

			if (!employeeShiftDailyAssociationList.isEmpty()) {
				employeeShiftDailyAssociationRepository.saveAll(employeeShiftDailyAssociationList);
				employeeShiftDailyAssociationList.clear();
			}
			
           //Save Shift unassigned Employee Which are already in Db.
			saveShiftUnassignedAccordToDbEmployee(empIdMap, dateMap);
			
			//Get the dto having List of Mismatch employee ,mismatch shift and empty empId and empty name.
			ShiftRosterValidationDto shiftRosterValidationDto = getEmployeeRosterValidationList(mismatchShiftRow,
					mismatchEmployeeRow, emptyEmployeeIdRow, emptyEmployeeNameRow);
			
			return shiftRosterValidationDto;
		} catch (Exception e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage());
		}

	}

	private void setDateMapFromExcelHeader(Sheet sheet, Map<Integer, Date> dateMap) {
		Row firstRow = sheet.getRow(NumberConstants.ONE);
		int j = NumberConstants.ZERO, index = NumberConstants.ZERO;
		try {
			for (Cell r : firstRow) {
				if (index <= NumberConstants.SIX || j > NumberConstants.ELEVEN) {
					index++;
					j++;
					continue;

				}
				Date value = r.getDateCellValue();
				dateMap.put(j, value);
				j++;
			}
		} catch (Exception e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage());
		}
	}

	private void saveShiftUnassignedAccordToDbEmployee(Map<String, Employee> empIdMap, Map<Integer, Date> rowData) {
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);

		Calendar start = Calendar.getInstance();
		start.setTime(rowData.get(NumberConstants.SEVEN));

		Calendar end = Calendar.getInstance();
		end.setTime(rowData.get(NumberConstants.ELEVEN));
		List<EmployeeShiftDailyAssociation> employeeShiftList = new ArrayList<EmployeeShiftDailyAssociation>();
		while (!start.after(end)) {
			Date date = start.getTime();

			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(date);

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(date);
			endCalendar.add(Calendar.DATE, NumberConstants.ONE);

			List<String> employeeIdShiftDailyAssociationList = employeeShiftDailyAssociationRepository
					.findByDateRangeCustom(startCalendar.getTime(), endCalendar.getTime());

			if (null != employeeIdShiftDailyAssociationList && !(employeeIdShiftDailyAssociationList.isEmpty())) {
				for (String empId : empIdMap.keySet()) {

					if (!employeeIdShiftDailyAssociationList.contains(empId)) {
						EmployeeShiftDailyAssociation employeeShiftDailyAssociation = new EmployeeShiftDailyAssociation();
						employeeShiftDailyAssociation.setDate(startCalendar.getTime());
						employeeShiftDailyAssociation.setDateStr(dateFormat.format(startCalendar.getTime()));
						employeeShiftDailyAssociation.setEmployee(empIdMap.get(empId));
						employeeShiftDailyAssociation.setShift(null);

						employeeShiftList.add(employeeShiftDailyAssociation);
					}
				}
			}

			start.add(Calendar.DATE, NumberConstants.ONE);
		}

		employeeShiftDailyAssociationRepository.saveAll(employeeShiftList);
	}

	private void setFilterEmployeeRosterList(List<EmployeeShiftDailyAssociation> employeeShiftDailyAssociationList,
			List<String> idDateList, Map<String, EmployeeShiftDailyAssociation> rosterDataMap,
			List<EmployeeShiftDailyAssociation> employeeRosterList) {
		for (EmployeeShiftDailyAssociation employeeShiftObj : employeeRosterList) {
			EmployeeShiftDailyAssociation employeeShift = employeeShiftDailyAssociationRepository.findByDateAndEmpIdCustom(employeeShiftObj.getDateStr(),
							employeeShiftObj.getEmployee().getEmpId());
			boolean isContainIdDate = idDateList.contains(employeeShiftObj.getDateStr() + ApplicationConstants.DELIMITER_HYPHEN + employeeShiftObj.getEmployee().getEmpId());
			if (null == employeeShift) {
				if (!isContainIdDate)
					employeeShiftDailyAssociationList.add(employeeShiftObj);
				else {
					employeeShiftDailyAssociationList.remove(rosterDataMap.get(
							employeeShiftObj.getDateStr() + ApplicationConstants.DELIMITER_HYPHEN + employeeShiftObj.getEmployee().getEmpId()));
					employeeShiftDailyAssociationList.add(employeeShiftObj);
				}

			} else {
				employeeShift.setShift(employeeShiftObj.getShift());
				employeeShiftDailyAssociationList.add(employeeShift);
			}
			idDateList.add(employeeShiftObj.getDateStr() + ApplicationConstants.DELIMITER_HYPHEN + employeeShiftObj.getEmployee().getEmpId());
			rosterDataMap.put(employeeShiftObj.getDateStr() + ApplicationConstants.DELIMITER_HYPHEN + employeeShiftObj.getEmployee().getEmpId(),
					employeeShiftObj);
		}
	}

	private ShiftRosterValidationDto getEmployeeRosterValidationList(Set<String> mismatchShiftRow,
			Set<String> mismatchEmployeeRow, Set<Integer> emptyEmployeeIdRow, Set<Integer> emptyEmployeeNameRow) {
		String emptyEmployeeId=ApplicationConstants.DELIMITER_EMPTY;
		String emptyEmployeeName=ApplicationConstants.DELIMITER_EMPTY;
		String mismatchEmployee=ApplicationConstants.DELIMITER_EMPTY;
		String mismatchShift=ApplicationConstants.DELIMITER_EMPTY;
		
		ShiftRosterValidationDto shiftRosterValidationDto = new ShiftRosterValidationDto();
		emptyEmployeeId = setEmptyEmployeeDetails(emptyEmployeeIdRow, emptyEmployeeId);
		emptyEmployeeName = setEmptyEmployeeDetails(emptyEmployeeNameRow, emptyEmployeeName);
		mismatchEmployee = setMismatchEmployee(mismatchEmployeeRow, mismatchEmployee);
		mismatchShift = setMismatchShift(mismatchShiftRow, mismatchShift);
		
		shiftRosterValidationDto.setEmptyEmployeeId(emptyEmployeeId);
		shiftRosterValidationDto.setEmptyEmployeeName(emptyEmployeeName);
		shiftRosterValidationDto.setMismatchEmployee(mismatchEmployee);
		shiftRosterValidationDto.setMismatchShift(mismatchShift);
		return shiftRosterValidationDto;
	}

	private String setMismatchShift(Set<String> mismatchShiftRow, String mismatchShift) {
		if(null==mismatchShiftRow || mismatchShiftRow.isEmpty())
			mismatchShift=ApplicationConstants.DELIMITER_EMPTY;
		else {
			for(String shift:mismatchShiftRow) {
				shift = shift.trim();
					if(mismatchShift.isEmpty() )
						mismatchShift=shift;
					else {
						mismatchShift+=ApplicationConstants.DELIMITER_COMMA+shift;
					}
				
			}
		}
		return mismatchShift;
	}

	private String setMismatchEmployee(Set<String> mismatchEmployeeRow, String mismatchEmployee) {
		if(null==mismatchEmployeeRow || mismatchEmployeeRow.isEmpty())
			mismatchEmployee=ApplicationConstants.DELIMITER_EMPTY;
		else {
			for(String employeeId:mismatchEmployeeRow) {
				if(mismatchEmployee.isEmpty())
					mismatchEmployee=employeeId;
				else {
					mismatchEmployee+=ApplicationConstants.DELIMITER_COMMA+employeeId;
				}
			}
		}
		return mismatchEmployee;
	}

	private String setEmptyEmployeeDetails(Set<Integer> emptyRow, String emptyEmployeeData) {
		if(null==emptyRow || emptyRow.isEmpty())
			emptyEmployeeData=ApplicationConstants.DELIMITER_EMPTY;
		else {
			for(int row:emptyRow) {
				if(emptyEmployeeData.isEmpty())
					emptyEmployeeData=String.valueOf(row);
				else {
					emptyEmployeeData+=ApplicationConstants.DELIMITER_COMMA+String.valueOf(row);
				}
			}
			
				
		}
		return emptyEmployeeData;
	}

	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private String getEmpNameFromExcelCell(String empName, Cell currentCell) {
		currentCell.setCellType(CellType.STRING);
		if (currentCell.getCellType() == CellType.NUMERIC) {
			empName = String.valueOf(currentCell.getNumericCellValue()).trim();

		} else if (currentCell.getCellType() == CellType.STRING) {
			empName = currentCell.getStringCellValue().trim();
		}
		return empName;
	}
	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private String getEmpIdFromExcelCell(String empId, Cell currentCell) {
		currentCell.setCellType(CellType.STRING);
		if (currentCell.getCellType() == CellType.NUMERIC) {
			empId = String.valueOf(currentCell.getNumericCellValue()).trim().replaceAll(ApplicationConstants.DELIMITER_HTML_SPACE, ApplicationConstants.DELIMITER_EMPTY);

		} else if (currentCell.getCellType() == CellType.STRING) {
			empId = currentCell.getStringCellValue().trim().replaceAll(ApplicationConstants.DELIMITER_HTML_SPACE, ApplicationConstants.DELIMITER_EMPTY);
		}
		return empId;
	}
	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private void setEmployeeRosterInfo(Map<String, Shift> shiftMap, Set<String> mismatchShiftRow,
			Map<Integer, Date> rowData, Employee emp, Employee empNameObj,
			List<EmployeeShiftDailyAssociation> employeeRosterList, Cell currentCell) {
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
		String shiftName = null;
		currentCell.setCellType(CellType.STRING);
		if (currentCell.getCellType() == CellType.NUMERIC) {
			shiftName = String.valueOf(currentCell.getNumericCellValue());

		} else if (currentCell.getCellType() == CellType.STRING) {
			shiftName = currentCell.getStringCellValue();
		}
		Shift shift = shiftMap.get(shiftName);

		employeeShiftRoster.setDate(rowData.get(NumberConstants.EIGHT));
		employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(NumberConstants.EIGHT)));
		employeeShiftRoster.setEmployee(emp);
		if (null != shift)
			employeeShiftRoster.setShift(shift);
		else {
			employeeShiftRoster.setShift(null);
			if(!(shiftName.isEmpty()) && null!=shiftName)
				mismatchShiftRow.add(shiftName);
			}

		if (null != emp && null!=empNameObj)
			employeeRosterList.add(employeeShiftRoster);
	}

	

}
