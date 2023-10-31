package org.gridgain.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;
import org.gridgain.demo.model.TradeType;

import com.github.javafaker.Faker;

public class RandomMarketTicker implements MarketTicker {
	private Random random;
	private List<Stock> stocks;
	private List<Account> accounts;
	private StreamCallback streamCallback;
	private ScheduledFuture<?> scheduledFuture;

	public static final int NUM_ACCOUNTS = 25;
	private static final long START_HOLDING = 10000;

	public RandomMarketTicker(StreamCallback streamCallback) {
		this.streamCallback = streamCallback;

		System.out.println("Generating Products...");

		stocks = new ArrayList<Stock>();

		stocks.add(new Stock(UUID.randomUUID().toString(), "Amazon", "AMZ", 50, 250, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Apple", "AAPL", 100, 1000, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Dell", "DELL", 100, 500, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Meta", "META", 85, 400, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Google", "GOOGL", 75, 300, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "HP", "HP", 50, 750, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "IBM", "IBM", 65, 250, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Intel", "INTC", 150, 550, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Tesla", "TSLA", 110, 600, 100));
		stocks.add(new Stock(UUID.randomUUID().toString(), "Oracle", "ORCL", 10, 100, 100));

		for (Stock stock : stocks) {
			streamCallback.message(stock.toProduct());
		}

		System.out.println("Generating Accounts...");
		Faker faker = new Faker();
		accounts = new ArrayList<>();
		for (long i = 0; i < NUM_ACCOUNTS; i++) {
			Account account = new Account(UUID.randomUUID().toString(), faker.name().fullName());
			accounts.add(account);
			streamCallback.message(account);

			for (Stock stock : stocks) {
				Holding h = new Holding(UUID.randomUUID().toString(), account.getId(), stock.id,
						System.currentTimeMillis(), START_HOLDING);
				streamCallback.message(new HoldingKey(h.getAccountId(), h.getSymbol()), h);

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

		String type = TradeType.BUY.name();
		if (random.nextBoolean()) {
			type = TradeType.SELL.name();
		}

		Trade trade = new Trade(UUID.randomUUID().toString(), accounts.get(random.nextInt(NUM_ACCOUNTS)).getId(),
				stock.getId(), random.nextInt(500), stock.getCurrentPrice(), type,
				System.currentTimeMillis());

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
		double changeAmount = stock.getCurrentPrice() * changePercent / 100;
		double newPrice = stock.getCurrentPrice() + changeAmount;

		// Add a ceiling and floor.
		if (newPrice < stock.minPrice) {
			newPrice += Math.abs(changeAmount) * 2;
		} else if (newPrice > stock.maxPrice) {
			newPrice -= Math.abs(changeAmount) * 2;
		}

		stock.setCurrentPrice(newPrice);
	}

	public static class Stock {

		private final String id;
		private final String name;
		private final String symbol;
		private final double minPrice;
		private final double maxPrice;
		private double currentPrice;

		public Stock(String id, String name, String symbol, double minPrice, double maxPrice, double currentPrice) {
			this.id = id;
			this.name = name;
			this.symbol = symbol;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
			this.setCurrentPrice(currentPrice);
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getSymbol() {
			return symbol;
		}

		public double getMinPrice() {
			return minPrice;
		}

		public double getMaxPrice() {
			return maxPrice;
		}

		public double getCurrentPrice() {
			return currentPrice;
		}

		public void setCurrentPrice(double currentPrice) {
			this.currentPrice = currentPrice;
		}

		public Product toProduct() {
			return new Product(symbol, name);
		}

		public ProductPrice toProductPrice() {
			return new ProductPrice(UUID.randomUUID().toString(), System.currentTimeMillis(), id,
					getCurrentPrice());
		}

	}

}