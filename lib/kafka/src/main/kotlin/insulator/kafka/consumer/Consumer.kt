package insulator.kafka.consumer

import arrow.core.Either
import insulator.kafka.model.Cluster
import insulator.kafka.model.Record
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import java.io.Closeable
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias GenericAvroToJsonConverter = (record: GenericRecord) -> Either<Throwable, String>
typealias ConsumerCallback = (List<Record>) -> Unit

fun consumer(cluster: Cluster, avroToJson: GenericAvroToJsonConverter) =
    Consumer(ConsumerFactory(cluster), avroToJson)

class Consumer(
    private val consumerFactory: ConsumerFactory,
    private val avroToJson: GenericAvroToJsonConverter
) : Closeable {

    private var threadLoop: Thread? = null
    private var running = false

    suspend fun start(topic: String, from: ConsumeFrom, valueFormat: DeserializationFormat, callback: ConsumerCallback) =
        suspendCoroutine<Unit> { continuation ->
            GlobalScope.launch {
                if (isRunning()) throw IllegalStateException("Consumer already running")
                val consumer = consumerFactory.build(valueFormat)
                initializeConsumer(consumer, topic, from)
                running = true
                loop(consumer, callback)
            }.invokeOnCompletion { exception ->
                if (exception == null) continuation.resume(Unit) else continuation.resumeWithException(exception)
            }
        }

    fun isRunning() = running

    override fun close() {
        running = false
        threadLoop?.join()
    }

    suspend fun stop() = suspendCoroutine<Unit> { continuation ->
        GlobalScope.launch {
            close()
        }.invokeOnCompletion { continuation.resume(Unit) }
    }

    private fun loop(consumer: Consumer<Any, Any>, callback: ConsumerCallback) {
        threadLoop = thread {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                callback(records.toList().map { parse(it) })
            }
        }
    }

    private fun Duration.ago() = Instant.now().minus(this).toEpochMilli()

    private fun initializeConsumer(consumer: Consumer<Any, Any>, topic: String, from: ConsumeFrom) {
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)
        when (from) {
            ConsumeFrom.Now -> Long.MAX_VALUE
            ConsumeFrom.Last15min -> Duration.ofMinutes(15).ago()
            ConsumeFrom.LastHour -> Duration.ofMinutes(60).ago()
            ConsumeFrom.LastDay -> Duration.ofDays(1).ago()
            ConsumeFrom.LastWeek -> Duration.ofDays(7).ago()
            ConsumeFrom.Beginning -> Long.MIN_VALUE
        }.let { assignPartitionByTime(consumer, partitions, it) }
    }

    private fun assignPartitionByTime(consumer: Consumer<Any, Any>, partitions: List<TopicPartition>, time: Long) =
        when (time) {
            Long.MAX_VALUE -> consumer.seekToEnd(partitions)
            Long.MIN_VALUE -> consumer.seekToBeginning(partitions)
            else ->
                consumer.offsetsForTimes(partitions.map { it to time }.toMap())
                    .forEach {
                        when (val offset = it.value?.offset()) {
                            null -> consumer.seekToEnd(listOf(it.key))
                            else -> consumer.seek(it.key, offset)
                        }
                    }
        }

    private fun parse(record: ConsumerRecord<Any, Any>): Record {
        val parsedValue = if (record.value() is GenericRecord) avroToJson(record.value() as GenericRecord)
            // fallback to Avro.toString if unable to parse with the custom parser
            .fold({ record.value().toString() }, { it })
        else record.value().toString()
        return Record(
            key = record.key()?.toString(),
            value = parsedValue,
            timestamp = record.timestamp(),
            partition = record.partition(),
            offset = record.offset(),
            headers = record.headers().toArray().map {
                it.key() to record.headers().headers(it.key()).map { h -> h.value() }
            }.toMap()
        )
    }
}

enum class ConsumeFrom(val text: String) {
    Now("Now"),
    Last15min("Last 15 min"),
    LastHour("Last hour"),
    LastDay("Last day"),
    LastWeek("Last week"),
    Beginning("Beginning"),
}

enum class DeserializationFormat {
    String,
    Avro,
}
