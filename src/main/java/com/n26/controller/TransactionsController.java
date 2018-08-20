package com.n26.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.n26.entity.model.Transaction;
import com.n26.entity.model.validator.TransactionValidator;
import com.n26.service.TransactionsService;

@RestController
public class TransactionsController {
	
	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;
	
	@Autowired
	private TransactionsService transactionsService;
	
	@Autowired
	private TransactionValidator transactionValidator;
	
	@PostMapping("/transactions")
	public ResponseEntity<?> addTransaction(@RequestBody(required = true) @Valid Transaction transaction) {
		transactionsService.addTransaction(transaction);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@DeleteMapping("/transactions")
	public ResponseEntity<?> deleteTransactions() {
		transactionsService.deleteTransactions();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@InitBinder("transaction")
	public void setupBinder(WebDataBinder binder) {
	    binder.addValidators(transactionValidator);
	}
}
 