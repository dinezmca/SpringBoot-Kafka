package com.kafka.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import com.kafka.broker.message.OnlinePaymentMessage;


public class OnlinePaymentTimestampExtractor implements TimestampExtractor {

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		
		var onlinePaymentmessage = (OnlinePaymentMessage)record.value();
		
		return onlinePaymentmessage !=null?
				
				LocalDateTimeUtil.toEpochTimestamp(onlinePaymentmessage.getPaymentDateTime())
				:record.timestamp();
	}

}
