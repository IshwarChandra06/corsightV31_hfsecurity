package com.eikona.mata.config.security;

import static com.eikona.mata.constants.ApplicationConstants.DEFAULT;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Department;
import com.eikona.mata.entity.Designation;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.Privilege;
import com.eikona.mata.entity.Role;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.PrivilegeRepository;
import com.eikona.mata.repository.RoleRepository;
import com.eikona.mata.repository.UserRepository;

@Service
public class DBSeeder implements CommandLineRunner {
	
	 private UserRepository userRepository;
	 
	 private PrivilegeRepository privilegeRepository;
	 
	 private RoleRepository roleRepository;
	 
	 private OrganizationRepository organizationRepository;
	 
	 private DepartmentRepository departmentRepository;
	 
	 private DesignationRepository designationRepository;
	 
	 private BranchRepository branchRepository;
	 
	 private AreaRepository areaRepository;
	 
     private PasswordEncoder passwordEncoder;
     
     public DBSeeder(PrivilegeRepository privilegeRepository,RoleRepository roleRepository,UserRepository userRepository,OrganizationRepository organizationRepository,
    		 DepartmentRepository departmentRepository,DesignationRepository designationRepository,BranchRepository branchRepository, PasswordEncoder passwordEncoder,
    		 AreaRepository areaRepository) {
    	 this.privilegeRepository=privilegeRepository;
    	 this.roleRepository=roleRepository;
    	 this.userRepository = userRepository;
    	 this.branchRepository=branchRepository;
    	 this.departmentRepository=departmentRepository;
    	 this.designationRepository=designationRepository;
    	 this.organizationRepository=organizationRepository;
    	 this.areaRepository= areaRepository;
         this.passwordEncoder = passwordEncoder;
     }

	@Override
	public void run(String... args) throws Exception {
		List<Privilege> privilegeList = privilegeRepository.findAllByIsDeletedFalse();
			
			if(null==privilegeList || privilegeList.isEmpty()) {
				List<Privilege> privileges = SeedPrivileges();
				  
				Role admin = seedRole(privileges);
					
				seedUser(admin);
			
			}
			seedOrganization();
			seedDepartment();
			seedDesignation();
			Branch branch =seedBranch();
			seedArea(branch);
			
		}
	
	private void seedArea(Branch branch) {
		List<Area> areaList = areaRepository.findAllByIsDeletedFalse();//ByNameAndIsDeletedFalse(DEFAULT);
		if(null==areaList || areaList.isEmpty()) {
			Area area = new Area(DEFAULT,branch,false);
			areaRepository.save(area);
		}
	}
	
	private void seedDesignation() {
		List<Designation> designationList = designationRepository.findAllByIsDeletedFalse();//findByNameAndIsDeletedFalse(DEFAULT);
		if(null==designationList || designationList.isEmpty()) {
			Designation designation = new Designation(DEFAULT,false);
			designationRepository.save(designation);
		}
	}
	
	private void seedDepartment() {
		List<Department> departmentList = departmentRepository.findAllByIsDeletedFalse();//findByNameAndIsDeletedFalse(DEFAULT);
		if(null==departmentList || departmentList.isEmpty()) {
			Department department = new Department(DEFAULT,false);
			departmentRepository.save(department);
		}
	}

	private Branch seedBranch() {
		List<Branch> branchList = branchRepository.findAllByIsDeletedFalse();//findByNameAndIsDeletedFalse(DEFAULT);
		Branch branch = null;
		if(null==branchList || branchList.isEmpty()) {
			branch = new Branch(DEFAULT,false);
			branchRepository.save(branch);
		}
		return branch;
	}

	private void seedOrganization() {
		List<Organization> organizationList= organizationRepository.findAllByIsDeletedFalse();
		if(null==organizationList || organizationList.isEmpty()) {
			Organization defaultOrg = new Organization(DEFAULT,false);
			organizationRepository.save(defaultOrg);
		}
	}

