package org.gridgain.demo.kafka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.gridgain.demo.MarketTicker;
import org.gridgain.demo.data.DayData;
import org.gridgain.demo.data.StockTicker;
import org.gridgain.demo.kafka.model.Account;
import org.gridgain.demo.kafka.model.Holding;
import org.gridgain.demo.kafka.model.Product;
import org.gridgain.demo.kafka.model.ProductPrice;
import org.gridgain.demo.kafka.model.Trade;
import org.gridgain.demo.model.TradeType;

import com.github.javafaker.Faker;

public class KafkaCsvStockTicker implements MarketTicker {

	public static final String START_DATE = "09/23/2013";
	public static final String[] STOCKS = { "AAPL", "AMD", "AMZN", "CSCO", "META", "MSFT", "NFLX", "QCOM", "TSLA", "IBM" };
	public static final String[] STOCK_NAMES = { "Apple", "AMD", "Amazon", "Cisco", "Meta", "Microsoft", "Netflix",
			"Qualcom", "Tesla", "IBM" };

	public static final boolean DEBUG = false;
	public static final int TICKS_PER_DAY = 1000;
	public static final int NUM_ACCOUNTS = 25;
	private static final long START_HOLDING = 10000;

	private final Map<String, StockTicker> tickers = new HashMap<>();
	private final List<Account> accounts = new ArrayList<>();
	private final List<Holding> holdings = new ArrayList<>();
	private final List<Product> products = new ArrayList<>();
	private final Random random = new Random();

	private ScheduledFuture<?> scheduledFuture;
	private int second = 0;
	private KafkaSender kafkaSender;

	public static void main(String args[]) throws IOException, ParseException {
		KafkaCsvStockTicker ticker = new KafkaCsvStockTicker();

		ticker.start();
	}
	
	public KafkaCsvStockTicker() throws IOException, ParseException {
		kafkaSender = new KafkaSender();

		for (int i = 0; i < STOCKS.length; i++) {
			String stock = STOCKS[i];
			InputStream inputStream = KafkaCsvStockTicker.class.getClassLoader()
					.getResourceAsStream("data/" + stock + ".csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(reader);
			List<CSVRecord> records = parser.getRecords();

			DayData dayData = null;
			Map<String, DayData> data = new HashMap<>();

			for (CSVRecord csvRecord : records) {
				dayData = DayData.get(csvRecord);
				data.put(dayData.getDate(), dayData);
			}

			tickers.put(stock, new StockTicker(stock, data, START_DATE));
			Product product = new Product(stock, STOCK_NAMES[i]);
			products.add(product);
			kafkaSender.send(product);
		}

		System.out.println("Generating Accounts...");
		Faker faker = new Faker();
		for (int i = 0; i < NUM_ACCOUNTS; i++) {
			Account account = new Account(UUID.randomUUID().toString(), faker.name().fullName());
			accounts.add(account);
			kafkaSender.send(account);

			for (int j = 0; j < STOCKS.length - 1; j++) {
				String pId = tickers.get(STOCKS[j]).getSymbol();
				Holding h = new Holding(UUID.randomUUID().toString(), account.getId(),
						System.currentTimeMillis(), START_HOLDING, pId);
				kafkaSender.send(h);
			}
		}
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public List<Holding> getHolding() {
		return holdings;
	}

	public void start() {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 5000, 5000,
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		scheduledFuture.cancel(true);
	}

	@Override
	public void run() {

		second++;
		if (second == TICKS_PER_DAY) {
			for (Entry<String, StockTicker> entry : tickers.entrySet()) {
				try {
					entry.getValue().nextDay();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				second = 0;
			}
		}

		for (Entry<String, StockTicker> entry : tickers.entrySet()) {
			StockTicker stockTicker = entry.getValue();
			double currentPrice = stockTicker.tick();
			ProductPrice productPrice = new ProductPrice(UUID.randomUUID().toString(),
					System.currentTimeMillis(), stockTicker.getSymbol(), currentPrice);
			kafkaSender.send(productPrice);
		}

		if (random.nextBoolean()) {
			int stockId = random.nextInt(STOCKS.length - 1);
			String stock = STOCKS[stockId];
			StockTicker stockTicker = tickers.get(stock);

			String type = TradeType.BUY.name();
			if (random.nextBoolean()) {
				type = TradeType.SELL.name();
			}

			Trade trade = new Trade(UUID.randomUUID().toString(), accounts.get(random.nextInt(NUM_ACCOUNTS)).getId(),
					stock, random.nextInt(500), stockTicker.getCurrentPrice(), type,
					System.currentTimeMillis());
			kafkaSender.send(trade);
		}
	}
}
