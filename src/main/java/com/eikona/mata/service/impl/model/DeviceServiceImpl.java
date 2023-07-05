package com.eikona.mata.service.impl.model;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BewardDeviceConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.DeviceConstants;
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeDevice;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeDeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ActionService actionService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EmployeeDeviceRepository employeeDeviceRepository;

	@Autowired
	private GeneralSpecificationUtil<Device> generalSpecification;

	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecificationEmp;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	@Qualifier(BewardDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<String> bewardSyncServiceImpl;

	@Autowired
	@Qualifier(CorsightDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<String> corsightSyncServiceImpl;


	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public List<Device> getAll() {
		return deviceRepository.findAllByIsDeletedFalse();

	}

	@Override
	public void save(Device device, Principal principal) {

		device.setDeleted(false);
		device.setSync(false);
//		String cameraId = null;
		if (null == device.getId()) {
			device.setFlag(ApplicationConstants.CREATE);

			this.deviceRepository.save(device);
//			if("FRWT".equalsIgnoreCase(deviceObj.getModel()))
//				unvDeviceUtil.addEmployeeToUNVDevice(deviceObj,  "Create", principal);
//			else if("BFRC".equalsIgnoreCase(deviceObj.getModel())) 
//				bewardDeviceUtil.addEmployeeToBewardDevice(deviceObj,  "Create", principal);
//			else{
//				cameraId=corsightDeviceUtil.createCamera(deviceObj);
//				deviceObj.setCameraId(cameraId);
//				deviceRepository.save(deviceObj);
//			}
		} else {
			Device deviceObj = deviceRepository.findById(device.getId()).get();
			device.setCreatedBy(deviceObj.getCreatedBy());
			device.setCreatedDate(deviceObj.getCreatedDate());
			device.setCameraId(deviceObj.getCameraId());
			device.setFlag(ApplicationConstants.EDIT);

//			if (null != device.getArea() && (deviceObj.getArea() != device.getArea())) {//!deviceObj.getArea().equals(device.getArea())
//				if ("FRWT".equalsIgnoreCase(deviceObj.getModel())) {
//						unvDeviceUtil.addEmployeeToUNVDevice(device, "Edit", principal);
//				} else if ("BFRC".equalsIgnoreCase(deviceObj.getModel())) {
//						bewardDeviceUtil.addEmployeeToBewardDevice(device, "Edit", principal);
//				} else {
//					
//					if(null == device.getArea().getWatchlistId()) {
//						String watchlistId = corsightDeviceUtil.createAndUpdateWatchList(device.getArea(), "Create");
//						device.getArea().setWatchlistId(watchlistId);
//						areaDatatableRepository.save(device.getArea());
//					}
//					
//					List<Employee> employeeList = employeeRepository.findByArea(device.getArea());
//					for(Employee employee: employeeList) {
//						if(null == employee.getPoi()) {
//							ActionDetails actionDetails = new ActionDetails();
//							Action action = new Action();
//							action.setEmployee(employee);
//							actionDetails.setAction(action);
//							String poi = corsightDeviceServiceImpl.addEmployeeToDevice(actionDetails);
//							employee.setPoi(poi);
//							
//							employeeRepository.save(employee);
//						}
//						corsightDeviceUtil.addPoiToWatchList(device.getArea(), employee);
//					}
//					
//					if (null != deviceObj.getCameraId()) {
//						String msg = corsightDeviceUtil.queryDevice(deviceObj.getCameraId());
//						if ("Success".equalsIgnoreCase(msg))
//							corsightDeviceUtil.updateCamera(device);
//						else {
//							cameraId = corsightDeviceUtil.createCamera(device);
//							deviceObj.setCameraId(cameraId);
//							deviceRepository.save(deviceObj);
//						}
//					} else {
//						cameraId = corsightDeviceUtil.createCamera(deviceObj);
//						deviceObj.setCameraId(cameraId);
//						deviceRepository.save(deviceObj);
//					}
//				}
//			} else {
			this.deviceRepository.save(device);
//			}
		}
	}

	@Override
	public Device getById(long id) {
		Device optional = deviceRepository.findByIdAndIsDeletedFalse(id);
		Device device = null;
		if (null != optional) {
			device = optional;
		} else {
			throw new RuntimeException(DeviceConstants.DEVICE_NOT_FOUND + id);
		}
		return device;

	}

	@Override
	public void deleteById(long id) {
		Optional<Device> optional = deviceRepository.findById(id);
		Device device = null;
		if (optional.isPresent()) {
			device = optional.get();
			device.setDeleted(true);
			device.setSync(false);
//        	if("Corsight".equalsIgnoreCase(device.getModel()))
//        		corsightDeviceUtil.removeCamera(device);
//        	
		} else {
			throw new RuntimeException(DeviceConstants.DEVICE_NOT_FOUND + id);
		}
		this.deviceRepository.save(device);
	}

	@Override
	public void employeeSyncFromMataToDevice(long id, Principal principal) {
		Device device = getById(id);
		List<Area> areaList = new ArrayList<>();
		areaList.add(device.getArea());
		long empCount = employeeRepository.countEmployeeAndIsDeletedFalseCustom(device.getBranch(),
				device.getArea().getId());
		int limit = NumberConstants.THOUSAND;
		int totalPage = (int) (empCount / limit);

		for (int i = NumberConstants.ZERO; i <= totalPage; i++) {
			Pageable paging = PageRequest.of(i, limit, Sort.by(ApplicationConstants.ID).ascending());
			List<Employee> employeeList = employeeRepository
					.findByAreaIdAndBranchAndIsDeletedFalseCustom(device.getBranch(), device.getArea().getId(), paging);
			for (Employee employee : employeeList) {
				actionService.employeeDeviceAction(device, employee, ApplicationConstants.SYNC,
						ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}

	}

	@Override
	public String saveDeviceEmployeeAssociation(EmployeeDevice employeeDevice, Long empId, Long devId,
			Principal principal) throws Exception {
		Employee employeeObj = employeeService.getById(empId);
		Device device = getById(devId);

		String status = actionService.employeeDeviceAction(device, employeeObj, ApplicationConstants.SYNC,
				ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
		List<Area> areaList = new ArrayList<>();
		areaList.add(device.getArea());
		areaList.addAll(employeeObj.getArea());
		employeeObj.setArea(areaList);
		employeeRepository.save(employeeObj);

		Date date = new Date();

		employeeDevice = employeeDeviceRepository.findByEmployeeAndDevice(employeeObj, device);
		if (null != employeeDevice) {
			employeeDevice.setLastSync(date);
		} else {
			employeeDevice = new EmployeeDevice();
			employeeDevice.setEmployee(employeeObj);
			employeeDevice.setDevice(device);
			employeeDevice.setLastSync(date);
		}
		employeeDeviceRepository.save(employeeDevice);
		return status;
	}

	@Override
	public PaginationDto<Device> searchByField(Long id, String deviceType, String name, String area, String office,
			int pageno, String sortField, String sortDir,String org) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Device> page = getDevicePage(id, deviceType, name, area, office, pageno, sortField, sortDir,org);
		
		List<Device> deviceList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC
				: ApplicationConstants.ASC;
		PaginationDto<Device> dtoList = new PaginationDto<Device>(deviceList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Device> getDevicePage(Long id, String deviceType, String name, String area, String office, int pageno,
			String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Device> isdeleted = generalSpecification.isDeletedSpecification();
		Specification<Device> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Device> deviceTypeSpc = generalSpecification.stringSpecification(deviceType,
				DeviceConstants.MODEL);
		Specification<Device> nameSpc = generalSpecification.stringSpecification(name, DeviceConstants.NAME);
		Specification<Device> officeSpc = generalSpecification.foreignKeyStringSpecification(office,
				DeviceConstants.BRANCH, DeviceConstants.NAME);
		Specification<Device> areaSpc = generalSpecification.foreignKeyStringSpecification(area, DeviceConstants.AREA,
				DeviceConstants.NAME);
		Specification<Device> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);

		Page<Device> page = deviceRepository
				.findAll(isdeleted.and(idSpc).and(deviceTypeSpc).and(nameSpc).and(officeSpc).and(areaSpc).and(orgSpc), pageable);
		return page;
	}

	@Override
	public PaginationDto<Employee> searchDeviceToEmployee(Long id, String empId, String empName, String designation,
			String office, String area, int pageno, String sortField, String sortDir,String orgName) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Employee> page = getEmployeePage(empId, empName, designation, office, area, pageno, sortField, sortDir,orgName);
		List<Employee> pageList = page.getContent();

		List<Employee> employeeListobj = new ArrayList<Employee>();
		for (Employee employee : pageList) {
			Device device = new Device();
			device.setId(id);

			EmployeeDevice employeeDevice = employeeDeviceRepository.findByEmployeeAndDevice(employee, device);
			if (null != employeeDevice) {
				employee.setSyncDate(employeeDevice.getLastSync());
			}

			employeeListobj.add(employee);
		}
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC
				: ApplicationConstants.ASC;
		PaginationDto<Employee> dtoList = new PaginationDto<Employee>(employeeListobj, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Employee> getEmployeePage(String empId, String empName, String designation, String office, String area,
			int pageno, String sortField, String sortDir, String orgName) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Employee> isdeleted = generalSpecificationEmp.isDeletedSpecification();
		Specification<Employee> empIdSpc = generalSpecificationEmp.stringSpecification(empId, EmployeeConstants.EMPID);
		Specification<Employee> nameSpc = generalSpecificationEmp.stringSpecification(empName, EmployeeConstants.NAME);
		Specification<Employee> degSpc = generalSpecificationEmp.foreignKeyStringSpecification(designation,
				EmployeeConstants.DESIGNATION, EmployeeConstants.NAME);
		Specification<Employee> officeSpc = generalSpecificationEmp.foreignKeyStringSpecification(office,
				EmployeeConstants.BRANCH, EmployeeConstants.NAME);
		Specification<Employee> areaSpc = generalSpecificationEmp.foreignKeyStringSpecification(area,
				EmployeeConstants.AREA, EmployeeConstants.NAME);
		Specification<Employee> orgSpc = generalSpecificationEmp.foreignKeyStringSpecification(orgName,
				"organization", EmployeeConstants.NAME);

		Page<Employee> page = employeeRepository
				.findAll(isdeleted.and(empIdSpc).and(nameSpc).and(degSpc).and(officeSpc).and(areaSpc).and(orgSpc), pageable);
		return page;
	}

	@Override
	public String generateTransactionByDate(long id, String sDate, String eDate) {

		Device device = deviceService.getById(id);

		SimpleDateFormat inputformatStr = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SLASH);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US);

		Date startDate = null;
		Date endDate = null;
		String message = ApplicationConstants.DELIMITER_EMPTY;
		try {
			startDate = inputformatStr.parse(sDate);
			endDate = inputformatStr.parse(eDate);

			String sDateStr = format.format(startDate);
			String eDateStr = format.format(endDate);

			startDate = format.parse(sDateStr);
			endDate = format.parse(eDateStr);

			message = getTransactionByDeviceType(device, startDate, endDate, message);

		} catch (ParseException e) {
			System.out.println(e);
		}

		return message;
	}

	private String getTransactionByDeviceType(Device device, Date startDate, Date endDate, String message) {

		
		if (BewardDeviceConstants.MODEL_TYPE.equalsIgnoreCase(device.getModel())) {
			String deviceInfo = bewardSyncServiceImpl.deviceBasicInfo(device.getIpAddress());
			if (null != deviceInfo) {
				message = bewardSyncServiceImpl.getTransactionByDate(device, startDate, endDate);
			} else {
				message = MessageConstants.TRANSACTION_FAILED;
			}
		}
		if (CorsightDeviceConstants.MODEL_TYPE.equalsIgnoreCase(device.getModel())) {
			String deviceInfo = corsightDeviceUtil.corsightServerBasicInfo();
			if (null != deviceInfo) {
				message = corsightSyncServiceImpl.getTransactionByDate(device, startDate, endDate);
			}
		}
		return message;
	}
}
