package com.n26.controller.validator;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.n26.controller.exception.FutureDateException;
import com.n26.controller.exception.OlderDateException;
import com.n26.controller.exception.TimestampParseException;
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
	 * Validator for transaction timestamp. If transaction is older than 60 seconds
	 * throw OlderDateException otherwise if transaction is in future date throw
	 * FutureDateException
	 * @param Object to be validated in this case Transaction
	 * @param errors Not in use
	 */
	@Override
	public void validate(Object target, Errors errors) {
		Transaction transaction = (Transaction) target;
		long currentTimestamp = Instant.now().toEpochMilli();
		
		try {
			long txnTimestamp = Instant.parse(transaction.getTimestamp()).toEpochMilli();
	
			if (txnTimestamp < (currentTimestamp - maxTimeMillsToKeep))
				throw new OlderDateException();
			else if (txnTimestamp > currentTimestamp)
				throw new FutureDateException();
		}  catch(DateTimeParseException ex) {
			throw new TimestampParseException(); 
		}
	}

}
