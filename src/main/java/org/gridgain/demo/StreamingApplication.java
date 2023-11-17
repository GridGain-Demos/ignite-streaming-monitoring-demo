package org.gridgain.demo;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.gridgain.demo.compute.ComputePortfolio;
import org.gridgain.demo.kafka.KafkaCsvStockTicker;
import org.gridgain.demo.model.Account;

public class StreamingApplication implements AutoCloseable, Runnable {
	// Select time to run the application
	private static int EXEC_TIME_MINS = 30;
	
	// Choose the Ticker you would like to use 
	//private static Class<? extends MarketTicker> MARKET_TICKER = RandomMarketTicker.class;
	//private static Class<? extends MarketTicker> MARKET_TICKER = CsvStockTicker.class;
	private static Class<? extends MarketTicker> MARKET_TICKER = KafkaCsvStockTicker.class;
	
	private MarketTicker ticker;
	private IgniteClientHelper ich;
	private IgniteStreamCallback streamCallback;

	public static void main(String args[]) throws Exception {
		System.out.println("Application execution time: " + EXEC_TIME_MINS + " minutes");

		StreamingApplication streamingApplication = new StreamingApplication();

		// Shutting down the application in 'execTime' minutes.
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("The execution time is over. Shutting down the application...");
				try {
					streamingApplication.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}, EXEC_TIME_MINS * 60 * 1000);
	}

	public StreamingApplication() throws Exception {
		ich = new IgniteClientHelper();

		new HoldingsUpdater(ich);
		
		Constructor<? extends MarketTicker> constructor = null;
		try {
			constructor = MARKET_TICKER.getDeclaredConstructor(StreamCallback.class);
		} catch (NoSuchMethodException e) {
		}
		if (constructor != null) {
			streamCallback = new IgniteStreamCallback(ich);
			ticker = constructor.newInstance(streamCallback);
		} else {
			ticker = MARKET_TICKER.newInstance();
		}
		ticker.start();

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 5, 30, SECONDS);
	}

	@Override
	public void run() {
		List<Account> accounts = streamCallback.getAccounts();
		for (int i = 0; i < RandomMarketTicker.NUM_ACCOUNTS; i++) {
			try {
				new ComputePortfolio(ich, accounts.get(i).getId());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void close() throws Exception {
		ticker.stop();
		ich.close();
	}

}
