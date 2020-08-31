package com.kafka.broker.serde;


import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomJsonSerializer<T> implements Serializer<T> {
	
	private ObjectMapper objectmapper = new ObjectMapper();
	//ObjectMapper converts java object to JSON
	@Override
	public byte[] serialize(String topic, T data) {
		try {
			return objectmapper.writeValueAsBytes(data);
		} catch (JsonProcessingException e) {

			throw new SerializationException(e);
		}
	}

}
