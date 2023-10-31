package org.gridgain.demo.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import org.gridgain.demo.StreamCallback;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;
import org.gridgain.demo.model.TradeType;

import com.github.javafaker.Faker;

public class CsvStockTicker implements MarketTicker {

	public static final String START_DATE = "09/23/2013";
	public static final String[] STOCKS = { "AAPL", "AMD", "AMZN", "CSCO", "META", "MSFT", "NFLX", "QCOM", "TSLA" };
	public static final String[] STOCK_NAMES = { "Apple", "AMD", "Amazon", "Cisco", "Meta", "Microsoft", "Netflix",
			"Qualcom", "Tesla" };

	public static final int TICKS_PER_DAY = 1000;
	public static final int NUM_ACCOUNTS = 25;
	private static final long START_HOLDING = 10000;

	private final Map<String, StockTicker> tickers = new HashMap<>();
	private final List<Account> accounts = new ArrayList<>();
	private final StreamCallback streamCallback;
	private final Random random = new Random();

	private ScheduledFuture<?> scheduledFuture;
	private int second = 0;

	public CsvStockTicker(StreamCallback streamCallback) throws IOException, ParseException {

		this.streamCallback = streamCallback;

		for (int i = 0; i < STOCKS.length; i++) {
			String stock = STOCKS[i];
			BufferedReader reader = new BufferedReader(new FileReader("data/" + stock + ".csv"));
			CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(reader);
			List<CSVRecord> records = parser.getRecords();

			DayData dayData = null;
			Map<String, DayData> data = new HashMap<>();

			for (CSVRecord csvRecord : records) {
				dayData = DayData.get(csvRecord);
				data.put(dayData.getDate(), dayData);
			}

			tickers.put(stock, new StockTicker(stock, data, START_DATE));
			
			streamCallback.message(new Product(stock, STOCK_NAMES[i]));
		}

		System.out.println("Generating Accounts...");
		Faker faker = new Faker();
		for (long i = 0; i < NUM_ACCOUNTS; i++) {
			Account account = new Account(UUID.randomUUID().toString(), faker.name().fullName());
			accounts.add(account);
			streamCallback.message(account);

			for (int j = 0; j < STOCKS.length; j++) {
				String symbol = tickers.get(STOCKS[j]).getSymbol();
				Holding h = new Holding(UUID.randomUUID().toString(), account.getId(), symbol,
						System.currentTimeMillis(), START_HOLDING);
				streamCallback.message(new HoldingKey(h.getAccountId(), h.getSymbol()), h);

			}
		}

	}

	public void start() {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 500, 100,
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
			streamCallback.message(productPrice);
		}

		if (random.nextBoolean()) {
			int stockId = random.nextInt(STOCKS.length);
			String stock = STOCKS[stockId];
			StockTicker stockTicker = tickers.get(stock);

			String type = TradeType.BUY.name();
			if (random.nextBoolean()) {
				type = TradeType.SELL.name();
			}

			Trade trade = new Trade(UUID.randomUUID().toString(), accounts.get(random.nextInt(NUM_ACCOUNTS)).getId(), stockTicker.getSymbol(),
					random.nextInt(500), stockTicker.getCurrentPrice(), type,
					System.currentTimeMillis());

			streamCallback.message(trade);
		}
	}
}
