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
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.FeedbackMessage;

//@Configuration
public class FeedbackThreeStream {
	
	public static final Set<String> GOOD_WORDS = Set.of("happy", "good", "helpful");
	private static final Set<String> BAD_WORDS = Set.of("angry", "sad", "bad");


	@Bean
	public KStream<String, FeedbackMessage> kstreamFeedback(StreamsBuilder builder) {
		
		var stringSerde = Serdes.String();
		var jsonSerde = new JsonSerde<>(FeedbackMessage.class);
		
		var sourceStream = builder.stream("t.commodity.feedback", Consumed.with(stringSerde, jsonSerde));
		
		var feedbackStreams = sourceStream.flatMap(spliteWord()).branch(goodWord(), badWord());
		
		feedbackStreams[0].to("t.commodity.feedback-three-good");
		feedbackStreams[1].to("t.commodity.feedback-three-bad");

		return sourceStream;
	}

	private Predicate<String, String> goodWord() {
		return (k,v)->GOOD_WORDS.contains(v);
	}
	
	private Predicate<String, String> badWord() {
		return (k,v)->BAD_WORDS.contains(v);
	}

	private KeyValueMapper<String, FeedbackMessage, Iterable<KeyValue<String, String>>> spliteWord() {
		// TODO Auto-generated method stub
		return (k,v)->Arrays.asList(v.getFeedback().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+"))
				.stream()
				.distinct()
				.map(word->KeyValue.pair(v.getLocation(), word)).collect(toList());
		

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
