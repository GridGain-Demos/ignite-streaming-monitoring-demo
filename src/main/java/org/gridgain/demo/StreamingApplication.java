package org.gridgain.demo;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.gridgain.demo.compute.ComputePortfolio;

public class StreamingApplication implements AutoCloseable, Runnable {
	private static int EXEC_TIME_MINS = 15;
	private RandomMarketTicker ticker;
	private IgniteClientHelper ich;

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

		StreamCallback streamCallback = new StreamCallback(ich);

		ticker = new RandomMarketTicker(streamCallback);
		ticker.start();

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 5, 30, SECONDS);
	}

	@Override
	public void run() {
		for (long i = 0; i < RandomMarketTicker.NUM_ACCOUNTS; i++) {
			try {
				new ComputePortfolio(ich, i);
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
