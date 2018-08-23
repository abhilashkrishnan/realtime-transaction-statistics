package com.n26.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.n26.entity.model.Transaction;
import com.n26.entity.model.TransactionStatistics;
import com.n26.util.DecimalUtils;

/**
 * Placeholder for Transaction Statistics
 */
public class TransactionStatisticsCollator {

	private final ReadWriteLock lock;
	private TransactionStatistics transactionStatistics;
	private long timestamp;

	public TransactionStatisticsCollator() {
		this.lock = new ReentrantReadWriteLock();
		this.transactionStatistics = new TransactionStatistics();
	}

	public ReadWriteLock getLock() {
		return this.lock;
	}

	/**
	 * Create a new transaction
	 * @param New transaction
	 */
	public void createTransaction(Transaction transaction) {
		this.transactionStatistics.setMax(DecimalUtils.round(transaction.getAmount(), 2));
		this.transactionStatistics.setMin(DecimalUtils.round(transaction.getAmount(), 2));
		this.transactionStatistics.setSum(DecimalUtils.round(transaction.getAmount(), 2));
		this.transactionStatistics.setAvg(DecimalUtils.round(transaction.getAmount(), 2));
		this.transactionStatistics.setCount(1);
		this.timestamp = Instant.parse(transaction.getTimestamp()).toEpochMilli();
	}

	/**
	 * Merge transaction to the collator if transaction index is already occupied
	 * with existing transaction(s) since timestamp is same for the transactions at
	 * this index
	 * @param Transaction to be merged
	 */
	public void merge(Transaction transaction) {
		if (transaction.getAmount().compareTo(getTransactionStatistics().getMin()) == -1)
			getTransactionStatistics().setMin(DecimalUtils.round(transaction.getAmount(), 2));
		if (transaction.getAmount().compareTo(getTransactionStatistics().getMax()) == 1)
			getTransactionStatistics().setMax(DecimalUtils.round(transaction.getAmount(), 2));

		getTransactionStatistics()
				.setSum(DecimalUtils.round(getTransactionStatistics().getSum().add(transaction.getAmount()), 2));
		getTransactionStatistics().setCount(getTransactionStatistics().getCount() + 1);
		getTransactionStatistics().setAvg(getTransactionStatistics().getSum()
				.divide(BigDecimal.valueOf(this.transactionStatistics.getCount()), 2, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * Fetch the transaction statistics and merge to the final result. 
	 * Thread-safe operation for concurrent reads.
	 * @param TransactionStatistics result to the merged
	 */
	public void mergeToResult(TransactionStatistics result) {
		try {
			getLock().readLock().lock();

			if (result.getMin().compareTo(getTransactionStatistics().getMin()) == 1)
				result.setMin(DecimalUtils.round(getTransactionStatistics().getMin(), 2));

			if (result.getMax().compareTo(getTransactionStatistics().getMax()) == -1)
				result.setMax(DecimalUtils.round(getTransactionStatistics().getMax(), 2));

			result.setSum(DecimalUtils.round(result.getSum().add(getTransactionStatistics().getSum()), 2));
			result.setCount(result.getCount() + getTransactionStatistics().getCount());
			result.setAvg(
					result.getSum().divide(DecimalUtils.round(new BigDecimal(Double.valueOf(result.getCount())), 2), 2,
							BigDecimal.ROUND_HALF_UP));

		} finally {
			getLock().readLock().unlock();
		}
	}

	public TransactionStatistics getTransactionStatistics() {
		return this.transactionStatistics;
	}

	public boolean isEmpty() {
		return this.transactionStatistics.getCount() == 0;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void reset() {
		this.transactionStatistics.reset();
		this.timestamp = 0;
	}
}
