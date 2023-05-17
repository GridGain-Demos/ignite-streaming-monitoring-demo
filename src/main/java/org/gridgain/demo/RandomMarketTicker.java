package org.gridgain.demo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

public class RandomMarketTicker implements Runnable, MarketTicker {
	private Random random;
	private List<Stock> stocks;
	private Gson gson;
	private StreamCallback streamCallback;
	private PubNub pubNub;
	private ScheduledFuture<?> scheduledFuture;

	public RandomMarketTicker(StreamCallback streamCallback) {
		this.streamCallback = streamCallback;

		stocks = new ArrayList<Stock>();

		stocks.add(new Stock("Amazon", 50, 250, 100));
		stocks.add(new Stock("Apple", 100, 1000, 100));
		stocks.add(new Stock("Dell", 100, 500, 100));
		stocks.add(new Stock("Facebook", 85, 400, 100));
		stocks.add(new Stock("Google", 75, 300, 100));
		stocks.add(new Stock("HP", 50, 750, 100));
		stocks.add(new Stock("IBM", 65, 250, 100));
		stocks.add(new Stock("Intel", 150, 550, 100));
		stocks.add(new Stock("Tesla", 110, 600, 100));
		stocks.add(new Stock("Yahoo", 10, 100, 100));

		random = new Random();
		gson = new Gson();
		pubNub = new PubNub(new PNConfiguration());
	}

	public void start() {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 1000, 200,
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		scheduledFuture.cancel(true);
	}

	@Override
	public void run() {
		Stock stock = stocks.get(random.nextInt(stocks.size()));
		updatePrice(stock);
		String type = Trade.TRADE_TYPE.BUY.name();
		if (random.nextBoolean()) {
			type = Trade.TRADE_TYPE.SELL.name();
		}
		Trade trade = new Trade(stock.name, random.nextInt(500), stock.currentPrice, type,
				new Timestamp(System.currentTimeMillis()));
		
		JsonElement json = gson.toJsonTree(trade);

		PNMessageResult result = PNMessageResult.builder().message(json).build();
		streamCallback.message(pubNub, result);
	}

	private void updatePrice(Stock stock) {
		// Instead of a fixed volatility, pick a random volatility
		// each time, between 2 and 10.
		double volatility = random.nextDouble() * 10 + 2;

		double rnd = random.nextDouble();

		double changePercent = 2 * volatility * rnd;

		if (changePercent > volatility) {
			changePercent -= (2 * volatility);
		}
		double changeAmount = stock.currentPrice * changePercent / 100;
		double newPrice = stock.currentPrice + changeAmount;

		// Add a ceiling and floor.
		if (newPrice < stock.minPrice) {
			newPrice += Math.abs(changeAmount) * 2;
		} else if (newPrice > stock.maxPrice) {
			newPrice -= Math.abs(changeAmount) * 2;
		}

		stock.currentPrice = newPrice;
	}

	public static class Stock {

		public String name;
		public double minPrice;
		public double maxPrice;
		public double currentPrice;

		public Stock(String name, double minPrice, double maxPrice, double currentPrice) {
			this.name = name;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
			this.currentPrice = currentPrice;
		}
	}

}