package com.kafka.broker.stream.orderpayment;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.OnlineOrderMessage;
import com.kafka.broker.message.OnlineOrderPaymentMessage;
import com.kafka.broker.message.OnlinePaymentMessage;
import com.kafka.util.OnlineOrderTimestampExtractor;
import com.kafka.util.OnlinePaymentTimestampExtractor;

@Configuration
public class OrderPaymentoneStream {
	
	@Bean
	public KStream<String, OnlineOrderMessage> ksorderPayment(StreamsBuilder builder ){
	
		var stringserde = Serdes.String();
		var orderserde = new JsonSerde<>(OnlineOrderMessage.class);
		var paymentserde = new JsonSerde<>(OnlinePaymentMessage.class);
		var orderpaymemtSerde = new JsonSerde<>(OnlineOrderPaymentMessage.class);
		
		var orderStream = builder.stream("t.commodity.online-order", Consumed.with(stringserde, orderserde, new OnlineOrderTimestampExtractor(), null));
		var paymentStream = builder.stream("t.commodity.online-payment", Consumed.with(stringserde, paymentserde, new OnlinePaymentTimestampExtractor(), null));
		
		orderStream
		.join(paymentStream, this::joinOrderPayment, JoinWindows.of(Duration.ofHours(1)),
				StreamJoined.with(stringserde, orderserde, paymentserde))
		.to("t.commodity.join-order-payment-one", Produced.with(stringserde, orderpaymemtSerde));

		return orderStream;
	}

	private OnlineOrderPaymentMessage joinOrderPayment(OnlineOrderMessage order, OnlinePaymentMessage payment) {
		var result = new OnlineOrderPaymentMessage();
		result.setOnlineOrderNumber(order.getOnlineOrderNumber());
		result.setOrderDateTime(order.getOrderDateTime());
		result.setTotalAmount(order.getTotalAmount());
		result.setUsername(order.getUsername());

		result.setPaymentDateTime(payment.getPaymentDateTime());
		result.setPaymentMethod(payment.getPaymentMethod());
		result.setPaymentNumber(payment.getPaymentNumber());

		return result;

	}
}
