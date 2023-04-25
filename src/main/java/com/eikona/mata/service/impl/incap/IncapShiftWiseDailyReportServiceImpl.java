package com.eikona.mata.service.impl.incap;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DailyAttendanceConstants;
import com.eikona.mata.constants.IncapConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeLeaveAssociation;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.repository.EmployeeLeaveAssociationRepository;
import com.eikona.mata.repository.EmployeeShiftDailyAssociationRepository;
import com.eikona.mata.repository.ShiftRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.util.CalendarUtil;

@Component
public class IncapShiftWiseDailyReportServiceImpl {
	
	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ShiftRepository shiftRepository;

	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;

	@Autowired
	private EmployeeShiftDailyAssociationRepository employeeShiftDailyAssociationRepository;
	
	@Autowired
	private EmployeeLeaveAssociationRepository employeeLeaveDailyAssociationRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;

	public void generateDailyAttendanceShiftWise(String startDateStr, String endDateStr) {
		try {
			List<Shift> shiftList = (List<Shift>) shiftRepository.findAll();
			SimpleDateFormat fontformat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA_SPLIT_BY_SLASH);
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);

			Date startDate = fontformat.parse(startDateStr);

			startDateStr = format.format(startDate);
			startDate = format.parse(startDateStr);

			Date endDate = fontformat.parse(endDateStr);

