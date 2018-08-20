package com.n26.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.n26.entity.model.Transaction;

/**
 * TransactionStatisticsAggregator container which holds the transaction statistics
 * @author Abhilash Krishnan
 */
@Component
public class TransactionContainerImpl implements TransactionContainer {

	private TransactionStatisticsAggregator[] transactionStatisticsAggregator;
	
	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;
	
	@Value("${time.mills.interval}")
	private int timeMillsInterval;
	
	private TransactionContainerImpl() {
	
	}
	
	/**
	 * Create the Transaction Aggregator array of length max time in seconds (60 secs)
	 */
	@PostConstruct
	private void postConstruct() {
		this.transactionStatisticsAggregator = new TransactionStatisticsAggregator[maxTimeMillsToKeep / timeMillsInterval];
		this.initAggregator();
	}
	
	private void initAggregator() {
		for (int i = 0; i < this.transactionStatisticsAggregator.length; i++)
			this.transactionStatisticsAggregator[i] = new TransactionStatisticsAggregator();
	}
	
	@Override
	public void addTransaction(Transaction transaction, long currentTimestamp) {
		this.aggregate(transaction, currentTimestamp);
	}

	@Override
	public List<TransactionStatisticsAggregator> getValidTransactionStatisticsAggregator(long currentTimestamp) {
		return Arrays.stream(this.transactionStatisticsAggregator).
				filter(t -> isValidTransaction(t.getTimestamp(), currentTimestamp)).
				collect(Collectors.toList());
	}

	@Override
	public void clear() {
		this.initAggregator();
	}
	
	/**
	 * Aggregate the transaction to the container
	 * @param Transaction to be added
	 * @param Current Timestamp which will be availed to matched the validity of the transaction 
	 */
	private void aggregate(Transaction transaction, long currentTimestamp) {
		int index = getTransactionIndex(transaction);
		
		TransactionStatisticsAggregator txnStatisticsAggregator = transactionStatisticsAggregator[index];
		
		try {
			txnStatisticsAggregator.getLock().writeLock().lock();
			
			if (txnStatisticsAggregator.isEmpty())
				txnStatisticsAggregator.createTransaction(transaction);
			else {
				if (isValidTransaction(txnStatisticsAggregator.getTimestamp(), currentTimestamp)) 
					txnStatisticsAggregator.merge(transaction);
				else {
					txnStatisticsAggregator.reset();
					txnStatisticsAggregator.createTransaction(transaction);
				}
			}
			
		} finally {
			txnStatisticsAggregator.getLock().writeLock().unlock();
		}
	}
	
	/**
	 * Determine the transaction index based on the formula -
	 * ((Current Time - Transaction Time) / Time Interval (1 sec)) % ( Max Time (60 sec) / Time Interval (1 sec)) 
	 * @param Transaction whose index has to be determined
	 * @return Index of the transaction
	 */
	private int getTransactionIndex(Transaction transaction) {
		long txnTime = transaction.getTimestamp().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
		
		long currTime = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();//Instant.now().toEpochMilli();
		
		int index =  (int) (((currTime - txnTime) / timeMillsInterval) % (maxTimeMillsToKeep / timeMillsInterval));
		return index;
	}
	
	private boolean isValidTransaction(long txnTimestamp, long currentTimestamp) {
		return txnTimestamp >= (currentTimestamp - maxTimeMillsToKeep);
	}
}
