package com.kafka.broker.serde;

import java.io.IOException;
import java.util.Objects;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomJsonDerializer<T> implements Deserializer<T> {

	private ObjectMapper mapper = new ObjectMapper();

	private final Class<T> deserializedClass;

	public CustomJsonDerializer(Class<T> deserializedClass) {
		Objects.requireNonNull(deserializedClass, "deserializedClass must not be null");
		this.deserializedClass = deserializedClass;
	}

	@Override
	public T deserialize(String topic, byte[] data) {
		try {
			return mapper.readValue(data, deserializedClass);
		} catch (IOException e) {
			throw new SerializationException();
		}
	}
}
