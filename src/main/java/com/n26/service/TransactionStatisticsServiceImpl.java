package com.n26.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n26.entity.TransactionContainer;
import com.n26.entity.TransactionStatisticsAggregator;
import com.n26.entity.model.TransactionStatistics;

@Service
public class TransactionStatisticsServiceImpl implements TransactionStatisticsService {

	@Autowired
	private TransactionContainer transactionContainer;

	@Override
	public TransactionStatistics getTransactionStatistics() {
		List<TransactionStatisticsAggregator> transactionStatisticsAggregators = getTransactionStatisticsAggregator();
		
		TransactionStatistics transactionStatistics = new TransactionStatistics();
		transactionStatisticsAggregators.forEach(t -> t.mergeToResult(transactionStatistics));
		return transactionStatistics;
	}
	
	private List<TransactionStatisticsAggregator> getTransactionStatisticsAggregator() {
		long currentTimestamp = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
		return transactionContainer.getValidTransactionStatisticsAggregator(currentTimestamp);
	}

}
