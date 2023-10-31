package org.gridgain.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.gridgain.demo.model.Account;
import org.gridgain.demo.model.Holding;
import org.gridgain.demo.model.HoldingKey;
import org.gridgain.demo.model.Product;
import org.gridgain.demo.model.ProductPrice;
import org.gridgain.demo.model.Trade;

public class IgniteStreamCallback implements StreamCallback {
	
	private IgniteClientHelper ich;
	private List<Account> accounts = new ArrayList<>();
    
    public IgniteStreamCallback(IgniteClientHelper ich) {
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
		accounts.add(account);
	}
	public void message(Product product) {
		ich.getProductCache().put(product.getSymbol(), product);
	}

	public void message(HoldingKey holdingsKey, Holding holding) {
		ich.getHoldingCache().put(holdingsKey, holding);
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}

}
