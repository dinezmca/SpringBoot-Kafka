package com.kafka.broker.stream.feedback;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Set;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.FeedbackMessage;

//@Configuration
public class FeedbackTwoStream {
	
	public static final Set<String> GOOD_WORDS = Set.of("happy", "good", "helpful");

	@Bean
	public KStream<String, String> kstreamFeedback(StreamsBuilder builder) {
		
		var stringSerde = Serdes.String();
		var jsonSerde = new JsonSerde<>(FeedbackMessage.class);
		
		var feedbackStream = builder.stream("t.commodity.feedback", Consumed.with(stringSerde, jsonSerde))
				.flatMap(mapperGoodWords());
		feedbackStream.to("t.commodity.feedback-two-good");

		return feedbackStream;
	}

	private KeyValueMapper<String, FeedbackMessage, Iterable<KeyValue<String, String>>> mapperGoodWords() {
		return (k,v)->Arrays.asList(v.getFeedback().replaceAll("[^a-zA-Z]", "").toLowerCase().split("\\S+"))
				.stream()
				.distinct()
				.map(word ->
				 KeyValue.pair(v.getLocation(),
						 word))
				.collect(toList());
	}

	/*
	 * private ValueMapper<FeedbackMessage, Iterable<String>> mapperGoodWords() {
	 * return feedbackMessage -> Arrays.asList(feedbackMessage.getFeedback()
	 * .replaceAll("[^a-zA-Z]", "") .toLowerCase() .split("\\S+")) .stream()
	 * .filter(splittedword
	 * ->GOOD_WORDS.contains(splittedword)).distinct().collect(toList()); }
	 */
}
