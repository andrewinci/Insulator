package insulator.kafka.local

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import insulator.helper.runCatchingE
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait

class LocalKafkaException(message: String?) : Exception(message)

class LocalKafka {

    private val kafka = KafkaContainer()
    private val schemaRegistry = SchemaRegistryContainer().withKafka(kafka)

    suspend fun start() = suspendCancellableCoroutine<Either<LocalKafkaException, Cluster>> { continuation ->
        GlobalScope.launch {
            continuation.resumeWith(Result.success(startLocalCluster()))
        }
    }

    private fun startLocalCluster(): Either<LocalKafkaException, Cluster> {
        kafka.runCatchingE { start() }
            .map { kafka.waitingFor(Wait.forListeningPort()) }
            .mapLeft { return LocalKafkaException(it.message).left() }
        schemaRegistry.runCatchingE { start() }
            .map { schemaRegistry.waitingFor(Wait.forListeningPort()) }
            .mapLeft { return LocalKafkaException(it.message).left() }
        return Cluster.empty().copy(
            name = "Local Cluster",
            endpoint = kafka.bootstrapServers,
            schemaRegistryConfig = SchemaRegistryConfiguration(schemaRegistry.endpoint)
        ).right()
    }
}
