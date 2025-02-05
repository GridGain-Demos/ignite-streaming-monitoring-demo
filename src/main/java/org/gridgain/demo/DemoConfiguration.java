package org.gridgain.demo;

import static org.apache.ignite.configuration.DeploymentMode.CONTINUOUS;

import java.util.ArrayList;

import org.apache.ignite.binary.BinaryBasicNameMapper;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.tracing.opencensus.OpenCensusTracingSpi;
import org.gridgain.control.agent.processor.deployment.ManagedDeploymentSpi;

public class DemoConfiguration extends IgniteConfiguration {

	public static final String DATA_REGION = "MyDataRegion";
	public static final String SQL_SCHEMA = "PUBLIC";

	public static final String ACCOUNT_CACHE_NAME = "Account";
	public static final String HOLDINGS_CACHE_NAME = "Holding";
	public static final String PRODUCT_CACHE_NAME = "Product";
	public static final String PRODUCT_PRICE_CACHE_NAME = "ProductPrice";
	public static final String TRADE_CACHE_NAME = "Trade";

	public DemoConfiguration() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("IGNITE_QUIET", "true");

		setPeerClassLoadingEnabled(true);
		setDeploymentMode(CONTINUOUS);

		TcpDiscoverySpi tcpDiscoverySpi = new org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder();
		ArrayList<String> list = new ArrayList<String>();
		list.add("127.0.0.1:47500..47510");

		tcpDiscoveryVmIpFinder.setAddresses(list);
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);

		setDiscoverySpi(tcpDiscoverySpi);

		BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
		BinaryBasicNameMapper nameMapper = new BinaryBasicNameMapper();
		nameMapper.setSimpleName(true);
		binaryConfiguration.setNameMapper(nameMapper);
		setBinaryConfiguration(binaryConfiguration);

		setTracingSpi(new OpenCensusTracingSpi());
		setDeploymentSpi(new ManagedDeploymentSpi());

		DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();

		DataRegionConfiguration defaultDataRegionConfiguration = new DataRegionConfiguration();
		defaultDataRegionConfiguration.setName("Default_Region");
		defaultDataRegionConfiguration.setInitialSize(100 * 1024 * 1024);

		dataStorageConfiguration.setDefaultDataRegionConfiguration(defaultDataRegionConfiguration);
		dataStorageConfiguration.setStoragePath("/tmp/GGData");

		DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
		dataRegionConfiguration.setName(DATA_REGION);
		dataRegionConfiguration.setPersistenceEnabled(true);
		dataRegionConfiguration.setInitialSize(200 * 1024 * 1024);
		dataRegionConfiguration.setMaxSize(400 * 1024 * 1024);
		dataRegionConfiguration.setPageEvictionMode(DataPageEvictionMode.RANDOM_2_LRU);
		dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);

		setDataStorageConfiguration(dataStorageConfiguration);
	}
}
