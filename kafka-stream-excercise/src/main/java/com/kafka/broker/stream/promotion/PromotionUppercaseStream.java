package com.kafka.broker.stream.promotion;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class PromotionUppercaseStream {
	
	//mapValues - Transform the value of each input record into a new value (with possible new type) of the output record. The provided ValueMapperWithKey is applied to each input record value and computes a new value for it. Thus, an input record <K,V> can be transformed into an output record <K:V'>. This is a stateless record-by-record operation (cf. transformValues(ValueTransformerWithKeySupplier, String...) for stateful value transformation).

	@Bean
	public KStream<String, String> upperCasePromotion(StreamsBuilder builder){
		
		KStream<String, String> sourceStream = builder.stream("t.commodity.promotion",
				Consumed.with(Serdes.String(), Serdes.String()));
		
		KStream<String, String> upperCaseStream = sourceStream.mapValues(s->s.toUpperCase());
		
		upperCaseStream.to("t.commodity.promotion-uppercase");
		
		sourceStream.print(Printed.<String,String>toSysOut().withLabel("ORIGINAL STREAM"));
			
		upperCaseStream.print(Printed.<String,String>toSysOut().withLabel("UPPER CASE STREAM"));
		
		return sourceStream;
		
	}
}
