package org.gridgain.demo;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheMode.REPLICATED;
import static org.apache.ignite.cluster.ClusterState.ACTIVE;
import static org.apache.ignite.configuration.DeploymentMode.CONTINUOUS;

import java.util.ArrayList;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryBasicNameMapper;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public class IgniteClientHelper implements AutoCloseable {

	public static final String DATA_REGION = "MyDataRegion";
	public static final String SQL_SCHEMA = "PUBLIC";

	public static final String ACCOUNT_CACHE_NAME = "Account";
	public static final String HOLDINGS_CACHE_NAME = "Holding";
	public static final String PRODUCT_CACHE_NAME = "Product";
	public static final String PRODUCT_PRICE_CACHE_NAME = "ProductPrice";
	public static final String TRADE_CACHE_NAME = "Trade";

	private final Ignite ignite;
	private final IgniteCache<String, Account> accountCache;
	private final IgniteCache<HoldingKey, Holding> holdingCache;
	private final IgniteCache<String, Product> productCache;
	private final IgniteCache<String, ProductPrice> productPriceCache;
	private final IgniteCache<String, Trade> tradeCache;

	public static void main(String args[]) throws Exception {
		try (IgniteClientHelper ich = new IgniteClientHelper()) {
			System.out.println("IgniteClientHelper");
		}
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

		TcpDiscoverySpi tcpDiscoverySpi = new org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder();
		ArrayList<String> list = new ArrayList<String>();
		list.add("127.0.0.1:47500..47510");

		tcpDiscoveryVmIpFinder.setAddresses(list);
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);

		cfg.setDiscoverySpi(tcpDiscoverySpi);

		BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
		BinaryBasicNameMapper nameMapper = new BinaryBasicNameMapper();
		nameMapper.setSimpleName(true);
		binaryConfiguration.setNameMapper(nameMapper);
		cfg.setBinaryConfiguration(binaryConfiguration);

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

			System.out.println("Creating Caches...");
		}
		accountCache = ignite.getOrCreateCache(new AccountCacheConfiguration<String, Account>());
		holdingCache = ignite.getOrCreateCache(new HoldingCacheConfiguration<HoldingKey, Holding>());
		productCache = ignite.getOrCreateCache(new ProductCacheConfiguration<String, Product>());
		productPriceCache = ignite.getOrCreateCache(new ProductPriceCacheConfiguration<String, ProductPrice>());
		tradeCache = ignite.getOrCreateCache(new TradeCacheConfiguration<String, Trade>());
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
			setName(ACCOUNT_CACHE_NAME);
			setIndexedTypes(String.class, Account.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class HoldingCacheConfiguration<K, V> extends CacheConfiguration<HoldingKey, Holding> {

		private static final long serialVersionUID = 0L;

		public HoldingCacheConfiguration() {
			// Set required cache configuration properties.
			setName(HOLDINGS_CACHE_NAME);
			setIndexedTypes(HoldingKey.class, Holding.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductCacheConfiguration<K, V> extends CacheConfiguration<String, Product> {

		private static final long serialVersionUID = 0L;

		public ProductCacheConfiguration() {
			// Set required cache configuration properties.
			setName(PRODUCT_CACHE_NAME);
			setIndexedTypes(String.class, Product.class);
			setCacheMode(REPLICATED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class ProductPriceCacheConfiguration<K, V> extends CacheConfiguration<String, ProductPrice> {

		private static final long serialVersionUID = 0L;

		public ProductPriceCacheConfiguration() {
			// Set required cache configuration properties.
			setName(PRODUCT_PRICE_CACHE_NAME);
			setIndexedTypes(String.class, ProductPrice.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}

	public static class TradeCacheConfiguration<K, V> extends CacheConfiguration<String, Trade> {

		private static final long serialVersionUID = 0L;

		public TradeCacheConfiguration() {
			// Set required cache configuration properties.
			setName(TRADE_CACHE_NAME);
			setIndexedTypes(String.class, Trade.class);
			setBackups(1);
			setCacheMode(PARTITIONED);
			setDataRegionName(DATA_REGION);
			setSqlSchema(SQL_SCHEMA);
			setStatisticsEnabled(true);
		}
	}
}
