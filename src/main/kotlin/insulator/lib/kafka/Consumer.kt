package insulator.lib.kafka

import arrow.core.Tuple3
import insulator.di.clusterScopedGet
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.joda.time.Minutes
import org.koin.core.qualifier.named
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread

class Consumer {

    private var threadLoop: Thread? = null
    private var running = false

    fun start(topic: String, from: ConsumeFrom, valueFormat: DeserializationFormat, callback: (String?, String, Long) -> Unit) {
        if (isRunning()) throw Throwable("Consumer already running")
        val consumer: Consumer<Any, Any> = when (valueFormat) {
            DeserializationFormat.Avro -> clusterScopedGet(named("avroConsumer"))
            DeserializationFormat.String -> clusterScopedGet()
        }
        initializeConsumer(consumer, topic, from)
        running = true
        loop(consumer, callback)
    }

    fun isRunning() = running

    fun stop() {
        running = false
        threadLoop?.join()
    }

    private fun loop(consumer: Consumer<Any, Any>, callback: (String?, String, Long) -> Unit) {
        threadLoop = thread {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                records.toList()
                        .map { parse(it) }
                        .forEach { (k, v, t) -> callback(k, v, t) }
            }
        }
    }

    private fun initializeConsumer(consumer: Consumer<Any, Any>, topic: String, from: ConsumeFrom) {
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)

        when (from) {
            ConsumeFrom.Now -> consumer.seekToEnd(partitions)
            ConsumeFrom.Beginning -> consumer.seekToBeginning(partitions)
            ConsumeFrom.LastHour -> {
                val time = Instant.now().minus(Duration.ofMinutes(30)).toEpochMilli()
                assignPartitionByTime(consumer, partitions, time)
            }
            ConsumeFrom.Today -> {
                val time = Instant.now().minus(Duration.ofDays(1)).toEpochMilli()
                assignPartitionByTime(consumer, partitions, time)
            }
        }
    }

    private fun assignPartitionByTime(consumer: Consumer<Any, Any>, partitions: List<TopicPartition>, time: Long) {
        consumer.offsetsForTimes(partitions.map { it to time }.toMap())
                .forEach {
                    if (it.value != null) consumer.seek(it.key, it.value.offset())
                    else consumer.seekToEnd(partitions)
                }
    }

    private fun parse(record: ConsumerRecord<Any, Any>): Tuple3<String?, String, Long> {
        //todo: parse this thing
        return Tuple3(record.key()?.toString(), record.value().toString(), record.timestamp())
    }
}

enum class ConsumeFrom {
    Now,
    LastHour,
    Today,
    Beginning
}

enum class DeserializationFormat {
    String,
    Avro
}