			endDateStr = format.format(endDate);
			endDate = format.parse(endDateStr);

			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startDate);

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(endDate);
			endCalendar.add(Calendar.DATE, NumberConstants.ONE);

			while (startCalendar.before(endCalendar)) {

				for (Shift shift : shiftList) {
					calculateDaywiseDailyAttendance(shift, startCalendar);
				}
				startCalendar.add(Calendar.DATE, NumberConstants.ONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateDaywiseDailyAttendance(Shift shift, Calendar currentCalendar) {

		if (null != shift.getStartTime() && null != shift.getEndTime()) {

			Calendar shiftStartCheckInTime = calendarUtil.getStartCalenderFromShift(shift, currentCalendar, -NumberConstants.ONE);
					
			Calendar shiftEndCheckOutTime = calendarUtil.getEndCalenderFromShift(shift, currentCalendar, NumberConstants.ONE);
					Calendar.getInstance();

			List<EmployeeShiftDailyAssociation> employeeShiftDailyAssociations = employeeShiftDailyAssociationRepository
					.findEmployeeByDateAndShiftCustom(shift.getName(), currentCalendar.getTime());

			List<Transaction> minTransaction = transactionRepository
					.getMinTransactionByTimeIntervalCustom(shiftStartCheckInTime.getTime(), shiftEndCheckOutTime.getTime());

			List<Transaction> maxTransaction = transactionRepository.
					getMaxTransactionByTimeIntervalCustom(shiftStartCheckInTime.getTime(), shiftEndCheckOutTime.getTime());

			calculateShiftwiseDailyAttendance(employeeShiftDailyAssociations, minTransaction, maxTransaction, currentCalendar.getTime(), shift);

		}
	}

	private void calculateShiftwiseDailyAttendance(List<EmployeeShiftDailyAssociation> employeeShiftDailyAssociations,
			List<Transaction> minTransaction, List<Transaction> maxTransaction, Date currentDate, Shift shift) {
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(currentDate);

		Calendar firstHalfInTime = calendarUtil.getStartCalenderFromShift(shift, currentCalendar, -NumberConstants.ONE);
				
		Calendar firstHalfOutTime = calendarUtil.getStartCalenderFromShift(shift, currentCalendar, NumberConstants.ONE);
		
		Calendar secondHalfInTime = calendarUtil.getStartCalenderFromShift(shift, currentCalendar, NumberConstants.THREE);

		Calendar secondHalfOutTime = calendarUtil.getStartCalenderFromShift(shift, currentCalendar, NumberConstants.FIVE);
		
		try {

			SimpleDateFormat datePresent = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			Iterator<Transaction> mintranItr = minTransaction.iterator();
			Iterator<Transaction> maxtranItr = maxTransaction.iterator();
			Transaction mintran = null;
			Transaction maxtran = null;

			if (mintranItr.hasNext())
				mintran = mintranItr.next();
			if (maxtranItr.hasNext())
				maxtran = maxtranItr.next();

			List<DailyAttendance> dailyReportList = new ArrayList<DailyAttendance>();
			for (EmployeeShiftDailyAssociation employeeShiftDailyAssociation : employeeShiftDailyAssociations) {

				Employee employee = employeeShiftDailyAssociation.getEmployee();

				DailyAttendance dailyAttendance = new DailyAttendance();

				setEmployeeDetails(employee, dailyAttendance);

				dailyAttendance.setDate(currentDate);
				dailyAttendance.setDateStr(datePresent.format(currentDate));
				EmployeeLeaveAssociation employeeLeaveAssociation= employeeLeaveDailyAssociationRepository.findLeaveStatusByDateAndEmpIdCustom(employee.getEmpId(), currentDate);
				
				if (minTransaction.isEmpty() && maxTransaction.isEmpty()) {
					if(IncapConstants.FULL_DAY.equalsIgnoreCase(employeeLeaveAssociation.getShiftWiseleaveSelection())) {
						dailyAttendance.setFirstHalf(employeeLeaveAssociation.getLeave().getName());
						dailyAttendance.setSecondHalf(employeeLeaveAssociation.getLeave().getName());
					}
					else if(IncapConstants.FIRST_HALF.equalsIgnoreCase(employeeLeaveAssociation.getShiftWiseleaveSelection()))
						dailyAttendance.setFirstHalf(employeeLeaveAssociation.getLeave().getName());
					else if(IncapConstants.SECOND_HALF.equalsIgnoreCase(employeeLeaveAssociation.getShiftWiseleaveSelection()))
						dailyAttendance.setSecondHalf(employeeLeaveAssociation.getLeave().getName());
					else {
						dailyAttendance.setFirstHalf(IncapConstants.AB);
						dailyAttendance.setSecondHalf(IncapConstants.AB);
					}
						
					DailyAttendance dailyAttendanceTest = dailyAttendanceRepository.findByEmpIdAndDate(employee.getEmpId(), currentDate);
					if (null == dailyAttendanceTest) {
						dailyReportList.add(dailyAttendance);
					}
				} else {

					if ((employee.getEmpId().equalsIgnoreCase(mintran.getEmpId()))
							&& (employee.getEmpId().equalsIgnoreCase(maxtran.getEmpId()))) {
						
						if(mintran.getPunchDate().after(firstHalfInTime.getTime()) && mintran.getPunchDate().before(firstHalfOutTime.getTime()))
							   dailyAttendance.setFirstHalf(IncapConstants.PR);
						else {
							if(IncapConstants.FIRST_HALF.equalsIgnoreCase(employeeLeaveAssociation.getShiftWiseleaveSelection()))
								dailyAttendance.setFirstHalf(employeeLeaveAssociation.getLeave().getName());
							else
								dailyAttendance.setFirstHalf(IncapConstants.AB);
						}

						SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);

						String dateString = datePresent.format(currentDate);

						// set roster to daily attendance
						setRoster(shift, employee, dailyAttendance, sdf, dateString);
						// roster end

						dailyAttendance.setEmpInLocation(mintran.getDeviceName());
						dailyAttendance.setEmpInAccessType(mintran.getAccessType());
						dailyAttendance.setEmpInTemp(mintran.getTemperature());
						dailyAttendance.setEmpInTime(mintran.getPunchTimeStr());
						dailyAttendance.setEmpInMask((mintran.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);

						if (!mintran.getPunchTimeStr().equalsIgnoreCase(maxtran.getPunchTimeStr())) {
							
							if(maxtran.getPunchDate().after(secondHalfInTime.getTime()) && maxtran.getPunchDate().before(secondHalfOutTime.getTime())) {
								if(IncapConstants.SECOND_HALF.equalsIgnoreCase(employeeLeaveAssociation.getShiftWiseleaveSelection()))
									dailyAttendance.setSecondHalf(employeeLeaveAssociation.getLeave().getName());
								else
									dailyAttendance.setSecondHalf(IncapConstants.AB);
							} else
								dailyAttendance.setSecondHalf(IncapConstants.PR);
							
							
							dailyAttendance.setEmpOutAccessType(maxtran.getAccessType());
							dailyAttendance.setEmpOutTime(maxtran.getPunchTimeStr());
							dailyAttendance.setEmpOutTemp(maxtran.getTemperature());
							dailyAttendance.setEmpOutLocation(maxtran.getDeviceName());
							dailyAttendance.setEmpInMask((maxtran.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);

							setCalculateDataInDailyReport(mintran, maxtran, dailyAttendance);
							dailyAttendance.setMissedOutPunch( ApplicationConstants.FALSE);
						} else {
							dailyAttendance.setMissedOutPunch(ApplicationConstants.TRUE);
						}

						if (mintranItr.hasNext()) {
							mintran = mintranItr.next();
							maxtran = maxtranItr.next();
						}
					} else {
						dailyAttendance.setFirstHalf(IncapConstants.AB);
						dailyAttendance.setSecondHalf(IncapConstants.AB);
					}
					dailyReportList.add(dailyAttendance);
				}
			}

			dailyAttendanceRepository.saveAll(dailyReportList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setCalculateDataInDailyReport(Transaction mintran, Transaction maxtran,
			DailyAttendance dailyAttendance) {
		LocalTime empIn = LocalTime.parse(mintran.getPunchTimeStr());
		LocalTime empOut = LocalTime.parse(maxtran.getPunchTimeStr());

		Long workMin = empIn.until(empOut, ChronoUnit.MINUTES);
		Long workHr = empIn.until(empOut, ChronoUnit.HOURS);

		dailyAttendance.setWorkTime(String.valueOf(workHr) + ApplicationConstants.DELIMITER_COLON + String.valueOf(workMin % NumberConstants.SIXTY));
		LocalTime shiftIn = LocalTime.parse(dailyAttendance.getShiftInTime());
		LocalTime shiftOut = LocalTime.parse(dailyAttendance.getShiftOutTime());

		Long lateComing = shiftIn.until(empIn, ChronoUnit.MINUTES);
		Long earlyComing = empIn.until(shiftIn, ChronoUnit.MINUTES);

		Long lateGoing = shiftOut.until(empOut, ChronoUnit.MINUTES);
		Long earlyGoing = empOut.until(shiftOut, ChronoUnit.MINUTES);

		Long shiftMin = shiftIn.until(shiftOut, ChronoUnit.MINUTES);

		dailyAttendance.setWorkTime(String.valueOf(workHr) + ApplicationConstants.DELIMITER_COLON + String.valueOf(workMin % NumberConstants.SIXTY));

		Long overTime = workMin - shiftMin;

		if (overTime > NumberConstants.ZERO) {
			dailyAttendance.setOverTime(overTime);
		}

		if (lateGoing > NumberConstants.ZERO) {
			dailyAttendance.setLateGoing(lateGoing);
		} else if (earlyGoing > NumberConstants.ZERO) {
			dailyAttendance.setEarlyGoing(earlyGoing);
		}

		if (lateComing > NumberConstants.ZERO) {
			dailyAttendance.setLateComing(lateComing);
		} else if (earlyComing > NumberConstants.ZERO) {
			dailyAttendance.setEarlyComing(earlyComing);
		}
	}

	private void setRoster(Shift shift, Employee employee, DailyAttendance dailyAttendance, SimpleDateFormat sdf,
			String dateString) {
		EmployeeShiftDailyAssociation roster = employeeShiftDailyAssociationRepository.findByDateAndEmpIdCustom(dateString, employee.getEmpId());
		if (null == roster) {
			dailyAttendance.setAsPerRoster(DailyAttendanceConstants.NOT_ALLOTED);
		} else {

			if (IncapConstants.WEEK_OFF.equalsIgnoreCase(roster.getShift().getName().trim())) {
				dailyAttendance.setShift(roster.getShift().getName());
				dailyAttendance.setAsPerRoster(IncapConstants.WEEK_OFF);

			} else if ((roster.getShift().getName()).equalsIgnoreCase(shift.getName())) {
				dailyAttendance.setShift(shift.getName());
				dailyAttendance.setAsPerRoster(ApplicationConstants.YES);
				dailyAttendance.setShiftInTime(sdf.format(roster.getShift().getStartTime()));
				dailyAttendance.setShiftOutTime(sdf.format(roster.getShift().getEndTime()));
			} else {
				dailyAttendance.setShift(shift.getName());
				dailyAttendance.setAsPerRoster(ApplicationConstants.NO);
				dailyAttendance.setShiftInTime(sdf.format(shift.getStartTime()));
				dailyAttendance.setShiftOutTime(sdf.format(shift.getEndTime()));
			}

		}
	}

	private void setEmployeeDetails(Employee employee, DailyAttendance dailyAttendance) {
		if (null != employee.getOrganization()) {
			dailyAttendance.setOrganization(employee.getOrganization().getName());
		}
		if ((null != employee.getDepartment())) {
			dailyAttendance.setDepartment(employee.getDepartment().getName());
		}
		if ((null != employee.getDesignation())) {
			dailyAttendance.setDesignation(employee.getDesignation().getName());
		}
		if ((null != employee.getBranch())) {
			dailyAttendance.setBranch(employee.getBranch().getName());
		}

		dailyAttendance.setEmpId(employee.getEmpId());
		dailyAttendance.setEmployeeName(employee.getName());
	}
}
