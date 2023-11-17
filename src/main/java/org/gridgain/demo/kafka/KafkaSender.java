package org.gridgain.demo.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.gridgain.demo.kafka.model.Account;
import org.gridgain.demo.kafka.model.Holding;
import org.gridgain.demo.kafka.model.Product;
import org.gridgain.demo.kafka.model.ProductPrice;
import org.gridgain.demo.kafka.model.Trade;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class KafkaSender implements AutoCloseable {
	public static final String ACCOUNT_TOPIC = "Account";
	public static final String PRODUCT_TOPIC = "Product";
	public static final String PRODUCT_PRICE_TOPIC = "ProductPrice";
	public static final String TRADE_TOPIC = "Trade";
	public static final String HOLDING_TOPIC = "Holding";
	public static final String BOOTSTRAP_URL = "localhost:9092";
	public static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
	private KafkaProducer<String, Account> accountProducer;
	private KafkaProducer<String, Product> productProducer;
	private KafkaProducer<String, ProductPrice> productPriceProducer;
	private KafkaProducer<String, Trade> tradeProducer;
	private KafkaProducer<String, Holding> holdingProducer;

	public KafkaSender() {
		// create Producer properties
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_URL);
		props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put("value.converter.schemas.enable", "true");
				
		accountProducer = new KafkaProducer<>(props);
		productProducer = new KafkaProducer<>(props);
		productPriceProducer = new KafkaProducer<>(props);
		tradeProducer = new KafkaProducer<>(props);
		holdingProducer = new KafkaProducer<>(props);
	}
	public void send(Account account) {
		ProducerRecord<String, Account> record = new ProducerRecord<>(ACCOUNT_TOPIC, account.getId(), account);
		accountProducer.send(record);
	}
	
	public void send(Product product) {
		ProducerRecord<String, Product> record = new ProducerRecord<>(PRODUCT_TOPIC, product.getSymbol(), product);
		productProducer.send(record);
	}

	public void send(ProductPrice productPrice) {
		System.out.println("send - " + productPrice.toString());
		try {
            ProducerRecord<String, ProductPrice> record = new ProducerRecord<>(PRODUCT_PRICE_TOPIC, productPrice.getId(), productPrice);
            productPriceProducer.send(record);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void send(Trade trade) {
		System.out.println(trade.toString());
		ProducerRecord<String, Trade> record = new ProducerRecord<>(TRADE_TOPIC, trade.getId(), trade);
		tradeProducer.send(record);
	}
	public void send(Holding holdings) {
		ProducerRecord<String, Holding> record = new ProducerRecord<>(HOLDING_TOPIC, holdings.getId(), holdings);
		holdingProducer.send(record);
	}

	@Override
	public void close() throws Exception {
		productPriceProducer.flush();
		tradeProducer.flush();
		productPriceProducer.close();
		tradeProducer.close();
	}

}