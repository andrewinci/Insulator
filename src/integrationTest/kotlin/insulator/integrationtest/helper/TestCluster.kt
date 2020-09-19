package insulator.integrationtest.helper

import insulator.lib.configuration.model.Cluster
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.Closeable

class TestCluster : Closeable {

    private val kafka = KafkaContainer()

    init {
        kafka.start()
        kafka.waitingFor(Wait.forListeningPort())
    }

    val clusterConfiguration = Cluster.empty().copy(name = "Test local cluster", endpoint = kafka.bootstrapServers)

    fun createTopics(vararg name: String) {
        // create topics
        val admin = AdminClient.create(
            mapOf(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers
            )
                .toProperties()
        )
        admin.createTopics(name.map { NewTopic(it, 3, 1) })
    }

    override fun close() = kafka.close()
}
