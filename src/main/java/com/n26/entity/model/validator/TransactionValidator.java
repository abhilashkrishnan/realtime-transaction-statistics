package com.n26.entity.model.validator;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.n26.controller.exception.FutureDateException;
import com.n26.controller.exception.OlderDateException;
import com.n26.entity.model.Transaction;

@Component
public class TransactionValidator implements Validator {

	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;

	@Override
	public boolean supports(Class<?> clazz) {
		return Transaction.class.isAssignableFrom(clazz);
	}

	/**
	 * Validator for transaction timestamp. If tranaction is older than 60 seconds
	 * throw OlderDateException otherwise if transaction is in future date throw
	 * FutureDateException
	 * @param Object to be validated in this case Transaction
	 * @param errors Not used
	 */
	@Override
	public void validate(Object target, Errors errors) {
		Transaction transaction = (Transaction) target;
		long currentTimestamp = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli(); // Instant.now().toEpochMilli();
		long txnTimestamp = transaction.getTimestamp().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

		if (txnTimestamp < (currentTimestamp - maxTimeMillsToKeep))
			throw new OlderDateException();
		else if (txnTimestamp > currentTimestamp)
			throw new FutureDateException();
	}

}
