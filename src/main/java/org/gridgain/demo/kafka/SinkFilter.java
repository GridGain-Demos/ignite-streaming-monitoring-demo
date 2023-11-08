package org.gridgain.demo.kafka;

import org.apache.ignite.cache.CacheEntry;
import org.gridgain.demo.kafka.model.ProductPrice;

public class SinkFilter implements java.util.function.Predicate<CacheEntry<?,?>> {

	@Override
	public boolean test(CacheEntry<?,?> entry) {
		if (entry.getValue().getClass().equals(ProductPrice.class)) {
			return !((ProductPrice)entry.getValue()).getSymbol().equals("IBM");
		}
		return false;
	}

}
