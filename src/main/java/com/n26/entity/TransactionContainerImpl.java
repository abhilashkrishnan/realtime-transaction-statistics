package com.n26.entity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.n26.entity.model.Transaction;

/**
 * Transaction Container which holds the transaction statistics collator objects. 
 * Transactions are added to the transaction statistics collator array. 
 */
@Component
public class TransactionContainerImpl implements TransactionContainer {

	private TransactionStatisticsCollator[] transactionStatisticsCollator;

	/**
	 * Max time - 60000 ms (60 secs) to keep the transaction statistics collator in the
	 * container array.
	 */
	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;

	/**
	 * Time interval in this case 1000 ms (1 sec) which is will be used in
	 * conjunction with transaction time, current time in ms and max time in ms
	 * (60000 ms) to calculate the container array index of the transaction
	 * statistics collator.
	 */
	@Value("${time.mills.interval}")
	private int timeMillsInterval;

	private TransactionContainerImpl() {

	}

	/**
	 * Create the Transaction Statistics Collator array of length which is max time in
	 * seconds (60 secs)
	 */
	@PostConstruct
	private void postConstruct() {
		this.transactionStatisticsCollator = new TransactionStatisticsCollator[maxTimeMillsToKeep
				/ timeMillsInterval];
		this.initCollator();
	}

	private void initCollator() {
		for (int i = 0; i < this.transactionStatisticsCollator.length; i++)
			this.transactionStatisticsCollator[i] = new TransactionStatisticsCollator();
	}

	/**
	 * Add Transaction to the container
	 * 
	 * @param Transaction to be added
	 * @param Current timestamp to verify whether transaction is within time constraints
	 */
	@Override
	public void addTransaction(Transaction transaction, long currentTimestamp) {
		this.collate(transaction, currentTimestamp);
	}

	@Override
	public List<TransactionStatisticsCollator> getValidTransactionStatisticsCollator(long currentTimestamp) {
		return Arrays.stream(this.transactionStatisticsCollator)
				.filter(t -> isValidTransaction(t.getTimestamp(), currentTimestamp)).collect(Collectors.toList());
	}

	@Override
	public void clear() {
		this.initCollator();
	}

	/**
	 * Collate the transaction into the container.
	 * Thread-safe operation for concurrent writes.
	 * @param Transaction to be added
	 * @param Current timestamp which will be availed to matched the validity of the transaction
	 */
	private void collate(Transaction transaction, long currentTimestamp) {
		int index = getTransactionIndex(transaction);

		TransactionStatisticsCollator txnStatisticsCollator = transactionStatisticsCollator[index];

		try {
			txnStatisticsCollator.getLock().writeLock().lock();

			if (txnStatisticsCollator.isEmpty())
				txnStatisticsCollator.createTransaction(transaction);
			else {
				if (isValidTransaction(txnStatisticsCollator.getTimestamp(), currentTimestamp))
					txnStatisticsCollator.merge(transaction);
				else {
					txnStatisticsCollator.reset();
					txnStatisticsCollator.createTransaction(transaction);
				}
			}

		} finally {
			txnStatisticsCollator.getLock().writeLock().unlock();
		}
	}

	/**
	 * Determine the transaction index based on the formula - ((Current Time -
	 * Transaction Time) / Time Interval (1 sec)) % ( Max Time (60 sec) / Time
	 * Interval (1 sec))
	 * 
	 * @param Transaction whose index has to be determined
	 * @return Index of the transaction
	 */
	private int getTransactionIndex(Transaction transaction) {
		
		long txnTime = Instant.parse(transaction.getTimestamp()).toEpochMilli();

		long currTime = Instant.now().toEpochMilli(); 

		int index = (int) ((currTime - txnTime) / timeMillsInterval) % (maxTimeMillsToKeep / timeMillsInterval);
		
		return index;
	}

	private boolean isValidTransaction(long txnTimestamp, long currentTimestamp) {
		return txnTimestamp >= (currentTimestamp - maxTimeMillsToKeep);
	}
}
