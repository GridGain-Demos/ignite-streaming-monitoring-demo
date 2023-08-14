# Apache Ignite Market Orders Streaming Demo

The demo starts a simple application that simulates trading activities of a stock exchange.

- The application creates account & portfolios and then generates stock ticker prices and trade events (buy & sell)
- There is a continuous query registered that calculates each accounts holding
- There are 5 caches created;
 - ACCOUNT  **Replicated**
 - HOLDINGS **Partitioned**
 - PRODUCT  **Replicated**
 - PRODUCTPRICE **Partitioned**
 - TRADE **Partitioned**

- There are compute jobs submitted every 30 seconds for each account to perform a MonteCarlo Simulation on each portfolio, paremeters for this can be adjusted in 

**``` org.gridgain.demo.compute.ComputePortfolio class```**

```java
	private static final int SIMULATION_COUNT = 1000;
	private static final int YEAR_COUNT = 10;
	private static final double INFLATION = 5.1;
	private static final double MEAN = 1.0;
	private static final double STD_DEV = .5;
```

## Setup
Application can be run locally or in Nebula

### Local server setup
It is suggested to run 2-3 server nodes to demonstrate partition distribution and HA capabilities.

The following environment variable needs to be set in order to enable code deployment.

```bash
export IGNITE_EVENT_DRIVEN_SERVICE_PROCESSOR_ENABLED=true
bin/ignite.sh -f .../ignite-streaming-monitoring-demo/config/config-server.xml
```

### Build the project

```bash
mvn clean package
```

### Deploy code to server via Control Centre

Add deployment unit and upload target/ignite-streaming-app.jar

### Run the application

```bash
export IGNITE_EVENT_DRIVEN_SERVICE_PROCESSOR_ENABLED=true
java -jar target/ignite-streaming-app.jar
```
