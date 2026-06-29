# Apache Ignite Market Orders Streaming Demo

The demo starts a sample application that simulates the trading activity of a stock exchange. By default, the
application generates simulated market orders locally and streams them into a running GridGain cluster, so no Internet
connection is required.

The demo supports the GridGain Control Center [written tutorial](https://www.gridgain.com/docs/tutorials/management-monitoring/overview)
and [instructor-led foundation course](https://www.gridgain.com/products/services/training/how-monitor-and-manage-apache-ignite-gridgain-control-center).

## Building and running

Docker compiles the application from source (see `docker/StreamingAppDockerfile`), so you do not need a local JDK or
Maven. Build the image and start the application with one command:

```bash
docker compose -f docker/ignite-streaming-app.yaml up -d --build
```

The application joins the running GridGain cluster as a client and streams trades into it. For the full walkthrough,
including how to start the cluster and GridGain Control Center first, follow the
[written tutorial](https://www.gridgain.com/docs/tutorials/management-monitoring/overview).

To build the application outside Docker instead, run `mvn clean package` with a local JDK 8 or later and Apache Maven
3.3 or later.

## Stopping and restarting

Stop the application without removing its container:

```bash
docker compose -f docker/ignite-streaming-app.yaml stop
```

Start the same container again:

```bash
docker compose -f docker/ignite-streaming-app.yaml start
```

A restart reuses the existing image, so you do not need `--build`. Rebuild with `--build` only the first time or after
you change the application source (for example, to toggle PubNub). To remove the container entirely, use
`docker compose -f docker/ignite-streaming-app.yaml down`.

## Streaming live data from PubNub

Instead of generating orders locally, the application can stream live data from the
[PubNub Market Orders data stream](https://www.pubnub.com/developers/realtime-data-streams/financial-securities-market-orders/).
This path requires an Internet connection.

To enable it, set `USE_PUB_NUB` to `true` in `src/main/java/org/gridgain/demo/StreamingApplication.java`, then rebuild
the application image with `docker compose -f docker/ignite-streaming-app.yaml up -d --build`. The stream's subscribe key
is already included in the code, so no PubNub account is needed. The PubNub Market Orders stream is a public demo
provided by PubNub and may change or become unavailable independently of this demo.
