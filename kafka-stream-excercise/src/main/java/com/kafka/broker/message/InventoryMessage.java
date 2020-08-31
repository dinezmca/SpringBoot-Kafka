package com.kafka.broker.message;

import java.time.LocalDateTime;

import com.kafka.util.LocalDateTimeDeserializer;
import com.kafka.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class InventoryMessage {

	private String item;
	private String location;
	private long quantity;
	private String type;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime transactionTime;

	public String getItem() {
		return item;
	}

	public String getLocation() {
		return location;
	}

	public long getQuantity() {
		return quantity;
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public String getType() {
		return type;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "InventoryMessage [item=" + item + ", location=" + location + ", quantity=" + quantity + ", type=" + type
				+ ", transactionTime=" + transactionTime + "]";
	}

}
