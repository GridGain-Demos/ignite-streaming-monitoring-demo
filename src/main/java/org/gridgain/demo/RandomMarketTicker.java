package org.gridgain.demo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holdings;
import org.gridgain.demo.model.HoldingsKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

import com.github.javafaker.Faker;

public class RandomMarketTicker implements Runnable {
	private Random random;
	private List<Stock> stocks;
	private StreamCallback streamCallback;
	private ScheduledFuture<?> scheduledFuture;

	private static final AtomicLong TRADE_SEQ = new AtomicLong();
	private static final AtomicLong PROD_PRICE_SEQ = new AtomicLong();
	private static final AtomicLong HOLDINGS_SEQ = new AtomicLong();
	public static final int NUM_ACCOUNTS = 25;
	private static final long START_HOLDING = 10000;

	public RandomMarketTicker(StreamCallback streamCallback) {
		this.streamCallback = streamCallback;

		System.out.println("Generating Products...");

		stocks = new ArrayList<Stock>();

		stocks.add(new Stock(1, "Amazon", "AMZ", 50, 250, 100));
		stocks.add(new Stock(2, "Apple", "AAPL", 100, 1000, 100));
		stocks.add(new Stock(3, "Dell", "DELL", 100, 500, 100));
		stocks.add(new Stock(4, "Meta", "META", 85, 400, 100));
		stocks.add(new Stock(5, "Google", "GOOGL", 75, 300, 100));
		stocks.add(new Stock(6, "HP", "HP", 50, 750, 100));
		stocks.add(new Stock(7, "IBM", "IBM", 65, 250, 100));
		stocks.add(new Stock(8, "Intel", "INTC", 150, 550, 100));
		stocks.add(new Stock(9, "Tesla", "TSLA", 110, 600, 100));
		stocks.add(new Stock(10, "Oracle", "ORCL", 10, 100, 100));

		for (Stock stock : stocks) {
			streamCallback.message(stock);
		}

		System.out.println("Generating Accounts...");
		Faker faker = new Faker();
		for (long i = 0; i < NUM_ACCOUNTS; i++) {
			Account account = new Account(i, faker.name().fullName());
			streamCallback.message(account);

			for (Stock stock : stocks) {
				Holdings h = new Holdings(HOLDINGS_SEQ.incrementAndGet(), i, stock.id,
						new Timestamp(System.currentTimeMillis()), START_HOLDING);
				streamCallback.message(new HoldingsKey(h.getAccountId(), h.getProductId()), h);

			}
		}

		random = new Random();
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
		for (Stock stock : stocks) {
			updatePrice(stock);
			streamCallback.message(stock.toProductPrice());
		}

		Stock stock = stocks.get(random.nextInt(stocks.size()));

		String type = Trade.TRADE_TYPE.BUY.name();
		if (random.nextBoolean()) {
			type = Trade.TRADE_TYPE.SELL.name();
		}

		Trade trade = new Trade(TRADE_SEQ.getAndIncrement(), (long) random.nextInt(NUM_ACCOUNTS), stock.id,
				random.nextInt(500), stock.currentPrice, type, new Timestamp(System.currentTimeMillis()));

		streamCallback.message(trade);
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

		private long id;
		public String name;
		public String symbol;
		public double minPrice;
		public double maxPrice;
		public double currentPrice;

		public Stock(long id, String name, String symbol, double minPrice, double maxPrice, double currentPrice) {
			this.id = id;
			this.name = name;
			this.symbol = symbol;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
			this.currentPrice = currentPrice;
		}

		public Product toProduct() {
			return new Product(id, symbol, name);
		}

		public ProductPrice toProductPrice() {
			return new ProductPrice(PROD_PRICE_SEQ.incrementAndGet(), new Timestamp(System.currentTimeMillis()), id,
					currentPrice);
		}
	}

}