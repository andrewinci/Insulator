package insulator.lib.kafka

import arrow.core.Tuple3
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import kotlin.concurrent.thread

class Consumer(private val consumer: Consumer<Any, Any>) {

    private var threadLoop: Thread? = null
    private var running = false

    fun start(topic: String, from: ConsumeFrom, callback: (String?, String, Long) -> Unit) {
        if (isRunning()) throw Throwable("Consumer already running")
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)
        seek(consumer, partitions, from)
        running = true
        loop(callback)
    }

    fun isRunning() = running

    fun stop() {
        running = false
        threadLoop?.join()
    }

    private fun loop(callback: (String?, String, Long) -> Unit) {
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

    private fun seek(consumer: Consumer<Any, Any>, partitions: List<TopicPartition>, from: ConsumeFrom) {
        when (from) {
            ConsumeFrom.Now -> consumer.seekToEnd(partitions)
            ConsumeFrom.Beginning -> consumer.seekToBeginning(partitions)
        }
    }

    private fun parse(record: ConsumerRecord<Any, Any>): Tuple3<String?, String, Long> {
        //todo: parse this thing
        return Tuple3(record.key()?.toString(), record.value().toString(), record.timestamp())
    }
}

enum class ConsumeFrom {
    Now,
    Beginning
}

enum class DeserializationFormat {
    String,
    Avro
}