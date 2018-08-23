package com.n26.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n26.entity.TransactionContainer;
import com.n26.entity.model.Transaction;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionContainer transactionContainer; 

	/**
	 * Add the transaction to the container
	 * @param Transcation to be added
	 */
	@Override
	public void addTransaction(Transaction transaction) {
		long currentTimestamp = Instant.now().toEpochMilli();
		transactionContainer.addTransaction(transaction, currentTimestamp);
	}

	/**
	 * Delete the transactions from the container 
	 */
	@Override
	public void deleteTransactions() {
		transactionContainer.clear();
	}

}
