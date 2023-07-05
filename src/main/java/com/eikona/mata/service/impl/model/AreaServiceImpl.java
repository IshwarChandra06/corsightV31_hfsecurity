package com.eikona.mata.service.impl.model;

import java.security.Principal;
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
import com.eikona.mata.constants.AreaConstants;
import com.eikona.mata.constants.DeviceConstants;
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.AreaService;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private GeneralSpecificationUtil<Area> generalSpecificationArea;

	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecificationEmp;

	@Autowired
	private GeneralSpecificationUtil<Device> generalSpecificationDevice;

	@Override
	public List<Area> getAll() {
		return areaRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(Area area) {
		area.setDeleted(false);
		area.setSync(false);

//		String watchlistId = null;
//		if ((null != area.getWatchlist()) || (!area.getWatchlist().isEmpty())) {
//
//			String msg = corsightDeviceUtil.queryWatchlist(area.getWatchlistId());
//			if (null != area.getWatchlistId() && "Success".equalsIgnoreCase(msg)) {
//				corsightDeviceUtil.createAndUpdateWatchList(area, "Update");
//				areaRepository.save(area);
//			} else {
//				watchlistId = corsightDeviceUtil.createAndUpdateWatchList(area, "Create");
//				area.setWatchlistId(watchlistId);
//				areaRepository.save(area);
//
//			}
//		} else
		areaRepository.save(area);
	}

	@Override
	public Area getById(long id) {
		Optional<Area> optional = areaRepository.findById(id);
		Area area = null;
		if (optional.isPresent()) {
			area = optional.get();
		} else {
			throw new RuntimeException(AreaConstants.NOT_FOUND_FOR_ID + id);
		}
		return area;
	}

	@Override
	public void deletedById(long id) {
		Optional<Area> optional = areaRepository.findById(id);
		Area area = null;
		if (optional.isPresent()) {
			area = optional.get();
			area.setDeleted(true);
			area.setSync(false);
//			if ((null != area.getWatchlistId()) || (!area.getWatchlistId().isEmpty())) {
//				corsightDeviceUtil.removeWatchList(area);
//
//				List<Device> devicelist = new ArrayList<>();
//				area.setDevice(devicelist);
//				areaRepository.save(area);
//			}
		} else {
			throw new RuntimeException(AreaConstants.NOT_FOUND_FOR_ID + id);
		}
		this.areaRepository.save(area);
	}

	@Override
	public String saveAreaEmployeeAssociation(Long employeeId, Long areaId, Principal principal) {
//		String status ="";
		try {
			Area area = getById(areaId);
			Employee employee = employeeService.getById(employeeId);

			if (null != employee) {
				List<Area> areaList = employee.getArea();
				if (areaList.isEmpty()) {
					areaList.add(area);

				} else {
					if (!(areaList.contains(area))) {
						areaList.add(area);
					}
				}
				employee.setArea(areaList);
				employeeRepository.save(employee);

//					List<Area> areaListObj = new ArrayList<Area>();
//					areaListObj.add(area);
//					List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaListObj);
//					
//					Device device = deviceList.get(0);
//					if("Corsight".equalsIgnoreCase(device.getModel())) {
//						corsightDeviceUtil.addPoiToWatchList(area, employee);
//					}
//					else {
//					status = actionService.employeeDeviceAction(device, employee, "Sync", "App", " ", principal);
//					}
			}
			
			return "Employee added successfully into specific Area!!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Employee Addition into Area is Failed!!";
		}
		
//			return status;

	}

	@Override
	public String saveAreaDeviceAssociation(Long deviceId, Long areaId, Principal principal) {

		try {
			Area area = new Area();
			area.setId(areaId);
			Device device = deviceService.getById(deviceId);
			if (null != device) {
				device.setArea(area);
				deviceService.save(device, principal);
			}
			return "Changed Successfully!!";
		} catch (Exception e) {
			return "Change Failed!!";
		}
	}

	@Override
	public PaginationDto<Area> searchByField(Long id, String name, String office, int pageno, String sortField,
			String sortDir,String organization) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<Area> page = getPaginatedArea(id, name, office, pageno, sort,organization);
		List<Area> accessLevelList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Area> dtoList = new PaginationDto<Area>(accessLevelList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Area> getPaginatedArea(Long id, String name, String office, int pageno, Sort sort, String organization) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Area> isdeleted = generalSpecificationArea.isDeletedSpecification();

		Specification<Area> idSpc = generalSpecificationArea.longSpecification(id, ApplicationConstants.ID);
		Specification<Area> nameSpc = generalSpecificationArea.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Area> officeSpc = generalSpecificationArea.foreignKeyStringSpecification(office, AreaConstants.BRANCH, ApplicationConstants.NAME);
		Specification<Area> orgSpc = generalSpecificationArea.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);

		Page<Area> page = areaRepository.findAll(isdeleted.and(idSpc).and(nameSpc).and(officeSpc).and(orgSpc),pageable);
		return page;
	}

	@Override
	public PaginationDto<Employee> searchAreaToEmployee(String empId, String empName, String office, String area, int pageno,
			String sortField, String sortDir,String orgName) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<Employee> page = getPaginatedAreaToEmployeeAssociation(empId, empName, office, pageno, sort,orgName);
		List<Employee> pageObjList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Employee> dtoList = new PaginationDto<Employee>(pageObjList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Employee> getPaginatedAreaToEmployeeAssociation(String empId, String empName, String office,
			int pageno, Sort sort, String orgName) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Employee> isdeleted = generalSpecificationEmp.isDeletedSpecification();

		Specification<Employee> idSpc = generalSpecificationEmp.stringSpecification(empId, EmployeeConstants.EMPID);
		Specification<Employee> nameSpc = generalSpecificationEmp.stringSpecification(empName, EmployeeConstants.NAME);
		Specification<Employee> officeSpc = generalSpecificationEmp.foreignKeyStringSpecification(office, EmployeeConstants.BRANCH, ApplicationConstants.NAME);
		Specification<Employee> areaSpc = generalSpecificationEmp.foreignKeyStringSpecification(office, EmployeeConstants.AREA, ApplicationConstants.NAME);
		Specification<Employee> orgSpc = generalSpecificationEmp.foreignKeyStringSpecification(orgName,"organization", ApplicationConstants.NAME);

		Page<Employee> page = employeeRepository.findAll(isdeleted.and(idSpc).and(nameSpc).and(areaSpc).and(officeSpc).and(orgSpc),
				pageable);
		return page;
	}

	@Override
	public PaginationDto<Device> searchAreaToDevice(String name, String office, String area, int pageno,
			String sortField, String sortDir,String orgName) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<Device> page = getPaginatedAreaToDeviceAssociation(name, office, area, pageno, sort,orgName);
		List<Device> pageObjList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		
		PaginationDto<Device> dtoList = new PaginationDto<Device>(pageObjList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Device> getPaginatedAreaToDeviceAssociation(String name, String office, String area, int pageno, Sort sort, String orgName) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Device> isdeleted = generalSpecificationDevice.isDeletedSpecification();

		Specification<Device> nameSpc = generalSpecificationDevice.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Device> officeSpc = generalSpecificationDevice.foreignKeyStringSpecification(office, DeviceConstants.BRANCH, ApplicationConstants.NAME);
		Specification<Device> areaSpc = generalSpecificationDevice.foreignKeyStringSpecification(area, DeviceConstants.AREA, ApplicationConstants.NAME);
		Specification<Device> orgSpc = generalSpecificationDevice.foreignKeyStringSpecification(orgName,"organization", ApplicationConstants.NAME);

		Page<Device> page = deviceRepository.findAll(isdeleted.and(nameSpc).and(officeSpc).and(areaSpc).and(orgSpc), pageable);
		return page;
	}

}
