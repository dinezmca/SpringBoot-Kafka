package com.kafka.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import com.kafka.broker.message.OnlineOrderMessage;

public class OnlineOrderTimestampExtractor implements TimestampExtractor {

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		
		var onlineOrdermessage = (OnlineOrderMessage)record.value();
		
		return onlineOrdermessage !=null?
				
				LocalDateTimeUtil.toEpochTimestamp(onlineOrdermessage.getOrderDateTime())
				:record.timestamp();
	}

}