package org.gridgain.demo;

import java.util.List;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Trade;

public class HoldingsUpdater {

	public HoldingsUpdater(IgniteClientHelper ich) {
		System.out.println("Starting Holding Updater (Continuous Query)...");

		IgniteCache<String, Trade> tradeCache = ich.getTradeCache();
		IgniteCache<HoldingKey, Holding> holdingsCache = ich.getHoldingCache();

		ContinuousQuery<Long, Trade> query = new ContinuousQuery<>();

		query.setLocalListener(new CacheEntryUpdatedListener<Long, Trade>() {
			@Override
			public void onUpdated(Iterable<CacheEntryEvent<? extends Long, ? extends Trade>> events)
					throws CacheEntryListenerException {
				for (CacheEntryEvent<? extends Long, ? extends Trade> e : events) {
					try {
						Trade trade = e.getValue();
						System.out.println("key=" + e.getKey() + ", val=" + trade.toString());
						SqlFieldsQuery sfq = new SqlFieldsQuery(
								"SELECT ID, SHARECOUNT FROM HOLDING WHERE ACCOUNTID = ? AND SYMBOL = ?");
						sfq.setArgs(trade.getAccountId(), trade.getSymbol());
						List<?> row = holdingsCache.query(sfq).getAll().get(0);
						String hid = (String) row.get(0);
						Long sc = (Long) row.get(1);

						if (trade.getTrade_type().equals("BUY")) {
							sc += trade.getOrder_quantity();
						} else {
							sc -= trade.getOrder_quantity();
						}

						sfq = new SqlFieldsQuery("UPDATE HOLDING SET LASTTRADEDATE = ?, SHARECOUNT = ? WHERE ID = ?");
						sfq.setArgs(trade.getOrder_date(), sc, hid);
						Long updateCount = (Long) holdingsCache.query(sfq).getAll().get(0).get(0);
						System.out.println("Updated " + updateCount + " rows with Holding id = " + hid);
					} catch (Exception ex) {
						System.err.println(ex.getMessage());
						ex.printStackTrace();
					}
				}

			}
		});

		tradeCache.query(query);
	}

}
