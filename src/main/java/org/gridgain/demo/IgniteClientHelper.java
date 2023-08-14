package org.gridgain.demo;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheMode.REPLICATED;
import static org.apache.ignite.cluster.ClusterState.ACTIVE;
import static org.apache.ignite.configuration.DeploymentMode.CONTINUOUS;

import java.util.ArrayList;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holdings;
import org.gridgain.demo.model.HoldingsKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public class IgniteClientHelper implements AutoCloseable {

	public static final String DATA_REGION = "MyDataRegion";
	public static final String SQL_SCHEMA = "PUBLIC";

	public static final String ACCOUNT_CACHE_NAME = "account";
	public static final String HOLDINGS_CACHE_NAME = "holdings";
	public static final String PRODUCT_CACHE_NAME = "product";
	public static final String PRODUCT_PRICE_CACHE_NAME = "productPrice";
	public static final String TRADE_CACHE_NAME = "trade";

	public static final String ACCOUNT_SEQUENCE = "AccountSequence";

	private final Ignite ignite;
	private final IgniteCache<Long, Account> accountCache;
	private final IgniteCache<HoldingsKey, Holdings> holdingsCache;
	private final IgniteCache<Long, Product> productCache;
	private final IgniteCache<Long, ProductPrice> productPriceCache;
	private final IgniteCache<Long, Trade> tradeCache;

	public static void main(String args[]) throws Exception {
		IgniteClientHelper ich = new IgniteClientHelper();
		ich.close();
	}

	public IgniteClientHelper() throws Exception {
		this(true);
	}

	public IgniteClientHelper(boolean destroyCaches) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");

		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setClientMode(true);
		cfg.setPeerClassLoadingEnabled(true);
		cfg.setDeploymentMode(CONTINUOUS);
		//configureDataRegion(cfg);

		TcpDiscoverySpi tcpDiscoverySpi = new org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder();
		ArrayList<String> list = new ArrayList<String>();
		list.add("127.0.0.1:47500..47510");

		tcpDiscoveryVmIpFinder.setAddresses(list);
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);

		cfg.setDiscoverySpi(tcpDiscoverySpi);

		ignite = Ignition.start(cfg);
		ignite.cluster().state(ACTIVE);
		ignite.cluster().tag("Demo Cluster");

		if (destroyCaches) {
			System.out.println("Deleting Caches...");

			ignite.destroyCache(ACCOUNT_CACHE_NAME);
			ignite.destroyCache(HOLDINGS_CACHE_NAME);
			ignite.destroyCache(PRODUCT_CACHE_NAME);
			ignite.destroyCache(PRODUCT_PRICE_CACHE_NAME);
			ignite.destroyCache(TRADE_CACHE_NAME);
		}

		System.out.println("Creating Caches...");

		accountCache = ignite.getOrCreateCache(new AccountCacheConfiguration<Long, Account>());
		holdingsCache = ignite.getOrCreateCache(new HoldingsCacheConfiguration<HoldingsKey, Holdings>());
		productCache = ignite.getOrCreateCache(new ProductCacheConfiguration<Long, Product>());
		productPriceCache = ignite.getOrCreateCache(new ProductPriceCacheConfiguration<Long, ProductPrice>());
		tradeCache = ignite.getOrCreateCache(new TradeCacheConfiguration<Long, Trade>());
	}

	public Ignite getIgnite() {
		return ignite;
	}

	@Override
	public void close() throws Exception {
		ignite.close();
	}

	public IgniteCache<Long, Account> getAccountCache() {
		return accountCache;
	}

	public IgniteCache<HoldingsKey, Holdings> getHoldingsCache() {
		return holdingsCache;
	}

	public IgniteCache<Long, Product> getProductCache() {
		return productCache;
	}

	public IgniteCache<Long, ProductPrice> getProductPriceCache() {
		return productPriceCache;
	}

	public IgniteCache<Long, Trade> getTradeCache() {
		return tradeCache;
	}

	public static class AccountCacheConfiguration<K, V> extends CacheConfiguration<Long, Account> {

		private static final long serialVersionUID = 0L;

		public AccountCacheConfiguration() {
			// Set required cache configuration properties.
			setName(ACCOUNT_CACHE_NAME);
			setIndexedTypes(Long.class, Account.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class HoldingsCacheConfiguration<K, V> extends CacheConfiguration<HoldingsKey, Holdings> {

		private static final long serialVersionUID = 0L;

		public HoldingsCacheConfiguration() {
			// Set required cache configuration properties.
			setName(HOLDINGS_CACHE_NAME);
			setIndexedTypes(HoldingsKey.class, Holdings.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductCacheConfiguration<K, V> extends CacheConfiguration<Long, Product> {

		private static final long serialVersionUID = 0L;

		public ProductCacheConfiguration() {
			// Set required cache configuration properties.
			setName(PRODUCT_CACHE_NAME);
			setIndexedTypes(Long.class, Product.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductPriceCacheConfiguration<K, V> extends CacheConfiguration<Long, ProductPrice> {

		private static final long serialVersionUID = 0L;

		public ProductPriceCacheConfiguration() {
			// Set required cache configuration properties.
			setName(PRODUCT_PRICE_CACHE_NAME);
			setIndexedTypes(Long.class, ProductPrice.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class TradeCacheConfiguration<K, V> extends CacheConfiguration<Long, Trade> {

		private static final long serialVersionUID = 0L;

		public TradeCacheConfiguration() {
			// Set required cache configuration properties.
			setName(TRADE_CACHE_NAME);
			setIndexedTypes(Long.class, Trade.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}
}
