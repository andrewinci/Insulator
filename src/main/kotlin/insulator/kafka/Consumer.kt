package insulator.kafka

import arrow.core.Tuple3
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.*

class Consumer(private val consumer: Consumer<Any, Any>) {
    private lateinit var job: Job
    private var running = false
    private val callbackList = LinkedList<(String, String, Long) -> Unit>()

    fun setCallback(cb: (String, String, Long) -> Unit) = callbackList.add(cb)

    fun start(topic: String, from: ConsumeFrom) {
        if (isRunning()) throw Throwable("Consumer already running")
        val partitions = consumer.partitionsFor(topic).map { TopicPartition(topic, it.partition()) }
        consumer.assign(partitions)
        seek(consumer, partitions, from)
        running = true
        loop()
    }

    fun isRunning() = running

    fun stop() {
        running = false
        callbackList.clear()
        //todo: lock to avoid start while stopping
        //todo: job.join()
    }

    private fun loop() {
        job = GlobalScope.launch {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                records.toList()
                        .map { parse(it) }
                        .forEach { (k, v, t) -> callbackList.forEach { it(k, v, t) } }
            }
        }
    }

    private fun seek(consumer: Consumer<Any, Any>, partitions: List<TopicPartition>, from: ConsumeFrom) {
        when (from) {
            ConsumeFrom.Now -> consumer.seekToEnd(partitions)
            ConsumeFrom.Beginning -> consumer.seekToBeginning(partitions)
        }
    }

    private fun parse(record: ConsumerRecord<Any, Any>): Tuple3<String, String, Long> {
        //todo: parse this thing
        return Tuple3(record.key().toString(), record.value().toString(), record.timestamp())
    }
}

enum class ConsumeFrom {
    Now,
    Beginning
}