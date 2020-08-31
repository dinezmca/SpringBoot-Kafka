package com.kafka.broker.stream.promotion;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.stream.message.PromotionMessages;

//@Configuration
public class PromotionUppercaseJsonStream {
	
	private static final Logger LOG = LoggerFactory.getLogger(PromotionUppercaseJsonStream.class);

	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Bean
	public KStream<String, String> build(StreamsBuilder builder){
	
		
		KStream<String, String> sourceStream = builder.stream("t.commodity.promotion",
				Consumed.with(Serdes.String(), Serdes.String()));
		KStream<String, String> uppercasestream = sourceStream.mapValues(this::getUpperCase);
		
		uppercasestream.to("t.commodity.promotion-uppercase");

		// useful for debugging, but better not use it on production
		uppercasestream.print(Printed.<String, String>toSysOut().withLabel("JSON Uppercase Stream"));
		sourceStream.print(Printed.<String, String>toSysOut().withLabel("JSON Original Stream"));

		return sourceStream;
	} 
	
	public String getUpperCase(String message) {
		
		LOG.info(" process message {}", message);
		
		try {
			PromotionMessages original = mapper.readValue(message, PromotionMessages.class);

			PromotionMessages converted = new PromotionMessages(original.getPromotionCode().toUpperCase());
			return mapper.writeValueAsString(converted);
		} catch (JsonProcessingException e) {
			LOG.warn("Can't process message {}", message);
		}

		return StringUtils.EMPTY;
	}

}
