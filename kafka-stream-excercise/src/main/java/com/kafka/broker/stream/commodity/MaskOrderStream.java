package com.kafka.broker.stream.commodity;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.OrderMessage;
import com.kafka.util.CommodityStreamUtil;

//@Configuration
public class MaskOrderStream {
	@Bean
	public KStream<String, OrderMessage> kstreamCommodityTrading(StreamsBuilder builder) {

		JsonSerde<OrderMessage> jsonserde = new JsonSerde<>(OrderMessage.class);
		KStream<String, OrderMessage> maskedstream = builder
				.stream("t.commodity.order", Consumed.with(Serdes.String(), jsonserde))
				.mapValues(CommodityStreamUtil::maskCreditCard);
		maskedstream.to("t.commodity.order-masked", Produced.with(Serdes.String(), jsonserde));
		maskedstream.print(Printed.<String, OrderMessage>toSysOut().withLabel("Masked order stream"));
		return maskedstream;
	}

}
