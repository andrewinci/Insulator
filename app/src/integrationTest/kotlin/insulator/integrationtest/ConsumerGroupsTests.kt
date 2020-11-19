package insulator.integrationtest

import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.Node
import javafx.scene.control.Label
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import tornadofx.CssRule
import java.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConsumerGroupsTests : FreeSpec({

    "Test consumer groups" {
        IntegrationTestFixture().use { fixture ->
            val clusterName = "Test cluster"
            val groupIdName = "test-consumer-group-id"
            val testTopic = "topic-consumer-topic"

            fixture.startAppWithKafkaCuster(clusterName, false)

            // Create topic and send a message
            fixture.createTopic(testTopic)
            fixture.stringProducer.send(testTopic, "k", "v")
            // Create a consumer group
            with(
                KafkaConsumer<String, String>(
                    mapOf(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to fixture.currentKafkaCluster.endpoint,
                        ConsumerConfig.GROUP_ID_CONFIG to groupIdName,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                    )
                )
            ) {
                subscribe(listOf(testTopic))
                poll(Duration.ofMillis(300))
                commitSync()
                close()
            }

            // open main view
            selectCluster(fixture.currentKafkaCluster)
            val mainView = waitWindowWithTitle("Insulator")
            // select consumer group tab
            mainView.lookupFirst<Node>(CssRule.id("sidebar-item-consumer-group")).click()
            // the consumer group is shown in the list of consumers
            mainView.lookupFirst<Label>(CssRule.id("consumer-$groupIdName")).text shouldBe groupIdName
        }
    }
})
