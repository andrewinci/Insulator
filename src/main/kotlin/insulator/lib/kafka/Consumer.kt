package insulator.lib.kafka

import arrow.core.Tuple3
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.kafka.helpers.ConsumerFactory
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread

class Consumer(
    private val converter: AvroToJsonConverter,
    private val consumerFactory: ConsumerFactory
) {

    private var threadLoop: Thread? = null
    private var running = false

    fun start(topic: String, from: ConsumeFrom, valueFormat: DeserializationFormat, callback: (List<Tuple3<String?, String, Long>>) -> Unit) {
        if (isRunning()) throw Throwable("Consumer already running")
        val consumer = consumerFactory.build(valueFormat)
        initializeConsumer(consumer, topic, from)
        running = true
        loop(consumer, callback)
    }

    fun isRunning() = running

    fun stop() {
        running = false
        threadLoop?.join()
    }

    private fun loop(consumer: Consumer<Any, Any>, callback: (List<Tuple3<String?, String, Long>>) -> Unit) {
        threadLoop = thread {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                callback(records.toList().map { parse(it) })
            }
        }
    }

    private fun initializeConsumer(consumer: Consumer<Any, Any>, topic: String, from: ConsumeFrom) {
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)

        when (from) {
            ConsumeFrom.Now -> consumer.seekToEnd(partitions)
            ConsumeFrom.LastHour -> {
                val time = Instant.now().minus(Duration.ofMinutes(60)).toEpochMilli()
                assignPartitionByTime(consumer, partitions, time)
            }
            ConsumeFrom.LastDay -> {
                val time = Instant.now().minus(Duration.ofDays(1)).toEpochMilli()
                assignPartitionByTime(consumer, partitions, time)
            }
            ConsumeFrom.LastWeek -> {
                val time = Instant.now().minus(Duration.ofDays(7)).toEpochMilli()
                assignPartitionByTime(consumer, partitions, time)
            }
            ConsumeFrom.Beginning -> consumer.seekToBeginning(partitions)
        }
    }

    private fun assignPartitionByTime(consumer: Consumer<Any, Any>, partitions: List<TopicPartition>, time: Long) {
        consumer.offsetsForTimes(partitions.map { it to time }.toMap())
            .forEach {
                when (val offset = it.value?.offset()) {
                    null -> consumer.seekToEnd(listOf(it.key))
                    else -> consumer.seek(it.key, offset)
                }
            }
    }

    private fun parse(record: ConsumerRecord<Any, Any>): Tuple3<String?, String, Long> {
        val parsedValue = if (record.value() is GenericRecord) converter.parse(record.value() as GenericRecord)
            // fallback to Avro.toString if unable to parse with the custom parser
            .fold({ record.value().toString() }, { it })
        else record.value().toString()
        return Tuple3(record.key()?.toString(), parsedValue, record.timestamp())
    }
}

enum class ConsumeFrom {
    Now,
    LastHour,
    LastDay,
    LastWeek,
    Beginning,
}

enum class DeserializationFormat {
    String,
    Avro,
}
