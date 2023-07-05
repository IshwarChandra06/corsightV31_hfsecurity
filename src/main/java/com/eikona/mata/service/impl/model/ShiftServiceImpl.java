package com.eikona.mata.service.impl.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.ShiftConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.dto.ShiftSettingDto;
import com.eikona.mata.entity.Parameter;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.repository.ParameterRepository;
import com.eikona.mata.repository.ShiftRepository;
import com.eikona.mata.service.ShiftService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class ShiftServiceImpl implements ShiftService {

	@Autowired
	private ShiftRepository shiftRepository;
	
	@Autowired
	private ParameterRepository parameterRepository;
	
	@Autowired
	private GeneralSpecificationUtil<Shift> generalSpecification;

	@Override
	public List<Shift> getAll() {
		return shiftRepository.findAllByIsDeletedFalse();
	}

	@Override
	public Shift save(Shift shift) {
		shift.setDeleted(false);
		return this.shiftRepository.save(shift);
	}

	@Override
	public Shift getById(long id) {
		Optional<Shift> optional = shiftRepository.findById(id);
		Shift shift = null;
		if (optional.isPresent()) {
			shift = optional.get();
		} else {
			throw new RuntimeException(ShiftConstants.SHIFT_NOT_FOUND + id);
		}
		return shift;
	}

	@Override
	public void deleteById(long id) {
		Optional<Shift> optional = shiftRepository.findById(id);
		Shift shift = null;
		if (optional.isPresent()) {
			shift = optional.get();
			shift.setDeleted(true);
		} else {
			throw new RuntimeException(ShiftConstants.SHIFT_NOT_FOUND + id);
		}
		this.shiftRepository.save(shift);
	}
	

	@Override
	public List<Parameter> saveShiftSetting(ShiftSettingDto shiftSettingDto) {
		try {
			List<Parameter> parameterList = new ArrayList<>();
		
			setName(shiftSettingDto, parameterList);
			
			setTimeTableType(shiftSettingDto, parameterList);
			
			checkInTime(shiftSettingDto, parameterList);
			
			setCheckOutTime(shiftSettingDto, parameterList);
			
			setBeforeGoingToWork(shiftSettingDto, parameterList);
			
			setBeforeGoingOffDuty(shiftSettingDto, parameterList);
			
			setAfterWork(shiftSettingDto, parameterList);
			
			setAfterDuty(shiftSettingDto, parameterList);
			
			setAllowLate(shiftSettingDto, parameterList);
			
			setAllowEarlyLeave(shiftSettingDto, parameterList);
			
			setMustCheckIn(shiftSettingDto, parameterList);
			
			setMustCheckOut(shiftSettingDto, parameterList);
			
			setWorkDay(shiftSettingDto, parameterList);
			
			setWorkTime(shiftSettingDto, parameterList);
			
			setAutoDeductBreakTime(shiftSettingDto, parameterList);
			
			setOnDuty(shiftSettingDto, parameterList);
			
			setOffDuty(shiftSettingDto, parameterList);
			
			setEnableFlexibleWork(shiftSettingDto, parameterList);
			
			
			parameterRepository.saveAll(parameterList);
			
			return parameterList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private void setEnableFlexibleWork(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter enableFlexibleWork = new Parameter();
		enableFlexibleWork.setName(ShiftConstants.SHIFT_ENABLE_FLEXIBLE_WORK);
		enableFlexibleWork.setValue(shiftSettingDto.getEnableFlexibleWork());
		enableFlexibleWork.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(enableFlexibleWork);
	}

	private void setOffDuty(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter offDuty = new Parameter();
		offDuty.setName(ShiftConstants.SHIFT_OFF_DUTY);
		offDuty.setValue(shiftSettingDto.getOffDuty());
		offDuty.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(offDuty);
	}

	private void setOnDuty(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter onDuty = new Parameter();
		onDuty.setName(ShiftConstants.SHIFT_ON_DUTY);
		onDuty.setValue(shiftSettingDto.getOnDuty());
		onDuty.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(onDuty);
	}

	private void setAutoDeductBreakTime(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter autoDeductBreakTime = new Parameter();
		autoDeductBreakTime.setName(ShiftConstants.SHIFT_AUTO_DEDUCT_BREAK_TIME);
		autoDeductBreakTime.setValue(shiftSettingDto.getAutoDeductBreakTime());
		autoDeductBreakTime.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(autoDeductBreakTime);
	}

	private void setWorkTime(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter workTime = new Parameter();
		workTime.setName(ShiftConstants.SHIFT_WORK_TIME);
		workTime.setValue(shiftSettingDto.getWorkTime());
		workTime.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(workTime);
	}

	private void setWorkDay(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter workDay = new Parameter();
		workDay.setName(ShiftConstants.SHIFT_WORK_DAY);
		workDay.setValue(shiftSettingDto.getWorkDay());
		workDay.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(workDay);
	}

	private void setMustCheckOut(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter mustCheckOut = new Parameter();
		mustCheckOut.setName(ShiftConstants.SHIFT_MUST_CHECK_OUT);
		mustCheckOut.setValue(shiftSettingDto.getMustCheckOut());
		mustCheckOut.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(mustCheckOut);
	}

	private void setMustCheckIn(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter mustCheckIn = new Parameter();
		mustCheckIn.setName(ShiftConstants.SHIFT_MUSH_CHECK_IN);
		mustCheckIn.setValue(shiftSettingDto.getMustCheckIn());
		mustCheckIn.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(mustCheckIn);
	}

	private void setAllowEarlyLeave(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter allowEarlyLeave = new Parameter();
		allowEarlyLeave.setName(ShiftConstants.SHIFT_ALLOW_EARLY_LEAVE);
		allowEarlyLeave.setValue(shiftSettingDto.getAllowEarlyLeave());
		allowEarlyLeave.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(allowEarlyLeave);
	}

	private void setAllowLate(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter allowLate = new Parameter();
		allowLate.setName(ShiftConstants.SHIFT_ALLOW_LATE);
		allowLate.setValue(shiftSettingDto.getAllowLate());
		allowLate.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(allowLate);
	}

	private void setAfterDuty(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter afterDuty = new Parameter();
		afterDuty.setName(ShiftConstants.SHIFT_AFTER_DUTY);
		afterDuty.setValue(shiftSettingDto.getAfterDuty());
		afterDuty.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(afterDuty);
	}

	private void setAfterWork(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter afterWork = new Parameter();
		afterWork.setName(ShiftConstants.SHIFT_AFTER_WORK);
		afterWork.setValue(shiftSettingDto.getAfterWork());
		afterWork.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(afterWork);
	}

	private void setBeforeGoingOffDuty(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter beforeGoingoffDuty = new Parameter();
		beforeGoingoffDuty.setName(ShiftConstants.SHIFT_BEFORE_GOING_OFF_DUTY);
		beforeGoingoffDuty.setValue(shiftSettingDto.getBeforeGoingOffDuty());
		beforeGoingoffDuty.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(beforeGoingoffDuty);
	}

	private void setBeforeGoingToWork(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter beforeGoingToWork = new Parameter();
		beforeGoingToWork.setName(ShiftConstants.SHIFT_BEFORE_GOING_TO_WORK);
		beforeGoingToWork.setValue(shiftSettingDto.getBeforeGoingToWork());
		beforeGoingToWork.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(beforeGoingToWork);
	}

	private void setCheckOutTime(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter checkOutTime = new Parameter();
		checkOutTime.setName(ShiftConstants.SHIFT_CHECK_OUT_TIME);
		checkOutTime.setValue(shiftSettingDto.getCheckOutTime());
		checkOutTime.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(checkOutTime);
	}

	private void checkInTime(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter checkInTime = new Parameter();
		checkInTime.setName(ShiftConstants.SHIFT_CHECK_IN_TIME);
		checkInTime.setValue(shiftSettingDto.getCheckInTime());
		checkInTime.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(checkInTime);
	}

	private void setTimeTableType(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter timeTableType = new Parameter();
		timeTableType.setName(ShiftConstants.SHIFT_TIME_TABLE_TYPE);
		timeTableType.setValue(shiftSettingDto.getTimeTableType());
		timeTableType.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(timeTableType);
	}

	private void setName(ShiftSettingDto shiftSettingDto, List<Parameter> parameterList) {
		Parameter name = new Parameter();
		name.setName(ApplicationConstants.NAME);
		name.setValue(shiftSettingDto.getName());
		name.setOrganization(shiftSettingDto.getOrganization());
		parameterList.add(name);
	}

	@Override
	public PaginationDto<Shift> searchByField(Long id, String name, int pageno, String sortField, String sortDir,String org) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField =  ApplicationConstants.ID;
		}
		Page<Shift> page = getShiftPage(id, name, pageno, sortField, sortDir,org);
		List<Shift> accessLevelList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Shift> dtoList = new PaginationDto<Shift>(accessLevelList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Shift> getShiftPage(Long id, String name, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Shift> isdeleted = generalSpecification.isDeletedSpecification();
		Specification<Shift> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Shift> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Shift> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);
		Page<Shift> page = shiftRepository.findAll(isdeleted.and(idSpc).and(nameSpc).and(orgSpc), pageable);
		return page;
	}
}



