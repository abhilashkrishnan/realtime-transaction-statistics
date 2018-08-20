package com.n26.entity.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode
@ToString
public class Transaction {
	
	
	@JsonProperty("amount")
	@NotNull
	private BigDecimal amount;
	
	
	@JsonProperty("timestamp")
	@NotNull
	private LocalDateTime timestamp;
	
}
