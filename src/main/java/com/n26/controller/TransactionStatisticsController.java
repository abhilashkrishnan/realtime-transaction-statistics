package com.n26.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.entity.model.TransactionStatistics;
import com.n26.service.TransactionStatisticsService;

@RestController
public class TransactionStatisticsController {

	@Autowired
	private TransactionStatisticsService transactionStatisticsService;
	
	/**
	 * Retrieve the last 60 seconds transactions
	 * @return Transaction Statistics from  last 60 seconds
	 */
	@GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
	public TransactionStatistics getStatistics() {
		return transactionStatisticsService.getTransactionStatistics();
	}
}
