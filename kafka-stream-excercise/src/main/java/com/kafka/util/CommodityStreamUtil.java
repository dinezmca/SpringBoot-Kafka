package com.kafka.util;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;

import com.kafka.broker.message.OrderMessage;
import com.kafka.broker.message.OrderPatternMessage;
import com.kafka.broker.message.OrderRewardMessage;

public class CommodityStreamUtil {
	
	public static OrderMessage maskCreditCard(OrderMessage original) {
		
		OrderMessage converted = original.copy();
		String maskedcard = original.getCreditCardNumber().replaceFirst("\\d{12}", 
				StringUtils.repeat("*", 12));
		converted.setCreditCardNumber(maskedcard);
		return converted;
	}
	
	public static OrderPatternMessage mapToOrderPattern(OrderMessage original) {

		OrderPatternMessage result = new OrderPatternMessage();

		result.setItemName(original.getItemName());
		result.setOrderDateTime(original.getOrderDateTime());
		result.setOrderLocation(original.getOrderLocation());
		result.setOrderNumber(original.getOrderNumber());

		long totalItemAmount = original.getPrice() * original.getQuantity();
		result.setTotalItemAmount(totalItemAmount);
		return result;
	}
	public static OrderRewardMessage mapToOrderReward(OrderMessage original) {
		OrderRewardMessage result = new OrderRewardMessage();

		result.setItemName(original.getItemName());
		result.setOrderDateTime(original.getOrderDateTime());
		result.setOrderLocation(original.getOrderLocation());
		result.setOrderNumber(original.getOrderNumber());
		result.setPrice(original.getPrice());
		result.setQuantity(original.getQuantity());

		return result;
	}
	public static Predicate<String, OrderMessage> isLargeQuantity(){
		return (key,value)->value.getQuantity()>200;
	}

	public static Predicate<? super String, ? super OrderPatternMessage> isPlastic() {
				return (key, value) -> StringUtils.startsWithIgnoreCase(value.getItemName(), "Plastic");

	}

	public static Predicate<? super String, ? super OrderMessage> isCheap() {
		// TODO Auto-generated method stub
		return (key,value)->value.getPrice()<100;
	}

	public static KeyValueMapper generateStorageKey() {
		// TODO Auto-generated method stub
	 
	return (key, value) -> Base64.getEncoder().encodeToString(((OrderMessage) value).getOrderNumber().getBytes());
				
	}
	public static KeyValueMapper<String, OrderMessage, KeyValue<String, OrderRewardMessage>> mapToOrderRewardChangeKey() {
		return (key, value) -> KeyValue.pair(value.getOrderLocation(), mapToOrderReward(value));
	}
}
