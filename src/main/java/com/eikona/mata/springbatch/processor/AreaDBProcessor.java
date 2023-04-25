package com.eikona.mata.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;

@Component
public class AreaDBProcessor implements  ItemProcessor<Area, Area>{
	
	@Override
    public Area process(Area areaObj) throws Exception {
		Area area = new Area();
		area.setId(areaObj.getId());
       System.out.println("inside processor " + area.toString());
        return area;
    }
}

