package com.kafka.broker.stream.inventory;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.kafka.broker.message.InventoryMessage;

//@Configuration
public class InventoryoneStream {
	
	@Bean
	public KStream<String, InventoryMessage>  kstreamInventory(StreamsBuilder builder){
		
		var stringSerde = Serdes.String();
		var inventorySerde = new JsonSerde<>(InventoryMessage.class);
		var longserde = Serdes.Long();
		var inventoryStream = builder.stream("t.commodity.inventory", Consumed.with(stringSerde, inventorySerde));
		//we need to map values to get quantity then group by key
		inventoryStream.mapValues((k,v)->v.getQuantity()).groupByKey()
		.aggregate(()->0l, (aggKey, newValue, aggValue) -> aggValue + newValue, 
				Materialized.with(stringSerde, longserde)).toStream()
		.to("t.commodity.inventory-total-one", Produced.with(stringSerde, longserde));
		
		
		return inventoryStream;

	}
	

}
