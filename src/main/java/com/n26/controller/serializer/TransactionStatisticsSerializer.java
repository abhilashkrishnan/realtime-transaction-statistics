package com.n26.controller.serializer;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.n26.entity.model.TransactionStatistics;

@JsonComponent
public class TransactionStatisticsSerializer extends JsonSerializer<TransactionStatistics> {
	
	/**
	 * Serialize Transaction object to JSON string in response to /statistics GET request
	 * @param TransactionStatistics object
	 * @param Json string generator
	 * @param provider
	 */
	@Override
	public void serialize(TransactionStatistics value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeStartObject();
		gen.writeStringField("sum", value.getSum().toPlainString());
		gen.writeStringField("avg", value.getAvg().toPlainString());
		gen.writeStringField("max", value.getMax().toPlainString());
		gen.writeStringField("min", value.getMin().toPlainString());
		gen.writeNumberField("count", value.getCount());
		gen.writeEndObject();
	}

}
