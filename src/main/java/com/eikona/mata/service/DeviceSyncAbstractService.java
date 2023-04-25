package com.eikona.mata.service;

import java.security.Principal;
import java.util.Date;

import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;


public interface DeviceSyncAbstractService<T> {
	/**
	 * This function checks the device is online or offline.
	 * @param ipAddress -Device Ip Address
	 */
	T deviceBasicInfo(String ipAddress);
	
	/**
	 * This function retrieves all employee list present in respective device and save it in mata dashboard.
	 * @param device
	 */
	void pullEmployeeFromDeviceToMata(Device device);
	
	/**
	 * This function is used to delete the particular employee from respective device.
	 * @param actionDetails 
	 */
	T deleteSingleEmployeeFromDevice(ActionDetails actionDetails);
	
	/**
	 * This function is used to delete all employees present in the respective device.
	 * @param device
	 */
	T deleteAllEmployeeFromDevice(Device device);
	
	/**
	 * This function is used to add particular employee into respective device.
	 * @param actionDetails
	 */
	T addEmployeeToDevice(ActionDetails actionDetails);
	
	/**
	 * This function is used to edit a particular employee data in respective device.
	 * @param actionDetails
	 */
	T editEmployeeInDevice(ActionDetails actionDetails);
	
	/**
	 * This function retrieves the employee basic Info from the respective device.
	 * @param 
	 */
	T queryFromDevice(Device device, Employee employee);
	
	/**
	 * This function gives a response of transaction list with respect to particular device and date range.
	 * @param device    
	 * @param startDate -start date of transaction
	 * @param endDate   -end date of transaction
	 */
	T getTransactionByDate(Device device, Date startDate, Date endDate);
	
	/**
	 * This function will push the employee into the assigned new area and delete the employee from the old area.
	 * These functionality happen in both database as well as device also.
	 * @param employee 
	 * @param principal -This interface represents the abstract notion of a principal, which can be used to represent any entity, such as an individual, a corporation, and a login id.
	 */
	void saveAsArea(Employee employee, Principal principal);
}
