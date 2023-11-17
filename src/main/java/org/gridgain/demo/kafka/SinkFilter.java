package org.gridgain.demo.kafka;

import java.util.function.Predicate;

import org.gridgain.demo.kafka.model.ProductPrice;
import org.gridgain.kafka.CacheEntry;

public class SinkFilter implements Predicate<CacheEntry> {

	@Override
	public boolean test(CacheEntry entry) {
		if (entry.getValue().getClass().equals(ProductPrice.class)) {
			return !((ProductPrice)entry.getValue()).getSymbol().equals("IBM");
		}
		return false;
	}

}
