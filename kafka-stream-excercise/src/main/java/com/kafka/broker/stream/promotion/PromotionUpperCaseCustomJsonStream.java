package com.kafka.broker.stream.promotion;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.PromotionMessage;
import com.kafka.broker.serde.PromotionSerde;


//@Configuration
public class PromotionUpperCaseCustomJsonStream {

	private static final Logger LOG = LoggerFactory.getLogger(PromotionUpperCaseCustomJsonStream.class);

	@Bean
	public KStream<String, PromotionMessage> build(StreamsBuilder builder) {

		Serde<String> stringSerde = Serdes.String();

		PromotionSerde promotionserde = new PromotionSerde();

		KStream<String, PromotionMessage> source = builder.stream("t.commodity.promotion",
				Consumed.with(stringSerde, promotionserde));

		KStream<String, PromotionMessage> uppercase = source.mapValues(this::uppercasePromotionCode);
		
		uppercase.to("t.commodity.promotion-uppercase", Produced.with(stringSerde, promotionserde));

		source.print(Printed.<String, PromotionMessage>toSysOut().withLabel("CUSTOM JSON Serde Original Stream"));
		uppercase.print(Printed.<String, PromotionMessage>toSysOut().withLabel("CUSTOM JSON Serde Uppercase Stream"));

		return source;
	}

	private PromotionMessage uppercasePromotionCode(PromotionMessage message) {
		return new PromotionMessage(message.getPromotionCode().toUpperCase());
	}
	
	
	//[JSON Uppercase Stream]: null, {"promotionCode":"SOM851"}
	//[JSON Original Stream]: null, {"promotionCode":"Som851"}
}
