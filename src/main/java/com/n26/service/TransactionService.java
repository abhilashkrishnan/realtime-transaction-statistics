package com.n26.service;

import com.n26.entity.model.Transaction;

public interface TransactionService {
	void addTransaction(Transaction transaction);
	void deleteTransactions();
}
