package com.kafka.broker.message;


import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kafka.util.LocalDateTimeDeserializer;
import com.kafka.util.LocalDateTimeSerializer;

public class OrderMessage {

	private String creditCardNumber;
	
	private String itemName;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime orderDateTime;

	private String orderLocation;

	private String orderNumber;

	private int price;

	private int quantity;
	
	public OrderMessage copy() {
		OrderMessage copy = new OrderMessage();

		copy.setCreditCardNumber(this.creditCardNumber);
		copy.setItemName(this.itemName);
		copy.setOrderDateTime(this.orderDateTime);
		copy.setOrderLocation(this.orderLocation);
		copy.setOrderNumber(this.orderNumber);
		copy.setPrice(this.price);
		copy.setQuantity(this.quantity);

		return copy;
	}
	

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public String getItemName() {
		return itemName;
	}

	public LocalDateTime getOrderDateTime() {
		return orderDateTime;
	}

	public String getOrderLocation() {
		return orderLocation;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public int getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setOrderDateTime(LocalDateTime orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public void setOrderLocation(String orderLocation) {
		this.orderLocation = orderLocation;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderMessage [orderLocation=" + orderLocation + ", orderNumber=" + orderNumber + ", creditCardNumber="
				+ creditCardNumber + ", orderDateTime=" + orderDateTime + ", itemName=" + itemName + ", price=" + price
				+ ", quantity=" + quantity + "]";
	}
}
