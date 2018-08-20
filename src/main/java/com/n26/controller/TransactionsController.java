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
	
	/**
	 * Maximum time to keep the transaction entries
	 */
	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;
	
	@Autowired
	private TransactionsService transactionsService;
	
	@Autowired
	private TransactionValidator transactionValidator;
	
	/**
	 * POST request for creating transactions
	 * @param Transaction to be created 
	 * @return HTTP Status 201 on successful creation of transaction
	 */
	@PostMapping("/transactions")
	public ResponseEntity<?> addTransaction(@RequestBody(required = true) @Valid Transaction transaction) {
		transactionsService.addTransaction(transaction);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/**
	 * Delete transactions from the container
	 * @return HTTP Status 200 on successful completion
	 */
	@DeleteMapping("/transactions")
	public ResponseEntity<?> deleteTransactions() {
		transactionsService.deleteTransactions();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Binding transaction validator to the controller
	 * @param Spring framework binder
	 */
	@InitBinder("transaction")
	public void setupBinder(WebDataBinder binder) {
	    binder.addValidators(transactionValidator);
	}
}
 