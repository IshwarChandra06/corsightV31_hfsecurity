package com.eikona.mata.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.eikona.mata.service.impl.model.ShiftServiceImpl;

//@WebMvcTest(ShiftController.class)
//@SpringBootTest
@ActiveProfiles("test")
public class ShiftControllerTest {

	//@MockBean
	@Autowired
	private ShiftServiceImpl shiftServiceImpl;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void shiftListPage() throws Exception{
		this.mockMvc.perform(MockMvcRequestBuilders.get("/shift"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("shift/shift_list"));
	}
}
