package com.n26.entity;

import java.util.List;

import com.n26.entity.model.Transaction;

public interface TransactionContainer {
	void addTransaction(Transaction transaction, long currentTimestamp);
	List<TransactionStatisticsCollator> getValidTransactionStatisticsCollator(long currentTimestamp);
	void clear();
}
