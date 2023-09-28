# Kafka Toy Cluster

This is Toy Cluster with Kafka nodes. Created for experiments.

It includes following services:

-   [Apache Kafka 3.5](https://kafka.apache.org/35/documentation.html)
-   [Kafka-UI](https://github.com/provectus/kafka-ui/)
-   [Apache Zookeeper 3.9.0](https://zookeeper.apache.org/doc/r3.9.0/)

## Run

Just execute:

```sh
    % docker-compose up -d
```

Go to [Kafka UI](http://localhost:8080/) to see all connected Producer/Consumer and Topics.

Producer/Consumer can use `localhost:9092` for connection.

Current state of all nodes will be saved at [data](/data) directory.

To stop:

```sh
    % docker-compose down
```

## License

Distributed under MIT License.
