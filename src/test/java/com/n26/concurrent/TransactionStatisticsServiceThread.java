package com.n26.concurrent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import com.n26.entity.TransactionContainer;
import com.n26.entity.model.Transaction;

public class TransactionStatisticsServiceThread implements Runnable {
	private int i;
	private CountDownLatch latch;
	private TransactionContainer transactionContainer;
	private long timestamp;
	
	public TransactionStatisticsServiceThread(int i, long timestamp, TransactionContainer transactionContainer, CountDownLatch latch) {
		this.i = i;
		this.transactionContainer = transactionContainer;
		this.latch = latch;
		this.timestamp = timestamp;
	}

	@Override
	public void run() {
		try {
			Transaction transaction = new Transaction();
			transaction.setAmount(new BigDecimal(i * 100));
			transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(timestamp - (i + i * 100))));
			transactionContainer.addTransaction(transaction, timestamp);
			this.latch.countDown();
		} catch(Exception ex) {
			throw new Error(ex);
		}
	}

}
