package insulator.kafka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.time.Duration
import java.util.*

class Consumer(private val consumer: Consumer<Any, Any>) {
    private lateinit var job: Job
    private var running = false
    private val callbackList = LinkedList<(String, String) -> Unit>()

    fun setCallback(cb: (String, String) -> Unit) = callbackList.add(cb)

    fun start(vararg topic: String) {
        if (isRunning()) throw Throwable("Consumer already running")
        consumer.subscribe(topic.toList())
        running = true
        loop()
    }

    fun isRunning() = running

    fun stop() {
        running = false
        callbackList.clear()
        //todo: job.join()
    }

    private fun loop() {
        job = GlobalScope.launch {
            while (running) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (records.isEmpty) continue
                records.toList()
                        .map { parse(it) }
                        .forEach { (k, v) -> callbackList.forEach { it(k, v) } }
            }
        }
    }

    private fun parse(record: ConsumerRecord<Any, Any>): Pair<String, String> {
        //todo: parse this thing
        return Pair(record.key().toString(), record.value().toString())
    }

}