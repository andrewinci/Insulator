package insulator.lib.kafka

import arrow.core.Tuple3
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import org.koin.ext.scope
import java.io.Closeable
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Consumer(private val cluster: Cluster, private val converter: AvroToJsonConverter) : KoinComponent, Closeable {

    private var threadLoop: Thread? = null
    private var running = false

    suspend fun start(topic: String, from: ConsumeFrom, valueFormat: DeserializationFormat, callback: (List<Tuple3<String?, String, Long>>) -> Unit) =
        suspendCoroutine<Unit> { continuation ->
            GlobalScope.launch {
                if (isRunning()) throw Throwable("Consumer already running")
                val consumer: Consumer<Any, Any> = when (valueFormat) {
                    DeserializationFormat.Avro -> cluster.scope.get(named("avroConsumer"))
                    DeserializationFormat.String -> cluster.scope.get()
                }
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

    private fun loop(consumer: Consumer<Any, Any>, callback: (List<Tuple3<String?, String, Long>>) -> Unit) {
        threadLoop = thread {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                callback(records.toList().map { parse(it) })
            }
            consumer.close()
        }
    }

    private fun initializeConsumer(consumer: Consumer<Any, Any>, topic: String, from: ConsumeFrom) {
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)

        when (from) {
            ConsumeFrom.Now -> consumer.seekToEnd(partitions)
            ConsumeFrom.LastHour -> {
                val time = Instant.now().minus(Duration.ofMinutes(30)).toEpochMilli()
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
