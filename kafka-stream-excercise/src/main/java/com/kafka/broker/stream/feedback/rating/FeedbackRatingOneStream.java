package com.kafka.broker.stream.feedback.rating;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.FeedbackMessage;
import com.kafka.broker.message.FeedbackRatingOneMessage;

//@Configuration
public class FeedbackRatingOneStream {
	
	@Bean
	public KStream<String, FeedbackMessage> ksfeedBackStream(StreamsBuilder builder){
		var stringserde = Serdes.String();
		var feedbackSerde = new JsonSerde<>(FeedbackMessage.class);
		
		var feedbackRatingOneSerde = new JsonSerde<>(FeedbackRatingOneMessage.class);

		var feedbackstoreserde = new JsonSerde<>(FeedbackRatingOneStoreValue.class);
		
		var feedbackratingstream = builder.stream("t.commodity.feedback", Consumed.with(stringserde, feedbackSerde));
		var storename = "feedbackratingstore";
		var storesupplier = Stores.inMemoryKeyValueStore(storename);
		var storebuilder = Stores.keyValueStoreBuilder(storesupplier, stringserde, feedbackstoreserde);
		builder.addStateStore(storebuilder);
		feedbackratingstream.transformValues(()->new FeedbackRatingOneTransformer(storename),
				storename).to("t.commodity.feedback.rating-one", Produced.with(stringserde, feedbackRatingOneSerde));
	
		return feedbackratingstream;
	}
}
