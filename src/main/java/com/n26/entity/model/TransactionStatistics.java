package com.n26.entity.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.controller.serializer.TransactionStatisticsSerializer;
import com.n26.util.DecimalUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode
@ToString
@JsonSerialize(using = TransactionStatisticsSerializer.class)
public class TransactionStatistics {
	
	@JsonProperty("sum")
	private BigDecimal sum = DecimalUtils.round(new BigDecimal(0.00), 2);
	
	@JsonProperty("avg")
	private BigDecimal avg = DecimalUtils.round(new BigDecimal(0.00), 2);
	
	@JsonProperty("max")
	private BigDecimal max = DecimalUtils.round(new BigDecimal(Double.MIN_VALUE), 2);
	
	@JsonProperty("min")
	private BigDecimal min = DecimalUtils.round(new BigDecimal(Double.MAX_VALUE), 2);
	
	@JsonProperty("count")
	private long count = 0;
	
	public void reset() {
		this.sum = DecimalUtils.round(new BigDecimal(0.00), 2);
		this.avg = DecimalUtils.round(new BigDecimal(0.00), 2);
		this.max = DecimalUtils.round(new BigDecimal(Double.MIN_VALUE), 2);
		this.min = DecimalUtils.round(new BigDecimal(Double.MAX_VALUE), 2);
		this.count = 0;
	}
}
