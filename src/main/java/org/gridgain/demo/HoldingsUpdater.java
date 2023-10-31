package org.gridgain.demo;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
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
					Trade trade = e.getValue();
					System.out.println("key=" + e.getKey() + ", val=" + trade.toString());
					HoldingKey key = new HoldingKey(trade.getAccountId(), trade.getSymbol());
					Holding h = holdingsCache.get(key);
					h.setLastTradeDate(trade.getOrder_date());
					if (trade.getTrade_type().equals("BUY")) {
						h.setShareCount(h.getShareCount() + trade.getOrder_quantity());
					} else {
						h.setShareCount(h.getShareCount() - trade.getOrder_quantity());
					}
					holdingsCache.put(new HoldingKey(h.getAccountId(), h.getSymbol()), h);
				}

			}
		});

		tradeCache.query(query);
	}

}
