package com.kafka.broker.serde;

import com.kafka.broker.message.PromotionMessage;

public class PromotionSerde extends CustomJsonSerde<PromotionMessage> {

	public PromotionSerde(){
		
		super(new CustomJsonSerializer<PromotionMessage>(), 
				new CustomJsonDerializer<PromotionMessage>(PromotionMessage.class));
	}
}
