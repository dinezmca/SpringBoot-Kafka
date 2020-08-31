package com.kafka.broker.stream.feedback.rating;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import com.kafka.broker.message.FeedbackMessage;
import com.kafka.broker.message.FeedbackRatingOneMessage;

public class FeedbackRatingOneTransformer implements ValueTransformer<FeedbackMessage, FeedbackRatingOneMessage>{

	private ProcessorContext context;
	
	private final String stateStoreName;
	
	private KeyValueStore<String, FeedbackRatingOneStoreValue> ratingStateStore;
	
	public FeedbackRatingOneTransformer(String stateStoreName) {
		if (StringUtils.isEmpty(stateStoreName)) {
			throw new IllegalArgumentException("State store name must not empty");
		}

		this.stateStoreName = stateStoreName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		this.ratingStateStore = (KeyValueStore<String, FeedbackRatingOneStoreValue>) 
				this.context.getStateStore(stateStoreName);
	}
	
	


	@Override
	public FeedbackRatingOneMessage transform(FeedbackMessage feedbackMessage) {
		
		var storevalue = java.util.Optional.ofNullable(ratingStateStore.get(stateStoreName))
				.orElse(new FeedbackRatingOneStoreValue());
		
		//update store value
		
		var sumRating = storevalue.getSumRating() + feedbackMessage.getRating();
		storevalue.setSumRating(sumRating);
		
		var countRating = storevalue.getCountRating() + 1;
		storevalue.setCountRating(countRating);
		
		//put new store to state
		ratingStateStore.put(feedbackMessage.getLocation(), storevalue);
		
		//build branch rating
		var branchRating = new FeedbackRatingOneMessage();
		branchRating.setLocation(feedbackMessage.getLocation());
		
		double averageRating = Math.round((double)(sumRating / countRating *10d)/10d);
		
		branchRating.setAverageRating(averageRating);

		return branchRating;
	}

	@Override
	public void close() {
		
	}

}
