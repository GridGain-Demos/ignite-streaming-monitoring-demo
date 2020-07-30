` docker-compose -f docker/ignite-cluster.yaml up`

`docker-compose -f docker/ignite-cluster.yaml up --scale ignite-server-node=2`

`docker exec -it docker_ignite-server-node_1 bash`

`docker-compose -f docker/control-center.yaml up`

`./management.sh --uri http://localhost:8443,http://control-center-frontend:8443`
