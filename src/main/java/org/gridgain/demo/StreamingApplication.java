package org.gridgain.demo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheMode.REPLICATED;
import static org.apache.ignite.cluster.ClusterState.ACTIVE;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.gridgain.demo.compute.ComputePortfolio;
import org.gridgain.demo.data.CsvStockTicker;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public class StreamingApplication implements AutoCloseable, Runnable {
	// Select time to run the application
	private static int EXEC_TIME_MINS = 30;

	// Choose the Ticker you would like to use
	// private static Class<? extends MarketTicker> MARKET_TICKER =
	// RandomMarketTicker.class;
	private static Class<? extends MarketTicker> MARKET_TICKER = CsvStockTicker.class;
	// private static Class<? extends MarketTicker> MARKET_TICKER =
	// KafkaCsvStockTicker.class;

	private MarketTicker ticker;
	private IgniteStreamCallback streamCallback;
	private DemoConfiguration cfg;

	private final Ignite ignite;
	private final IgniteCache<String, Account> accountCache;
	private final IgniteCache<HoldingKey, Holding> holdingCache;
	private final IgniteCache<String, Product> productCache;
	private final IgniteCache<String, ProductPrice> productPriceCache;
	private final IgniteCache<String, Trade> tradeCache;

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
		this(true);
	}

	public StreamingApplication(boolean destroyCaches) throws Exception {
		cfg = new DemoConfiguration();

		ignite = Ignition.start(cfg);
		ignite.cluster().state(ACTIVE);
		ignite.cluster().tag("Demo Cluster");

		if (destroyCaches) {
			System.out.println("Deleting Caches...");

			ignite.destroyCache(DemoConfiguration.ACCOUNT_CACHE_NAME);
			ignite.destroyCache(DemoConfiguration.HOLDINGS_CACHE_NAME);
			ignite.destroyCache(DemoConfiguration.PRODUCT_CACHE_NAME);
			ignite.destroyCache(DemoConfiguration.PRODUCT_PRICE_CACHE_NAME);
			ignite.destroyCache(DemoConfiguration.TRADE_CACHE_NAME);

			System.out.println("Creating Caches...");
		}
		accountCache = ignite.getOrCreateCache(new AccountCacheConfiguration<String, Account>());
		holdingCache = ignite.getOrCreateCache(new HoldingCacheConfiguration<HoldingKey, Holding>());
		productCache = ignite.getOrCreateCache(new ProductCacheConfiguration<String, Product>());
		productPriceCache = ignite.getOrCreateCache(new ProductPriceCacheConfiguration<String, ProductPrice>());
		tradeCache = ignite.getOrCreateCache(new TradeCacheConfiguration<String, Trade>());

		new HoldingsUpdater(this);

		Constructor<? extends MarketTicker> constructor = null;
		try {
			constructor = MARKET_TICKER.getDeclaredConstructor(StreamCallback.class);
		} catch (NoSuchMethodException e) {
		}
		if (constructor != null) {
			streamCallback = new IgniteStreamCallback(this);
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
				new ComputePortfolio(this, accounts.get(i).getId());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}


	public Ignite getIgnite() {
		return ignite;
	}

	@Override
	public void close() throws Exception {
		ignite.close();
	}

	public IgniteCache<String, Account> getAccountCache() {
		return accountCache;
	}

	public IgniteCache<HoldingKey, Holding> getHoldingCache() {
		return holdingCache;
	}

	public IgniteCache<String, Product> getProductCache() {
		return productCache;
	}

	public IgniteCache<String, ProductPrice> getProductPriceCache() {
		return productPriceCache;
	}

	public IgniteCache<String, Trade> getTradeCache() {
		return tradeCache;
	}

	public static class AccountCacheConfiguration<K, V> extends CacheConfiguration<String, Account> {

		private static final long serialVersionUID = 0L;

		public AccountCacheConfiguration() {
			// Set required cache configuration properties.
			setName(DemoConfiguration.ACCOUNT_CACHE_NAME);
			setIndexedTypes(String.class, Account.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DemoConfiguration.DATA_REGION);
			setSqlSchema(DemoConfiguration.SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class HoldingCacheConfiguration<K, V> extends CacheConfiguration<HoldingKey, Holding> {

		private static final long serialVersionUID = 0L;

		public HoldingCacheConfiguration() {
			// Set required cache configuration properties.
			setName(DemoConfiguration.HOLDINGS_CACHE_NAME);
			setIndexedTypes(HoldingKey.class, Holding.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DemoConfiguration.DATA_REGION);
			setSqlSchema(DemoConfiguration.SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductCacheConfiguration<K, V> extends CacheConfiguration<String, Product> {

		private static final long serialVersionUID = 0L;

		public ProductCacheConfiguration() {
			// Set required cache configuration properties.
			setName(DemoConfiguration.PRODUCT_CACHE_NAME);
			setIndexedTypes(String.class, Product.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DemoConfiguration.DATA_REGION);
			setSqlSchema(DemoConfiguration.SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductPriceCacheConfiguration<K, V> extends CacheConfiguration<String, ProductPrice> {

		private static final long serialVersionUID = 0L;

		public ProductPriceCacheConfiguration() {
			// Set required cache configuration properties.
			setName(DemoConfiguration.PRODUCT_PRICE_CACHE_NAME);
			setIndexedTypes(String.class, ProductPrice.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DemoConfiguration.DATA_REGION);
			setSqlSchema(DemoConfiguration.SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class TradeCacheConfiguration<K, V> extends CacheConfiguration<String, Trade> {

		private static final long serialVersionUID = 0L;

		public TradeCacheConfiguration() {
			// Set required cache configuration properties.
			setName(DemoConfiguration.TRADE_CACHE_NAME);
			setIndexedTypes(String.class, Trade.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DemoConfiguration.DATA_REGION);
			setSqlSchema(DemoConfiguration.SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

}
