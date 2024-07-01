# Kafka Toy Cluster

This is Toy Cluster with Kafka nodes. Created for experiments.

It includes following services:

-   [Apache Kafka 3.9](https://kafka.apache.org/39/documentation.html)
-   [Kafka-UI](https://github.com/provectus/kafka-ui/)

## Run

Just execute:

```sh
$ ./start.sh
```

Go to [Kafka UI](http://localhost:8080/) to see all connected Producer/Consumer and Topics.

Producer/Consumer can use `localhost:9092` for connection.

Current state of all nodes will be saved at [data](./data) directory.

To stop:

```sh
$ ./stop.sh
```

_Note:_ If you want clean-up all working data just execute `$ ./clean.sh` script.

## Examples

Running examples:

```sh
$ mvn clean install
$ ./create_topic.sh
$ ./consumer.sh
```

In separate shell:

```sh
$ ./producer.sh
```

## License

Distributed under MIT License.
