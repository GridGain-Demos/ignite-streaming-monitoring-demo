package org.gridgain.demo;

import java.util.Arrays;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class PubNubMarketTicker implements MarketTicker {
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

	private StreamCallback streamCallback;

    public PubNubMarketTicker(StreamCallback streamCallback) {
		this.streamCallback = streamCallback;
    }

    public void start() {
        PNConfiguration cfg = new PNConfiguration();
        cfg.setSubscribeKey(STREAM_SUBSCRIPION_KEY);

        stream = new PubNub(cfg);

        stream.addListener(streamCallback);
        stream.subscribe().channels(Arrays.asList(STREAM_NAME)).execute();
    }

    public void stop() {
        stream.unsubscribe().execute();
    }

}
