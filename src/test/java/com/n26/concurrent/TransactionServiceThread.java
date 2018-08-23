package com.n26.concurrent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import com.n26.entity.model.Transaction;
import com.n26.service.TransactionService;

public class TransactionServiceThread implements Runnable {
	
	private int i;
	private TransactionService transactionService;
	private CountDownLatch latch;
	private long timestamp;
	
	public TransactionServiceThread(int i, long timestamp, TransactionService transactionService, CountDownLatch latch) {
		this.i = i;
		this.transactionService = transactionService;
		this.latch = latch;
		this.timestamp = timestamp;
	}
	
	@Override
	public void run() {
		try {
			Transaction transaction = new Transaction();
			transaction.setAmount(new BigDecimal(i * 100));
			transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(timestamp - (i + i * 100))));
			transactionService.addTransaction(transaction);
			this.latch.countDown();
		} catch(Exception ex) {
			throw new Error(ex);
		}
	}

}
