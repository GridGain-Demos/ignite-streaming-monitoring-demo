docker-compose -f docker/ignite-streaming-app.yaml stop
docker container rm ignite-streaming-app
docker image rm ignite-streaming-app

mvn clean package
docker build -f docker/StreamingAppDockerfile -t ignite-streaming-app .

