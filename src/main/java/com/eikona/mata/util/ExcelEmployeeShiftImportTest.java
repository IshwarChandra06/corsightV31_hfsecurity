//package com.eikona.mata.util;
//
//import java.io.InputStream;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.eikona.mata.dto.ShiftRosterValidationDto;
//import com.eikona.mata.entity.Employee;
//import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
//import com.eikona.mata.entity.Shift;
//import com.eikona.mata.repository.EmployeeShiftDailyAssociationRepository;
//
//@Component
//public class ExcelEmployeeShiftImportTest {
//
//	@Autowired
//	private EmployeeObjectMap employeeObjectMap;
//
//	@Autowired
//	private EmployeeShiftDailyAssociationRepository employeeShiftDailyAssociationRepository;
//
//	@SuppressWarnings({ "deprecation", "resource", "unchecked" })
//	public ShiftRosterValidationDto parseExcelFileEmployeeShiftList(InputStream inputStream) {
//		try {
//			Map<String, Employee> empIdMap = employeeObjectMap.getEmployeeByEmpId();
//			Map<String, Employee> empNameMap = employeeObjectMap.getEmployeeByEmpName();
//			Map<String, Shift> shiftMap = employeeObjectMap.getShiftByName();
//			Set<String> mismatchShiftRow = new HashSet<String>();
//			Set<String> mismatchEmployeeRow = new HashSet<String>();
//			Set<Integer> emptyEmployeeIdRow = new HashSet<Integer>();
//			Set<Integer> emptyEmployeeNameRow = new HashSet<Integer>();
//
//			Workbook workbook = new XSSFWorkbook(inputStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			Iterator<Row> rows = sheet.iterator();
//
//			Map<Integer, Date> rowData = new HashMap<>();
//			Row firstRow = sheet.getRow(1);
//			int j = 0, index = 0;
//			try {
//				for (Cell r : firstRow) {
//					if (index <= 6 || j > 11) {
//						index++;
//						j++;
//						continue;
//
//					}
//					Date value = r.getDateCellValue();
//					rowData.put(j, value);
//					j++;
//				}
//			} catch (Exception e) {
//				throw new RuntimeException("FAILED! -> message = " + e.getMessage());
//			}
//
//			int rowNumber = 0;
//			List<EmployeeShiftDailyAssociation> employeeShiftDailyAssociationList = new ArrayList<EmployeeShiftDailyAssociation>();
//			while (rows.hasNext()) {
//				Row currentRow = rows.next();
//
//				// skip header
//				if (rowNumber <= 1) {
//					rowNumber++;
//					continue;
//				}
//
//				rowNumber++;
//
//				Iterator<Cell> cellsInRow = currentRow.iterator();
//				int cellIndex = 0;
//
//				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//				String empId = null;
//				String empName = null;
//				Employee emp = null;
//				Employee empNameObj = null;
//
//				List<EmployeeShiftDailyAssociation> employeeRosterList = new ArrayList<EmployeeShiftDailyAssociation>();
//				while (cellsInRow.hasNext()) {
//
//					Cell currentCell = cellsInRow.next();
//
//					if (cellIndex == 1) {
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							empId = String.valueOf(currentCell.getNumericCellValue()).trim().replaceAll("\u00A0", "");
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							empId = currentCell.getStringCellValue().trim().replaceAll("\u00A0", "");
//						}
//						emp = empIdMap.get(empId);
//						if (null == empId || empId.isEmpty())
//							emptyEmployeeIdRow.add(currentRow.getRowNum());
//						if (null == emp && null != empId && !empId.isEmpty())
//							mismatchEmployeeRow.add(empId);
//					}
//					if (cellIndex == 2) {
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							empName = String.valueOf(currentCell.getNumericCellValue()).trim();
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							empName = currentCell.getStringCellValue().trim();
//						}
//						empNameObj = empNameMap.get(empName);
//						if (null == empName || empName.isEmpty())
//							emptyEmployeeNameRow.add(currentRow.getRowNum());
//
//					}
//
//					else if (cellIndex == 7) {
//
//						EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
//						String shiftName = null;
//
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							shiftName = String.valueOf(currentCell.getNumericCellValue()).trim().replaceAll("\u00A0",
//									"");
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							shiftName = currentCell.getStringCellValue().trim().replaceAll("\u00A0", "");
//						}
//						Shift shift = shiftMap.get(shiftName);
//
//						employeeShiftRoster.setDate(rowData.get(7));
//						employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(7)));
//						employeeShiftRoster.setEmployee(emp);
//						if (null != shift)
//							employeeShiftRoster.setShift(shift);
//						else {
//							employeeShiftRoster.setShift(null);
//							if (!(shiftName.isEmpty()) && null != shiftName)
//								mismatchShiftRow.add(shiftName);
//						}
//						if (null != emp && null != empNameObj)
//							employeeRosterList.add(employeeShiftRoster);
//
//					} else if (cellIndex == 8) {
//						EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
//						String shiftName = null;
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							shiftName = String.valueOf(currentCell.getNumericCellValue());
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							shiftName = currentCell.getStringCellValue();
//						}
//						Shift shift = shiftMap.get(shiftName);
//
//						employeeShiftRoster.setDate(rowData.get(8));
//						employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(8)));
//						employeeShiftRoster.setEmployee(emp);
//						if (null != shift)
//							employeeShiftRoster.setShift(shift);
//						else {
//							employeeShiftRoster.setShift(null);
//							if (!(shiftName.isEmpty()) && null != shiftName)
//								mismatchShiftRow.add(shiftName);
//						}
//
//						if (null != emp && null != empNameObj)
//							employeeRosterList.add(employeeShiftRoster);
//					} else if (cellIndex == 9) {
//						EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
//						String shiftName = null;
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							shiftName = String.valueOf(currentCell.getNumericCellValue());
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							shiftName = currentCell.getStringCellValue();
//						}
//						Shift shift = shiftMap.get(shiftName);
//
//						employeeShiftRoster.setDate(rowData.get(9));
//						employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(9)));
//						employeeShiftRoster.setEmployee(emp);
//						if (null != shift)
//							employeeShiftRoster.setShift(shift);
//						else {
//							employeeShiftRoster.setShift(null);
//							if (!(shiftName.isEmpty()) && null != shiftName)
//								mismatchShiftRow.add(shiftName);
//						}
//						if (null != emp && null != empNameObj)
//							employeeRosterList.add(employeeShiftRoster);
//					} else if (cellIndex == 10) {
//						EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
//						String shiftName = null;
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							shiftName = String.valueOf(currentCell.getNumericCellValue());
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							shiftName = currentCell.getStringCellValue();
//						}
//						Shift shift = shiftMap.get(shiftName);
//
//						employeeShiftRoster.setDate(rowData.get(10));
//						employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(10)));
//						employeeShiftRoster.setEmployee(emp);
//						if (null != shift)
//							employeeShiftRoster.setShift(shift);
//						else {
//							employeeShiftRoster.setShift(null);
//							if (!(shiftName.isEmpty()) && null != shiftName)
//								mismatchShiftRow.add(shiftName);
//						}
//						if (null != emp && null != empNameObj)
//							employeeRosterList.add(employeeShiftRoster);
//					} else if (cellIndex == 11) {
//						EmployeeShiftDailyAssociation employeeShiftRoster = new EmployeeShiftDailyAssociation();
//						String shiftName = null;
//						currentCell.setCellType(CellType.STRING);
//						if (currentCell.getCellType() == CellType.NUMERIC) {
//							shiftName = String.valueOf(currentCell.getNumericCellValue());
//
//						} else if (currentCell.getCellType() == CellType.STRING) {
//							shiftName = currentCell.getStringCellValue();
//						}
//						Shift shift = shiftMap.get(shiftName);
//
//						employeeShiftRoster.setDate(rowData.get(11));
//						employeeShiftRoster.setDateStr(dateFormat.format(rowData.get(11)));
//						employeeShiftRoster.setEmployee(emp);
//						if (null != shift)
//							employeeShiftRoster.setShift(shift);
//						else {
//							employeeShiftRoster.setShift(null);
//							if (!(shiftName.isEmpty()) && null != shiftName)
//								mismatchShiftRow.add(shiftName);
//						}
//
//						if (null != emp && null != empNameObj)
//							employeeRosterList.add(employeeShiftRoster);
//
//					}
//
//					cellIndex++;
//				}
//				employeeShiftDailyAssociationList.addAll(employeeRosterList);
//			}
//			JSONArray jsonArray = new JSONArray();
//			for (EmployeeShiftDailyAssociation employeeShift : employeeShiftDailyAssociationList) {
//				JSONObject currentObject = new JSONObject();
//
//				currentObject.put("date", employeeShift.getDateStr());
//
//				if (null != employeeShift.getShift())
//					currentObject.put("shift", employeeShift.getShift().getName());
//				else
//					currentObject.put("shift", "");
//				if (null != employeeShift.getEmployee()) {
//					currentObject.put("empId", employeeShift.getEmployee().getEmpId());
//					currentObject.put("name", employeeShift.getEmployee().getName());
//					if (null != employeeShift.getEmployee().getDepartment())
//						currentObject.put("department", employeeShift.getEmployee().getDepartment().getName());
//					else
//						currentObject.put("department", "");
//				} else {
//					currentObject.put("empId", "");
//					currentObject.put("name", "");
//					currentObject.put("department", "");
//				}
//				jsonArray.add(currentObject);
//			}
//			System.out.println(jsonArray.toJSONString());
//			
//
//			String emptyEmployeeId = "";
//			String emptyEmployeeName = "";
//			String mismatchEmployee = "";
//			String mismatchShift = "";
//			ShiftRosterValidationDto shiftRosterValidationDto = new ShiftRosterValidationDto();
//			if (null == emptyEmployeeIdRow || emptyEmployeeIdRow.isEmpty())
//				emptyEmployeeId = "";
//			else {
//				for (int row : emptyEmployeeIdRow) {
//					if (emptyEmployeeId.isEmpty())
//						emptyEmployeeId = String.valueOf(row);
//					else {
//						emptyEmployeeId += "," + String.valueOf(row);
//					}
//				}
//
//			}
//			if (null == emptyEmployeeNameRow || emptyEmployeeNameRow.isEmpty())
//				emptyEmployeeName = "";
//			else {
//				for (int row : emptyEmployeeNameRow) {
//					if (emptyEmployeeName.isEmpty())
//						emptyEmployeeName = String.valueOf(row);
//					else {
//						emptyEmployeeName += "," + String.valueOf(row);
//					}
//				}
//			}
//			if (null == mismatchEmployeeRow || mismatchEmployeeRow.isEmpty())
//				mismatchEmployee = "";
//			else {
//				for (String employeeId : mismatchEmployeeRow) {
//					if (mismatchEmployee.isEmpty())
//						mismatchEmployee = employeeId;
//					else {
//						mismatchEmployee += "," + employeeId;
//					}
//				}
//			}
//			if (null == mismatchShiftRow || mismatchShiftRow.isEmpty())
//				mismatchShift = "";
//			else {
//				for (String shift : mismatchShiftRow) {
//					shift = shift.trim();
//					if (mismatchShift.isEmpty())
//						mismatchShift = shift;
//					else {
//						mismatchShift += "," + shift;
//					}
//
//				}
//			}
//			shiftRosterValidationDto.setEmptyEmployeeId(emptyEmployeeId);
//			shiftRosterValidationDto.setEmptyEmployeeName(emptyEmployeeName);
//			shiftRosterValidationDto.setMismatchEmployee(mismatchEmployee);
//			shiftRosterValidationDto.setMismatchShift(mismatchShift);
//			return shiftRosterValidationDto;
//		} catch (Exception e) {
//			throw new RuntimeException("FAILED! -> message = " + e.getMessage());
//		}
//
//	}
//
//}
