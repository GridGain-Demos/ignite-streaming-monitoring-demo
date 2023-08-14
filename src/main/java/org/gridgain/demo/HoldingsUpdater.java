package org.gridgain.demo;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.gridgain.demo.model.Holdings;
import org.gridgain.demo.model.HoldingsKey;
import org.gridgain.demo.model.Trade;

public class HoldingsUpdater {

	public HoldingsUpdater(IgniteClientHelper ich) {
		System.out.println("Starting Holdings Updater (Continuous Query)...");

		IgniteCache<Long, Trade> tradeCache = ich.getTradeCache();
		IgniteCache<HoldingsKey, Holdings> holdingsCache = ich.getHoldingsCache();

		ContinuousQuery<Long, Trade> query = new ContinuousQuery<>();

		query.setLocalListener(new CacheEntryUpdatedListener<Long, Trade>() {
			@Override
			public void onUpdated(Iterable<CacheEntryEvent<? extends Long, ? extends Trade>> events)
					throws CacheEntryListenerException {
				for (CacheEntryEvent<? extends Long, ? extends Trade> e : events) {
					Trade trade = e.getValue();
					System.out.println("key=" + e.getKey() + ", val=" + trade.toString());
					HoldingsKey key = new HoldingsKey(trade.getAccountId(), trade.getProductId());
					Holdings h = holdingsCache.get(key);
					h.setLastTradeDate(trade.getOrder_date());
					if (trade.getTrade_type().equals("BUY")) {
						h.setShareCount(h.getShareCount() + trade.getOrder_quantity());
					} else {
						h.setShareCount(h.getShareCount() - trade.getOrder_quantity());
					}
					holdingsCache.put(new HoldingsKey(h.getAccountId(), h.getProductId()), h);
				}

			}
		});

		tradeCache.query(query);
	}

}
