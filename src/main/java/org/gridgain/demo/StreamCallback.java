package org.gridgain.demo;

import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.gridgain.demo.RandomMarketTicker.Stock;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holdings;
import org.gridgain.demo.model.HoldingsKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public class StreamCallback {
	
	private IgniteClientHelper ich;
    
    public StreamCallback(IgniteClientHelper ich) {
        this.ich = ich;
    }

    public void message(Trade trade) {
        IgniteTransactions txs = ich.getIgnite().transactions();

        try (Transaction tx = txs.txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
            // Using transactions to demonstrate tracing capabilities.
            ich.getTradeCache().put(trade.getId(), trade);
            tx.commit();
        }
    }

	public void message(ProductPrice productPrice) {
		ich.getProductPriceCache().put(productPrice.getId(), productPrice);
	}
	
	public void message(Account account) {
		ich.getAccountCache().put(account.getId(), account);
	}
	public void message(Stock stock) {
		Product product = stock.toProduct();
		ich.getProductCache().put(product.getId(), product);
	}

	public void message(HoldingsKey holdingsKey, Holdings holdings) {
		ich.getHoldingsCache().put(holdingsKey, holdings);
	}

}
