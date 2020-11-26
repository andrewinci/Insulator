package insulator.integrationtest

import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.doubleClick
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.kafka.factories.kafkaConfig
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TreeView
import kotlinx.coroutines.delay
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import tornadofx.CssRule
import tornadofx.expandAll
import java.io.Closeable
import java.time.Duration
import java.util.Properties
import kotlin.concurrent.thread
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConsumerGroupsTests : FreeSpec({

    "Test consumer groups" {
        IntegrationTestFixture().use { fixture ->
            val clusterName = "Test cluster"
            val groupIdName = "test-consumer-group-id"
            val testTopic = "topic-consumer-topic"
            val consumerNumber = 3

            fixture.startAppWithKafkaCuster(clusterName, false)

            // Create topic and send a message
            fixture.createTopic(testTopic)
            fixture.stringProducer.send(testTopic, "k", "v")
            // Create a consumer group
            val consumerConfig = kafkaConfig(fixture.currentKafkaCluster).apply {
                put(ConsumerConfig.GROUP_ID_CONFIG, groupIdName)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            }
            val consumers = (1..consumerNumber).map { testConsumer(consumerConfig, testTopic) }

            // open main view
            selectCluster(fixture.currentKafkaCluster)
            val mainView = waitWindowWithTitle("Insulator")
            // select consumer group tab
            mainView.lookupFirst<Node>(CssRule.id("sidebar-item-consumer-group")).click()
            // the consumer group is shown in the list of consumers
            mainView.lookupFirst<Label>(CssRule.id("consumer-$groupIdName")).text shouldBe groupIdName
            delay(10_000) // wait a bit for re-balancing
            // double click on the consumer group name shows the the tree view with the lag
            mainView.lookupFirst<Label>(CssRule.id("consumer-$groupIdName")).doubleClick()
            with(mainView.lookupFirst<TreeView<String>>(".tree-view")) {
                root.value shouldBe "Consumers"
                root.children.size shouldBe consumerNumber
                root.expandAll()
            }
            screenShoot("consumer-group-view")
            consumers.forEach { it.close() }
        }
    }
})

fun testConsumer(consumerConfig: Properties, topicName: String) = object : Closeable {

    private var isRunning = true

    private val consumerThread = with(KafkaConsumer<String, String>(consumerConfig)) {
        subscribe(listOf(topicName))
        thread(start = true) {
            while (isRunning) {
                poll(Duration.ofMillis(200))
                commitSync()
            }
            this.close()
        }
    }

    override fun close() {
        isRunning = false
        consumerThread.join()
    }
}
