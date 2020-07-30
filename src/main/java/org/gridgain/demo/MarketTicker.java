package org.gridgain.demo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;

public class MarketTicker {
    /**
     * Stream object.
     */
    private PubNub stream;

    /**
     * PubNub stream name.
     */
    protected final static String STREAM_NAME = "pubnub-market-orders";

    /**
     * PubNub stream subscription key.
     */
    private String STREAM_SUBSCRIPION_KEY = "sub-c-4377ab04-f100-11e3-bffd-02ee2ddab7fe";

    /**
     * A reference to the utility cache.
     */
    private IgniteCache utilityCache;

    /**
     * IDs generator.
     */
    private AtomicLong counter = new AtomicLong();

    public MarketTicker(IgniteCache utilityCache) {
        this.utilityCache = utilityCache;
    }

    public void start() {
        PNConfiguration cfg = new PNConfiguration();
        cfg.setSubscribeKey(STREAM_SUBSCRIPION_KEY);

        stream = new PubNub(cfg);

        stream.addListener(new StreamCallback());
        stream.subscribe().channels(Arrays.asList(STREAM_NAME)).execute();
    }

    public void stop() {
        stream.unsubscribe().execute();
    }

    private class StreamCallback extends SubscribeCallback {

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

            SqlFieldsQuery query = new SqlFieldsQuery(
                "INSERT INTO Trade (id, buyer_id, symbol, order_quantity, bid_price, trade_type, order_date) VALUES (?,?,?,?,?,?,?)").
                setSchema("PUBLIC");

            utilityCache.query(query.setArgs(
                counter.incrementAndGet(), /* id */
                new Random().nextInt(6) + 1, /* buyer_id */
                json.get("symbol").getAsString(),
                json.get("order_quantity").getAsInt(),
                json.get("bid_price").getAsDouble(),
                json.get("trade_type").getAsString(),
                new Timestamp(json.get("timestamp").getAsLong() * 1000)
            ));
        }

        /**
         * @param nub
         * @param result
         */
        public void presence(PubNub nub, PNPresenceEventResult result) {
            System.out.println("Stream presence event: " + result.toString());
        }
    }

}
