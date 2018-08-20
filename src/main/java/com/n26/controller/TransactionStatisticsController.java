package com.n26.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.entity.model.TransactionStatistics;
import com.n26.service.TransactionStatisticsService;
import com.n26.util.DecimalUtils;

@RestController
public class TransactionStatisticsController {

	@Autowired
	private TransactionStatisticsService transactionStatisticsService;
	
	@GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionStatistics> getStatistics() {
		TransactionStatistics stats = transactionStatisticsService.getTransactionStatistics();
		
		if (stats.getCount() == 0) {
			BigDecimal min = stats.getMin();
			min = DecimalUtils.round(new BigDecimal(0.00), 2);
			stats.setMin(min);
		}
		
		return new ResponseEntity<TransactionStatistics>(stats, HttpStatus.OK);
	}
}
