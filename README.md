# Apache Ignite Market Orders Streaming Demo

The demo starts a simple application that simulates trading activities of a stock exchange.

- The application creates account & portfolios and then generates stock ticker prices and trade events (buy & sell)
- There is a continuous query registered that calculates each accounts holding
- There are 5 caches created;
 - ACCOUNT **Replicated**
 - HOLDINGS **Partitioned**
 - PRODUCT **Replicated**
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

## Setup Kafka
1. Download binary package

    ```console
    curl -O https://packages.confluent.io/archive/7.5/confluent-7.5.1.tar.gz
    ```
2. Extract contents

    ``` 
    tar -xvzf confluent-7.5.1.tar.gz
    ```
3. Set Environment
    
    ```
    export CONFLUENT_HOME=.../confluent-7.5.0
    ```

    ```
    export PATH=$PATH:$CONFLUENT_HOME/bin
    ```
4. Install GridGain Kafka Connector
    
    ```
    confluent-hub install gridgain/gridgain-kafka-connect:8.8.34
    The component can be installed in any of the following Confluent Platform installations:
     1. .../confluent-7.5.1 (based on $CONFLUENT_HOME)
     2. .../confluent-7.5.1 (found in the current directory)    
     3. .../confluent-7.5.1 (where this tool is installed)

    Choose one of these to continue the installation (1-3): 1

    Do you want to install this into .../confluent-7.5.1/share/confluent-hub-components? (yN) y

    You are about to install \'gridgain-kafka-connect\' from GridGain Systems, Inc., as published on Confluent Hub. 

    Do you want to continue? (yN) y

    Downloading component Kafka Connect GridGain 8.8.34, provided by GridGain Systems, Inc. from Confluent Hub and installing into .../confluent-7.5.1/share/confluent-hub-components 
    Detected Worker\'s configs:

      1. Standard: .../confluent-7.5.1/etc/kafka/connect-distributed.properties 
      2. Standard: .../confluent-7.5.1/etc/kafka/connect-standalone.properties 
      3. Standard: .../confluent-7.5.1/etc/schema-registry/connect-avro-distributed.properties 
      4. Standard: .../confluent-7.5.1/etc/schema-registry/connect-avro-standalone.properties 

    Do you want to update all detected configs? (yN) y

    Adding installation directory to plugin path in the following files: 

    .../confluent-7.5.1/etc/kafka/connect-distributed.properties 

    .../confluent-7.5.1/etc/kafka/connect-standalone.properties 

    .../confluent-7.5.1/etc/schema-registry/connect-avro-distributed.properties 

    .../confluent-7.5.1/etc/schema-registry/connect-avro-standalone.properties 

    Completed 

    ```

```bash
confluent local services start
confluent local services connect stop
cp -r ~/gridgain/gridgain-ultimate-8.8.34/libs/optional/control-center-agent share/confluent-hub-components/gridgain-gridgain-kafka-connect/lib
connect-standalone etc/kafka/connect-standalone.properties ~/git/ignite-streaming-monitoring-demo/kafka-connect/gridgain-kafka-connect-sink.properties
```