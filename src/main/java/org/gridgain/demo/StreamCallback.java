package org.gridgain.demo;

import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public class StreamCallback extends SubscribeCallback {

    /** Roughly one in every {@code HEAVY_TX_EVERY} trades runs as a heavier transaction. */
    private static final int HEAVY_TX_EVERY = 25;

    /** Number of extra trades the heavy transaction writes in a single commit. */
    private static final int HEAVY_TX_BATCH = 50;

    private AtomicLong counter = new AtomicLong();
	private IgniteCache<TradeKey, Trade> tradeCache;
	private Ignite ignite;
    
    public StreamCallback(Ignite ignite) {
        this.ignite = ignite;
		this.tradeCache = ignite.cache("Trade");
    }

    /**
     * @param nub
     * @param status
     */
    public void status(PubNub nub, PNStatus status) {

        if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            // Connect event.
            System.out.println("Connected to the market orders stream: " + status.toString());
        }
        else if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            System.err.println("Connection is lost:" + status.getErrorData().toString());
        }
        else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
            // Happens as part of our regular operation. This event happens when
            // radio / connectivity is lost, then regained.
            System.out.println("Reconnected to the market orders stream");
        }
        else {
            System.out.println("Connection status changes:" + status.toString());
        }
    }

    /**
     * @param nub
     * @param result
     */
    public void message(PubNub nub, PNMessageResult result) {
        JsonElement mes = result.getMessage();
        JsonObject json = mes.getAsJsonObject();

        long id = counter.incrementAndGet();
        TradeKey key = new TradeKey(id, new Random().nextInt(6) + 1);

        Trade trade = new Trade(
            json.get("symbol").getAsString(),
            json.get("order_quantity").getAsInt(),
            json.get("bid_price").getAsDouble(),
            json.get("trade_type").getAsString(),
            new Timestamp(json.get("timestamp").getAsLong() * 1000)
        );

        IgniteTransactions txs = ignite.transactions();

        if (id % HEAVY_TX_EVERY == 0) {
            // Every HEAVY_TX_EVERY trades, run a heavier transaction so the tracing screen
            // shows some variety. This one uses OPTIMISTIC/SERIALIZABLE and writes a batch of
            // trades in a single commit, so its commit spans more partitions and nodes and takes
            // noticeably longer than the steady single-key transactions below. It stands out in
            // the trace list, letting you drill into the slower transaction and see where the
            // time goes.
            try (Transaction tx = txs.txStart(TransactionConcurrency.OPTIMISTIC, TransactionIsolation.SERIALIZABLE)) {
                tradeCache.put(key, trade);

                for (int i = 0; i < HEAVY_TX_BATCH; i++) {
                    tradeCache.put(new TradeKey(counter.incrementAndGet(), new Random().nextInt(6) + 1), trade);
                }

                tx.commit();
            }
        }
        else {
            try (Transaction tx = txs.txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
                // Using transactions to demonstrate tracing capabilities.
                tradeCache.put(key, trade);

                tx.commit();
            }
        }
    }

    /**
     * @param nub
     * @param result
     */
    public void presence(PubNub nub, PNPresenceEventResult result) {
        System.out.println("Stream presence event: " + result.toString());
    }
}
