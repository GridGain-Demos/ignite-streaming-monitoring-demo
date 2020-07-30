` docker-compose -f docker/ignite-cluster.yaml up`

`docker-compose -f docker/ignite-cluster.yaml up --scale ignite-server-node=2`

`docker-compose -f docker/control-center.yaml up`

`docker exec -it docker_ignite-server-node_1 bash`

`./management.sh --uri http://localhost:8443,http://control-center-frontend:8443`

## Building Application Image

* Build the app: `mvn clean package`

* Create a Docker image for the app: `docker build -f docker/StreamingAppDockerfile -t ignite-streaming-app .`

* Run the container: `docker-compose -f docker/ignite-streaming-app.yaml up`


ENTRYPOINT ["java", "-jar", "ignite-streaming-app.jar", "execTime=$execTime"]

## Enabling tracing

`docker exec -it docker_ignite-server-node_1 bash`

`JVM_OPTS="-DIGNITE_ENABLE_EXPERIMENTAL_COMMAND=true" ./control.sh --tracing-configuration set --scope TX --sampling-rate 1`
