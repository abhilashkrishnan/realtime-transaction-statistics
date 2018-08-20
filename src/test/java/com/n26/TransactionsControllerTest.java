package com.n26;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TransactionsControllerTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@Test
	public void testInvalidTimestamp() throws Exception {
		Instant timestamp = Instant.now().plusMillis(-60000);
		String json = createTransactionJson(DateTimeFormatter.ISO_INSTANT.format(timestamp));
		System.out.println("Json: " + json);
		this.mockMvc.perform(post("/transactions").
				contentType(MediaType.APPLICATION_JSON).
				content(json)).andDo(print()).andExpect(status().isNoContent());
		
	}
	
	@Test
	public void testValidTimestamp() throws Exception {
		Instant timestamp = Instant.now();
		String json = createTransactionJson(DateTimeFormatter.ISO_INSTANT.format(timestamp));
		System.out.println("Json: " + json);
		this.mockMvc.perform(post("/transactions").
				contentType(MediaType.APPLICATION_JSON).
				content(json)).andDo(print()).andExpect(status().isCreated());
	}
	
	protected String createTransactionJson(String timestamp){
    	return String.format("{\"amount\": \"15.5\", \"timestamp\":  \"%s\"}", timestamp);
	}
	
}
