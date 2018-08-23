package com.n26.concurrent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import com.n26.entity.TransactionStatisticsCollator;
import com.n26.entity.model.Transaction;

public class TransactionStatisticsCollatorThread implements Runnable {
	
	
	private CountDownLatch latch;
	private TransactionStatisticsCollator transactionStatisticsCollator;
	private int i;
	private long timestamp;
	
	public TransactionStatisticsCollatorThread(int i, long timestamp, TransactionStatisticsCollator transactionStatisticsCollator, CountDownLatch latch) {
		this.i = i;
		this.transactionStatisticsCollator = transactionStatisticsCollator;
		this.latch = latch;
		this.timestamp = timestamp;
	}
	
	@Override
	public void run() {
		try {
			Transaction transaction = new Transaction();
			transaction.setAmount(new BigDecimal(i * 100));
			transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(timestamp - (i + i * 100))));
			transactionStatisticsCollator.merge(transaction);
			this.latch.countDown();
		} catch(Exception ex) {
			throw new Error(ex);
		}
	}
}
