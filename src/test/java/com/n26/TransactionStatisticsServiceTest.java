package com.n26;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.n26.concurrent.TransactionStatisticsServiceThread;
import com.n26.entity.TransactionContainer;
import com.n26.entity.model.Transaction;
import com.n26.entity.model.TransactionStatistics;
import com.n26.service.TransactionService;
import com.n26.service.TransactionStatisticsServiceImpl;
import com.n26.util.DecimalUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionStatisticsServiceTest {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionStatisticsServiceImpl transactionStatisticsService;
	
	@Autowired
	private TransactionContainer transactionContainer;
	
	@Before
	public void setup() {
		transactionContainer.clear();
	}
	
	@Test
	public void getTransactionStatistics() throws Exception {
		
		Instant now = Instant.now();
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal(100.68));
		transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(now));
		transactionService.addTransaction(transaction);
		
		TransactionStatistics transactionStatistics = transactionStatisticsService.getTransactionStatistics();
		assertNotNull(transactionStatistics);
		
		assertEquals(1, transactionStatistics.getCount());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getAvg());
	}
	
	@Test
	public void getTransactionStatisticsOfConcurrentTransactions() throws Exception {
		
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(10);
		long timestamp = Instant.now().toEpochMilli();
		try {
			IntStream.range(0, 10).forEach(i -> {
				executor.execute(new TransactionStatisticsServiceThread(i, timestamp, transactionContainer, latch));
			});
			
			latch.await();
		} finally {
			executor.shutdown();
		}
		
		TransactionStatistics transactionStatistics = transactionStatisticsService.getTransactionStatistics();
		assertNotNull(transactionStatistics);
		
		assertEquals(10, transactionStatistics.getCount());
		assertEquals(DecimalUtils.round(new BigDecimal(0), 2), transactionStatistics.getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(900), 2), transactionStatistics.getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(4500), 2), transactionStatistics.getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(450), 2), transactionStatistics.getAvg());
	}
	
	
	@Test
	public void emptyTransactionStatistics() throws Exception {
		TransactionStatistics transactionStatistics = transactionStatisticsService.getTransactionStatistics();
		
		assertNotNull(transactionStatistics);
		assertEquals(0, transactionStatistics.getCount());
		
		assertEquals(DecimalUtils.round(new BigDecimal(0.00), 2), transactionStatistics.getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(0.00), 2), transactionStatistics.getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(0.00), 2), transactionStatistics.getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(0.00), 2), transactionStatistics.getAvg());
	}
}
