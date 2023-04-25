package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.EmployeeObjectMap;

@Component
public class AreaEmployeeAssociationWriter implements ItemWriter<Employee> {

	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public void write(List<? extends Employee> employeeList) throws Exception {

		Map<Long, Employee> employeeMap = employeeObjectMap.getEmployeeByIsDeletedFalseAndIsSyncTrue();
		for (Employee employee : employeeList) {
			
			Employee empObj = employeeMap.get(employee.getId());
			List<Area> areaList = empObj.getArea();
			List<String> watchlistIdDataBase = new ArrayList<String>();
			
			for(Area area: areaList) {
				watchlistIdDataBase.add(area.getWatchlistId());
			}
			
			String message = corsightDeviceUtil.corsightServerBasicInfo();
			if(null!=message) {
				List<String> watchlistIdList=corsightDeviceUtil.getWatchlistFromPoiDetails(employee.getPoi());

				  for (Area area : empObj.getArea()) {
						
						if(!(watchlistIdList.contains(area.getWatchlistId())))
								corsightDeviceUtil.addPoiToWatchList(area, empObj);
						}
				  for(String watchlistId : watchlistIdList) {
						
						if(!(watchlistIdDataBase.contains(watchlistId))) {
							//remove poi from watchlist
							Area area = new Area();
							area.setWatchlistId(watchlistId);
							corsightDeviceUtil.removePoiFromWatchList(area, empObj);
						}
					}
				}
				
				
			
		}

	}
}
