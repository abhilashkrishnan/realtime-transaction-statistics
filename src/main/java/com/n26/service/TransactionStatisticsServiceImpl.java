package com.n26.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n26.entity.TransactionContainer;
import com.n26.entity.TransactionStatisticsCollator;
import com.n26.entity.model.TransactionStatistics;

@Service
public class TransactionStatisticsServiceImpl implements TransactionStatisticsService {

	@Autowired
	private TransactionContainer transactionContainer;

	/**
	 * Retrieve the transaction statistics collator
	 * @return Transaction statistics collator containing the Transaction Statistics
	 */
	@Override
	public TransactionStatistics getTransactionStatistics() {
		List<TransactionStatisticsCollator> transactionStatisticsCollator = getTransactionStatisticsCollator();
		
		TransactionStatistics transactionStatistics = new TransactionStatistics();
		
		if (!transactionStatisticsCollator.isEmpty())
			transactionStatisticsCollator.forEach(t -> t.mergeToResult(transactionStatistics));
		else {
			//Clear the MAX and MIN values to Zero
			transactionStatistics.clear();
		}
		return transactionStatistics;
	}
	
	/**
	 * Retrieve the valid transaction statistics collator
	 * @return List of valid transaction statistics collator
	 */
	private List<TransactionStatisticsCollator> getTransactionStatisticsCollator() {
		long currentTimestamp = Instant.now().toEpochMilli();
		return transactionContainer.getValidTransactionStatisticsCollator(currentTimestamp);
	}

}
