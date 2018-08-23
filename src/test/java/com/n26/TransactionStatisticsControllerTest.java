package com.n26;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

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

import com.n26.entity.TransactionContainer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TransactionStatisticsControllerTest extends TransactionTest {

	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired
	private TransactionContainer container;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		container.clear();
	}
	
	@Test
	public void emptyStatistics() throws Exception {
		this.mockMvc.perform(get("/statistics").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).
				andExpect(jsonPath("$.count", is(0)));
				
	}
	
	@Test
	public void multipleTransactionStatistics() throws Exception {
		IntStream.range(0, 100).forEach(i -> {
	        try {
	        	Instant timestamp = Instant.now();
	    		String json = createValidTransactionJson(DateTimeFormatter.ISO_INSTANT.format(timestamp));
	    		this.mockMvc.perform(post("/transactions").
	    				contentType(MediaType.APPLICATION_JSON).
	    				content(json)).andExpect(status().isCreated());
			} catch (Exception ex) {
				throw new Error(ex);
			}
	    });
		
		this.mockMvc.perform(get("/statistics")
		   		.accept(MediaType.APPLICATION_JSON))
		   		.andExpect(status().isOk())
		   		.andDo(print())
		   		.andExpect(jsonPath("$.count", is(100))); 
		
	}
	
	@Test
	public void statisticsNotEmpty() throws Exception {

		Instant timestamp = Instant.now();
		String json = createValidTransactionJson(DateTimeFormatter.ISO_INSTANT.format(timestamp));
		this.mockMvc.perform(post("/transactions").
				contentType(MediaType.APPLICATION_JSON).
				content(json)).andExpect(status().isCreated());
   	
	   	this.mockMvc.perform(get("/statistics")
	   		.accept(MediaType.APPLICATION_JSON))
	   		.andExpect(status().isOk())
	   		.andDo(print())
	   		.andExpect(jsonPath("$.count", is(1))); 
	}
	
}
