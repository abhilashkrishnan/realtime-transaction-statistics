package com.n26;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

import com.n26.concurrent.TransactionServiceThread;
import com.n26.entity.TransactionContainer;
import com.n26.entity.TransactionStatisticsCollator;
import com.n26.entity.model.Transaction;
import com.n26.service.TransactionService;
import com.n26.util.DecimalUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionServiceTest extends TransactionTest  {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionContainer transactionContainer;
	
	@Before
	public void setup() {
		transactionContainer.clear();
	}
	
	@Test
	public void addTransaction() throws Exception {
		
		Instant now = Instant.now();
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal(100.68));
		transaction.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(now));
		transactionService.addTransaction(transaction);
		
		long timestamp = Instant.now().toEpochMilli();
		List<TransactionStatisticsCollator> collators = transactionContainer.getValidTransactionStatisticsCollators(timestamp);
		
		assertEquals(1, collators.size());
		TransactionStatisticsCollator collator = collators.get(0);
		
		assertNotNull(collator.getTransactionStatistics());
		
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), collator.getTransactionStatistics().getMax());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), collator.getTransactionStatistics().getMin());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), collator.getTransactionStatistics().getSum());
		assertEquals(DecimalUtils.round(new BigDecimal(100.68), 2), collator.getTransactionStatistics().getAvg());
	}
	
	@Test
	public void testConcurrentTransactions() throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(100);
		long timestamp = Instant.now().toEpochMilli();
		try {
			IntStream.range(0, 100).forEach(i -> {
				executor.execute(new TransactionServiceThread(i, timestamp, transactionService, latch));
			});
			
			latch.await();
		} catch (InterruptedException ex) {
			throw new Error(ex);
		} finally {
			executor.shutdown();
		}
		
		try {
			Thread.sleep(2000); 
		} catch (InterruptedException e) {
			//Eat me
		}
		
		List<TransactionStatisticsCollator> collators = transactionContainer.getValidTransactionStatisticsCollators(timestamp);
		
		assertNotNull(collators);
		
		int total = 0;
		
		for (TransactionStatisticsCollator collator : collators) {
			total += collator.getTransactionStatistics().getCount();
		}
		
		assertEquals(100, total);
	}
	
	@Test
	public void deleteTransactions() throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(100);
		long timestamp = Instant.now().toEpochMilli();
		try {
			IntStream.range(0, 100).forEach(i -> {
				executor.execute(new TransactionServiceThread(i, timestamp, transactionService, latch));
			});
			
			latch.await();
		} catch (InterruptedException ex) {
			throw new Error(ex);
		} finally {
			executor.shutdown();
		}
		
		try {
			Thread.sleep(2000); 
		} catch (InterruptedException e) {
			//Eat me
		}
		
		transactionService.deleteTransactions();
		
		long now = Instant.now().toEpochMilli();
		List<TransactionStatisticsCollator> collators = transactionContainer.getValidTransactionStatisticsCollators(now);
		
		assertEquals(0, collators.size());
	}
}
