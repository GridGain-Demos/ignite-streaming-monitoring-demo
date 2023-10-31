package org.gridgain.demo;

import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public interface StreamCallback {
	    
    public void message(Trade trade);
	public void message(ProductPrice productPrice);
	public void message(Account account);
	public void message(Product product);
	public void message(HoldingKey holdingKey, Holding holding);

}