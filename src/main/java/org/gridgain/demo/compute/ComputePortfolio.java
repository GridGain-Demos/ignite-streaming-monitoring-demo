package org.gridgain.demo.compute;

import java.util.Iterator;
import java.util.List;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.gridgain.demo.IgniteClientHelper;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holdings;
import org.gridgain.demo.model.HoldingsKey;

public class ComputePortfolio {

	private static final String PORTFOLIO_QUERY = "select PRODUCT.SYMBOL, PRODUCTPRICE.price, HOLDINGS.shareCount, PRODUCTPRICE.price * HOLDINGS.shareCount as total "
			+ "from HOLDINGS " + "INNER JOIN PRODUCTPRICE " + "on HOLDINGS.productId = PRODUCTPRICE.productId "
			+ "INNER JOIN PRODUCT " + "ON HOLDINGS.PRODUCTID = PRODUCT.ID " + "WHERE HOLDINGS.ACCOUNTID = ? "
			+ "AND PRODUCTPRICE.TIME = (SELECT MAX(TIME) from PRODUCTPRICE where PRODUCTID = HOLDINGS.PRODUCTID)";

	private static final int SIMULATION_COUNT = 1000;
	private static final int YEAR_COUNT = 10;
	private static final double INFLATION = 5.1;
	private static final double MEAN = 1.0;
	private static final double STD_DEV = .5;

	public ComputePortfolio(IgniteClientHelper ich, Long accountId) throws IgniteException {
		HoldingsKey key = new HoldingsKey(accountId, 1l);

		IgniteCompute compute = ich.getIgnite().compute();
		IgniteCache<Long, Account> accountCache = ich.getAccountCache();
		IgniteCache<HoldingsKey, Holdings> cache = ich.getHoldingsCache();

		compute.affinityRun(IgniteClientHelper.HOLDINGS_CACHE_NAME, key, () -> {
			SqlFieldsQuery query = new SqlFieldsQuery(PORTFOLIO_QUERY);
			query.setArgs(accountId);
			query.setCollocated(true);

			Iterator<List<?>> cursor = cache.query(query).getAll().iterator();
			double portfolioValue = 0.0;
			while (cursor.hasNext()) {
				List<?> vars = cursor.next();
				portfolioValue += (double) vars.get(3);
			}

			Account account = accountCache.get(accountId);
			MonteCarloSimulator simulator = new MonteCarloSimulator();
			PortfolioStatistics ps = simulator.runSimulation(account.getName(), SIMULATION_COUNT, YEAR_COUNT, MEAN, STD_DEV, portfolioValue, INFLATION);
			
			System.out.println(ps.toString());
		});
	}

}