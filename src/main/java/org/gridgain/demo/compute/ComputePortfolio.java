package org.gridgain.demo.compute;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.gridgain.demo.IgniteClientHelper;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;

public class ComputePortfolio implements Serializable {

	private static final long serialVersionUID = -5374688715244448641L;

	private static final String PORTFOLIO_QUERY = "select PRODUCT.SYMBOL, PRODUCTPRICE.price, HOLDINGS.shareCount, PRODUCTPRICE.price * HOLDINGS.shareCount as total "
			+ "from HOLDINGS " + "INNER JOIN PRODUCTPRICE " + "on HOLDINGS.SYMBOL = PRODUCTPRICE.SYMBOL "
			+ "INNER JOIN PRODUCT " + "ON HOLDINGS.SYMBOL = PRODUCT.SYMBOL " + "WHERE HOLDINGS.ACCOUNTID = ? "
			+ "AND PRODUCTPRICE.TIME = (SELECT MAX(TIME) from PRODUCTPRICE where SYMBOL = HOLDINGS.SYMBOL)";

	private static final int SIMULATION_COUNT = 1000;
	private static final int YEAR_COUNT = 10;
	private static final double INFLATION = 5.1;
	private static final double MEAN = 1.0;
	private static final double STD_DEV = .5;

	public ComputePortfolio(IgniteClientHelper ich, String accountId) throws IgniteException {

		IgniteCompute compute = ich.getIgnite().compute();
		IgniteCache<String, Account> accountCache = ich.getAccountCache();
		IgniteCache<HoldingKey, Holding> cache = ich.getHoldingCache();

		compute.affinityRun(IgniteClientHelper.ACCOUNT_CACHE_NAME, accountId, () -> {
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