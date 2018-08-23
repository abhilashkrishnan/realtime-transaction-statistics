package com.n26;

public class TransactionTest {

	protected String createValidTransactionJson(String timestamp){
    	return String.format("{\"amount\": \"15.5\", \"timestamp\":  \"%s\"}", timestamp);
	}
	
	protected String createInvalidTransactionJson(String timestamp){
    	return String.format("\"Hello world!\"}", timestamp);
	}
	
	protected String createUnparsableTransactionJson(String timestamp){
		return String.format("{\"timestamp\": \"%s\", \"amount\": \"One hundred\"}", timestamp);
	}
}