	private List<Privilege> SeedPrivileges() {
		Privilege dashboardView = new Privilege("dashboard_view", false);
		
		Privilege orgView = new Privilege("organization_view", false);
		Privilege orgCreate = new Privilege("organization_create", false);
		Privilege orgUpdate = new Privilege("organization_update", false);
		Privilege orgDelete = new Privilege("organization_delete", false);
		
		Privilege userView = new Privilege("user_view", false);
		Privilege userCreate = new Privilege("user_create", false);
		Privilege userUpdate = new Privilege("user_update", false);
		Privilege userDelete = new Privilege("user_delete", false);
		
		Privilege roleView = new Privilege("role_view", false);
		Privilege roleCreate = new Privilege("role_create", false);
		Privilege roleUpdate = new Privilege("role_update", false);
		Privilege roleDelete = new Privilege("role_delete", false);
		
		Privilege privilegeView = new Privilege("privilege_view", false);
		Privilege privilegeUpdate = new Privilege("privilege_update", false);
		Privilege privilegeDelete = new Privilege("privilege_delete", false);
		
		Privilege branchView = new Privilege("branch_view", false);
		Privilege branchCreate = new Privilege("branch_create", false);
		Privilege branchUpdate = new Privilege("branch_update", false);
		Privilege branchDelete = new Privilege("branch_delete", false);
		
		Privilege watchlistSync = new Privilege("watchlist_sync", false);
		
		Privilege areaView = new Privilege("area_view", false);
		Privilege areaCreate = new Privilege("area_create", false);
		Privilege areaUpdate = new Privilege("area_update", false);
		Privilege areaDelete = new Privilege("area_delete", false);
		
		Privilege areaEmployeeAssociation = new Privilege("area_employee_association", false);
		Privilege areaDeviceAssociation = new Privilege("area_device_association", false);
		
		Privilege constraintRangeView = new Privilege("constraint_range_view", false);
		Privilege constraintRangeCreate = new Privilege("constraint_range_create", false);
		Privilege constraintRangeUpdate = new Privilege("constraint_range_update", false);
		Privilege constraintRangeDelete = new Privilege("constraint_range_delete", false);
			
		Privilege constraintSingleView = new Privilege("constraint_single_view", false);
		Privilege constraintSingleCreate = new Privilege("constraint_single_create", false);
		Privilege constraintSingleUpdate = new Privilege("constraint_single_update", false);
		Privilege constraintSingleDelete = new Privilege("constraint_single_delete", false);
		
		Privilege dailyreportView = new Privilege("dailyreport_view", false);
		Privilege dailyreportGenerate = new Privilege("dailyreport_generate", false);
		Privilege dailyreportGenerateShiftwise = new Privilege("dailyreport_generate_shiftwise", false);
		Privilege dailyreportExport = new Privilege("dailyreport_export", false);
		
		Privilege departmentView = new Privilege("department_view", false);
		Privilege departmentCreate = new Privilege("department_create", false);
		Privilege departmentUpdate = new Privilege("department_update", false);
		Privilege departmentDelete = new Privilege("department_delete", false);
		
		Privilege designationView = new Privilege("designation_view", false);
		Privilege designationCreate = new Privilege("designation_create", false);
		Privilege designationUpdate = new Privilege("designation_update", false);
		Privilege designationDelete = new Privilege("designation_delete", false);
		
		Privilege mataToDeviceSync = new Privilege("mata_to_device_sync", false);
		Privilege deviceToMataSync = new Privilege("device_to_mata_sync", false);
		Privilege transactionByDate = new Privilege("transaction_by_date", false);
		Privilege deviceEmployeeAssociation = new Privilege("device_employee_association", false);
		
		Privilege deviceSync = new Privilege("device_sync", false);
		Privilege deviceView = new Privilege("device_view", false);
		Privilege deviceCreate = new Privilege("device_create", false);
		Privilege deviceUpdate = new Privilege("device_update", false);
		Privilege deviceDelete = new Privilege("device_delete", false);
		
		Privilege doorView = new Privilege("door_view", false);
		Privilege doorCreate = new Privilege("door_create", false);
		Privilege doorUpdate = new Privilege("door_update", false);
		Privilege doorDelete = new Privilege("door_delete", false);
		
		Privilege emailScheduleView = new Privilege("email_schedule_view", false);
		Privilege emailScheduleCreate = new Privilege("email_schedule_create", false);
		Privilege emailScheduleUpdate = new Privilege("email_schedule_update", false);
		Privilege emailScheduleDelete = new Privilege("email_schedule_delete", false);
		
		Privilege emailSetupView = new Privilege("email_setup_view", false);
		Privilege emailSetupCreate = new Privilege("email_setup_create", false);
		Privilege emailSetupUpdate = new Privilege("email_setup_update", false);
		Privilege emailSetupDelete = new Privilege("email_setup_delete", false);
		
		Privilege accessLevelSync = new Privilege("access_level_sync", false);
		Privilege accessLevelView = new Privilege("access_level_view", false);
		Privilege absentReportView = new Privilege("absent_report_view", false);
		Privilege trasactionView = new Privilege("transaction_view", false);
		
		Privilege parameterView = new Privilege("parameter_view", false);
		Privilege parameterCreate = new Privilege("parameter_create", false);
		Privilege parameterUpdate = new Privilege("parameter_update", false);
		Privilege parameterDelete = new Privilege("parameter_delete", false);
		
		Privilege shiftView = new Privilege("shift_view", false);
		Privilege shiftCreate = new Privilege("shift_create", false);
		Privilege shiftUpdate = new Privilege("shift_update", false);
		Privilege shiftDelete = new Privilege("shift_delete", false);
		Privilege shiftSetting = new Privilege("shift_setting", false);
		
		Privilege monthlypunchrecordView = new Privilege("monthlypunchrecord_view", false);
		Privilege monthlypunchrecordExport = new Privilege("monthlypunchrecord_export", false);
		Privilege monthlyattendanceView = new Privilege("monthlyattendance_view", false);
		Privilege monthlyattendanceExport = new Privilege("monthlyattendance_export", false);
		
		Privilege pandemicDashboardView = new Privilege("pandemic_dashboard_view", false);
		Privilege exceptionSummaryView = new Privilege("exception_summary_view", false);
		Privilege employeeShiftUnassignedExport = new Privilege("employee_shift_unassigned_export", false);
		Privilege employeeShiftUnassignedView = new Privilege("employee_shift_unassigned_view", false);
		Privilege notificationLogView = new Privilege("notification_log_view", false);
		
		Privilege employeeView = new Privilege("employee_view", false);
		Privilege employeeCreate = new Privilege("employee_create", false);
		Privilege employeeUpdate = new Privilege("employee_update", false);
		Privilege employeeDelete = new Privilege("employee_delete", false);
		
		Privilege employeeImport = new Privilege("employee_import", false);
		Privilege employeeCosecImport = new Privilege("employee_cosec_import", false);
		Privilege employeeSync = new Privilege("employee_sync", false);
		Privilege employeeDeviceAssociation = new Privilege("employee_device_association", false);
		Privilege employeeAreaAssociation = new Privilege("employee_area_association", false);
		Privilege employeeRosterExport = new Privilege("employee_roster_export", false);
		Privilege employeeRosterImport = new Privilege("employee_roster_import", false);
		Privilege employeeRosterView = new Privilege("employee_roster_view", false);
		
		Privilege actiondetailsView = new Privilege("action_details_view", false);
		Privilege actiondetailsExport = new Privilege("action_details_export", false);
		Privilege actionview = new Privilege("action_view", false);
		
		
		
		List<Privilege> privileges = Arrays.asList(dashboardView,
				orgView, orgCreate, orgUpdate, orgDelete,
				userView, userCreate, userUpdate, userDelete,
				roleView, roleCreate, roleUpdate, roleDelete,
				privilegeView,privilegeUpdate,privilegeDelete,
				watchlistSync,notificationLogView,actiondetailsView,actiondetailsExport,
				areaView,areaCreate,areaUpdate,areaDelete,actionview,
				areaEmployeeAssociation,areaDeviceAssociation,deviceEmployeeAssociation,
				constraintRangeView,constraintRangeCreate,constraintRangeUpdate,constraintRangeDelete,
				constraintSingleView,constraintSingleCreate,constraintSingleUpdate,constraintSingleDelete,
				dailyreportView,dailyreportGenerate,dailyreportGenerateShiftwise,dailyreportExport,
				departmentView,departmentCreate,departmentUpdate,departmentDelete,
				designationView,designationCreate,designationUpdate,designationDelete,
				mataToDeviceSync,deviceToMataSync,transactionByDate,
				deviceView,deviceCreate,deviceUpdate,deviceDelete,deviceSync,
				doorView,doorCreate,doorUpdate,doorDelete,
				emailScheduleView,emailScheduleCreate,emailScheduleUpdate,emailScheduleDelete,
				emailSetupView,emailSetupCreate,emailSetupUpdate,emailSetupDelete,
				branchView, branchCreate, branchUpdate, branchDelete,
				accessLevelSync, accessLevelView, absentReportView, trasactionView,
				parameterView, parameterCreate, parameterUpdate, parameterDelete,
				shiftView, shiftCreate, shiftUpdate, shiftDelete, shiftSetting,
				monthlypunchrecordView, monthlypunchrecordExport, monthlyattendanceView, monthlyattendanceExport,
				pandemicDashboardView, exceptionSummaryView, employeeShiftUnassignedExport, employeeShiftUnassignedView,
				employeeView, employeeCreate, employeeUpdate, employeeDelete,employeeImport,
				employeeCosecImport, employeeSync, employeeDeviceAssociation, employeeAreaAssociation, employeeRosterExport, employeeRosterImport, employeeRosterView
				
				);
		
		
		  privilegeRepository.saveAll(privileges);
		return privileges;
	}

	private Role seedRole(List<Privilege> privileges) {
		Role admin=roleRepository.findByName("Admin");
		if(null==admin) {
			 admin= new Role("Admin", privileges, false);
			roleRepository.save(admin);
		}
		return admin;
	}

	private void seedUser(Role admin) {
		List<User> userList=userRepository.findAllByIsDeletedFalse();
		if(null==userList || userList.isEmpty()) {
			User adminUser= new User("Admin", passwordEncoder.encode("Admin@123"), true, admin, false);
			userRepository.save(adminUser);
		}
	}
}
