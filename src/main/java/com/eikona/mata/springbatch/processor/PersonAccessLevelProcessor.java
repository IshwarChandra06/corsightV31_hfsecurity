package com.eikona.mata.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
@Component
public class PersonAccessLevelProcessor implements ItemProcessor<EmployeeShiftDailyAssociation, EmployeeShiftDailyAssociation>{
	@Override
    public EmployeeShiftDailyAssociation process(EmployeeShiftDailyAssociation trans) throws Exception {
       EmployeeShiftDailyAssociation empShift = new EmployeeShiftDailyAssociation();
       empShift.setId(trans.getId());
       System.out.println("inside processor " + empShift.toString());
        return empShift;
    }

}
