package com.kafka.broker.stream.commodity;

import static com.kafka.util.CommodityStreamUtil.generateStorageKey;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.OrderMessage;
import com.kafka.broker.message.OrderPatternMessage;
import com.kafka.broker.message.OrderRewardMessage;
import com.kafka.util.CommodityStreamUtil;

import static com.kafka.util.CommodityStreamUtil.isPlastic;


//@Configuration
public class CommodityFiveStream {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommodityFiveStream.class);


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
				final Produced branchProducer = Produced.with(stringSerde, orderPatternSerde);
				
				new KafkaStreamBrancher<String, OrderPatternMessage>()
						.branch(isPlastic(), kstream -> kstream.to("t.commodity.pattern-three.plastic", branchProducer))
						.defaultBranch(kstream -> kstream.to("t.commodity.pattern-three.notplastic", branchProducer))
						.onTopOf(maskedOrderStream.mapValues(CommodityStreamUtil::mapToOrderPattern));

		// 2nd sink stream to reward
		// filter only "large" quantity
		KStream<String, OrderRewardMessage> rewardStream = maskedOrderStream
				.filter(CommodityStreamUtil.isLargeQuantity())
				.filterNot(CommodityStreamUtil.isCheap())
				.map(CommodityStreamUtil.mapToOrderRewardChangeKey());

		rewardStream.to("t.commodity.reward-three", Produced.with(stringSerde, orderRewardSerde));

		// 3rd sink stream to storage
		// no transformation
		KStream<String, OrderMessage> storageStream = maskedOrderStream
				.selectKey(generateStorageKey());
		storageStream.to("t.commodity.storage-three", Produced.with(stringSerde, orderSerde));
		
		//4th stream for fraud detection
		maskedOrderStream.filter((key,value)->value.getOrderLocation().startsWith("C"))
		.foreach((key,value)->this.reportFraud(value));

		return maskedOrderStream;
	}
	
public void reportFraud(OrderMessage order) {
	LOG.info("Reporting fraud {}", order);
}

}
