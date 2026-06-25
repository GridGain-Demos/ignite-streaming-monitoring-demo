# Apache Ignite Market Orders Streaming Demo

The demo starts a sample application that simulates the trading activity of a stock exchange. By default, the
application generates simulated market orders locally and streams them into a running GridGain cluster, so no Internet
connection is required.

The demo supports the GridGain Control Center [written tutorial](https://www.gridgain.com/docs/tutorials/management-monitoring/overview)
and [instructor-led foundation course](https://www.gridgain.com/products/services/training/how-monitor-and-manage-apache-ignite-gridgain-control-center).

## Streaming live data from PubNub

Instead of generating orders locally, the application can stream live data from the
[PubNub Market Orders data stream](https://www.pubnub.com/developers/realtime-data-streams/financial-securities-market-orders/).
This path requires an Internet connection.

To enable it, set `USE_PUB_NUB` to `true` in `src/main/java/org/gridgain/demo/StreamingApplication.java`, then rebuild
the application jar and Docker image. The stream's subscribe key is already included in the code, so no PubNub account
is needed. The PubNub Market Orders stream is a public demo provided by PubNub and may change or become unavailable
independently of this demo.
