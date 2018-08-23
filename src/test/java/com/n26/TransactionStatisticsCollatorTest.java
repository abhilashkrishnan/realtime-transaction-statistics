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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.n26.concurrent.TransactionStatisticsCollatorThread;
import com.n26.entity.TransactionStatisticsCollator;
import com.n26.entity.model.Transaction;
import com.n26.entity.model.TransactionStatistics;
import com.n26.util.DecimalUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionStatisticsCollatorTest {

	private TransactionStatisticsCollator transactionStatisticsCollator;
	
	@Before
	public void setup() {
		this.transactionStatisticsCollator = new TransactionStatisticsCollator();
	}
	
	@Test
	public void createTransaction() throws Exception {
		Instant now = Instant.now();
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal(100.68));
		transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(now));
		transactionStatisticsCollator.createTransaction(transaction);
		
		TransactionStatistics transactionStatistics = transactionStatisticsCollator.getTransactionStatistics();
	
		assertNotNull(transactionStatistics);
	
		assertEquals(1, transactionStatistics.getCount());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getAvg());
	}
	
	
	@Test
	public void merge() throws Exception {
		Instant now = Instant.now();
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal(100.68));
		transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(now));
		transactionStatisticsCollator.merge(transaction);
		
		TransactionStatistics transactionStatistics = transactionStatisticsCollator.getTransactionStatistics();
	
		assertNotNull(transactionStatistics);
	
		assertEquals(1, transactionStatistics.getCount());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), transactionStatistics.getAvg());
	}
	
	@Test
	public void mergeConcurrentTransactions() throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(100);
		long timestamp = Instant.now().toEpochMilli();
		try {
			IntStream.range(0, 100).forEach(i -> {
				executor.execute(new TransactionStatisticsCollatorThread(i, timestamp, transactionStatisticsCollator, latch));
			});
			
			latch.await();
		} finally {
			executor.shutdown();
		}
		
		TransactionStatistics transactionStatistics = transactionStatisticsCollator.getTransactionStatistics();
		
		assertNotNull(transactionStatistics);
	
		assertEquals(true, transactionStatistics.getCount() > 0);
	}
	
	@Test
	public void mergeTransactionStatisticsToResult() throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(100);
		long timestamp = Instant.now().toEpochMilli();
		try {
			IntStream.range(0, 100).forEach(i -> {
				TransactionStatisticsCollatorThread thread = new TransactionStatisticsCollatorThread(i, timestamp, transactionStatisticsCollator, latch);
				executor.execute(thread);
			});
			
			latch.await();
		} finally {
			executor.shutdown();
		}
		
		TransactionStatistics transactionStatistics = new TransactionStatistics();
		
		transactionStatisticsCollator.mergeToResult(transactionStatistics);
		
		assertEquals(true, transactionStatistics.getCount() > 0);
	}
}
