package com.kafka.broker.stream.commodity;

import static com.kafka.util.CommodityStreamUtil.generateStorageKey;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.OrderMessage;
import com.kafka.broker.message.OrderPatternMessage;
import com.kafka.broker.message.OrderRewardMessage;
import com.kafka.util.CommodityStreamUtil;


//@Configuration
public class CommodityTwoStream {

	@Bean
	public KStream<String, OrderMessage> kstreamCommodityTrading(StreamsBuilder builder) {
		Serde<String> stringSerde = Serdes.String();
		JsonSerde<OrderMessage> orderSerde = new JsonSerde<OrderMessage>(OrderMessage.class);
		JsonSerde orderPatternSerde = new JsonSerde<>(OrderPatternMessage.class);
		JsonSerde orderRewardSerde = new JsonSerde<>(OrderRewardMessage.class);

		KStream<String, OrderMessage> maskedOrderStream = builder
				.stream("t.commodity.order", Consumed.with(stringSerde, orderSerde))
				.mapValues(CommodityStreamUtil::maskCreditCard);
		// 1st sink stream to pattern
		// summarize order item (total = price * quantity)
		KStream<String, OrderPatternMessage> patternStream[] = maskedOrderStream
				.mapValues(CommodityStreamUtil::mapToOrderPattern)
				.branch(CommodityStreamUtil.isPlastic(), (key, value) -> true);

		int plasticIndex = 0;
		int notPlasticIndex = 1;

		patternStream[plasticIndex].to("t.commodity.pattern-two.plastic",
				Produced.with(stringSerde, orderPatternSerde));
		patternStream[notPlasticIndex].to("t.commodity.pattern-two.notplastic",
				Produced.with(stringSerde, orderPatternSerde));

		// 2nd sink stream to reward
		// filter only "large" quantity
		KStream<String, OrderRewardMessage> rewardStream = maskedOrderStream
				.filter(CommodityStreamUtil.isLargeQuantity()).filterNot(CommodityStreamUtil.isCheap())
				.mapValues(CommodityStreamUtil::mapToOrderReward);

		rewardStream.to("t.commodity.reward-two", Produced.with(stringSerde, orderRewardSerde));

		// 3rd sink stream to storage
		// no transformation
		KStream<String, OrderMessage> storageStream = maskedOrderStream
				.selectKey(generateStorageKey());
		storageStream.to("t.commodity.storage-two", Produced.with(stringSerde, orderSerde));

		return maskedOrderStream;
	}

}
