package org.gridgain.demo.compute;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiTuple;
import org.gridgain.demo.HoldingsUpdater;
import org.gridgain.demo.IgniteClientHelper;

public class ComputeApplication implements AutoCloseable, Runnable {
	// Select time to run the application
	private static int EXEC_TIME_MINS = 30;
	
	private IgniteClientHelper ich;

	private Object[] accounts;

	public static void main(String args[]) throws Exception {
		System.out.println("Application execution time: " + EXEC_TIME_MINS + " minutes");

		ComputeApplication streamingApplication = new ComputeApplication();

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

	public ComputeApplication() throws Exception {
		ich = new IgniteClientHelper(false);
		new HoldingsUpdater(ich);
		
		accounts = ich.getAccountCache().query(new ScanQuery<>(null)).getAll().toArray();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 5, 30, SECONDS);
	}

	@Override
	public void run() {
		for (Object account : accounts) {
			try {
				String id = (String) ((IgniteBiTuple) account).get1();
				new ComputePortfolio(ich, id);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void close() throws Exception {
		ich.close();
	}

}
