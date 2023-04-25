package com.eikona.mata.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Device;
@Component
public class DeviceDBProcessor implements  ItemProcessor<Device, Device>{
	
	@Override
    public Device process(Device deviceObj) throws Exception {
		Device device = new Device();
		device.setId(deviceObj.getId());
       System.out.println("inside processor " + device.toString());
        return device;
    }
}
