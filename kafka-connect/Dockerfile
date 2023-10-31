FROM confluentinc/cp-kafka-connect:7.5.0

ENV CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components"
ENV CONNECT_BOOTSTRAP_SERVERS: "localhost:29092"

RUN confluent-hub install --no-prompt gridgain/gridgain-kafka-connect:8.8.34
