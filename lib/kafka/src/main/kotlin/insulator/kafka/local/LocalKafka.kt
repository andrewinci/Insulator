package insulator.kafka.local

import arrow.core.Either
import arrow.core.computations.either
import insulator.helper.runCatchingE
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait

class LocalKafkaException(throwable: Throwable) : Exception(throwable.message)

class LocalKafka(
    val kafka: KafkaContainer = KafkaContainer(),
    val schemaRegistry: SchemaRegistryContainer = SchemaRegistryContainer().withKafka(kafka)
) {

    suspend fun start() = suspendCancellableCoroutine<Either<LocalKafkaException, Cluster>> { continuation ->
        GlobalScope.launch {
            continuation.resumeWith(Result.success(startLocalCluster()))
        }
    }

    private suspend fun startLocalCluster() = either<LocalKafkaException, Cluster> {
        listOf(kafka, schemaRegistry).forEach { container ->
            !container.runCatchingE { start() }
                .map { container.waitingFor(Wait.forListeningPort()) }
                .mapLeft { LocalKafkaException(it) }
        }

        Cluster.empty().copy(
            name = "Local Cluster",
            endpoint = kafka.bootstrapServers,
            schemaRegistryConfig = SchemaRegistryConfiguration(schemaRegistry.endpoint)
        )
    }
}
