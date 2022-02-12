package insulator.kafka.local

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.testcontainers.containers.KafkaContainer

class LocalKafkaTest : FreeSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    init {
        "Local kafka test" - {
            val testBootstrapServers = "testBootstrapServers"
            val testSchemaRegistryEndpoint = "testEndpoint"
            val kafkaContainer = mockk<KafkaContainer> {
                every { start() } just runs
                every { waitingFor(any()) } returns mockk()
                every { bootstrapServers } returns testBootstrapServers
            }
            val schemaRegistryContainer = mockk<SchemaRegistryContainer> {
                every { start() } just runs
                every { waitingFor(any()) } returns mockk()
                every { endpoint } returns testSchemaRegistryEndpoint
            }

            "Happy path" {
                // arrange
                val sut = LocalKafka(kafkaContainer, schemaRegistryContainer)
                // act
                val cluster = sut.start()
                // assert
                cluster.shouldBeRight().should {
                    it.endpoint shouldBe testBootstrapServers
                    it.schemaRegistryConfig.endpoint shouldBe testSchemaRegistryEndpoint
                }
            }

            "Error on kafka container startup" {
                // arrange
                val startException = Exception("Unable to start")
                every { kafkaContainer.start() } throws startException
                val sut = LocalKafka(kafkaContainer, schemaRegistryContainer)
                // act
                val cluster = sut.start()
                // assert
                cluster.shouldBeLeft().should {
                    it.shouldBeInstanceOf<LocalKafkaException>()
                    it.message shouldBe startException.message
                }
            }

            "Error waiting kafka container startup" {
                // arrange
                val startException = Exception("Unable to start")
                every { kafkaContainer.waitingFor(any()) } throws startException
                val sut = LocalKafka(kafkaContainer, schemaRegistryContainer)
                // act
                val cluster = sut.start()
                // assert
                cluster.shouldBeLeft().should {
                    it.shouldBeInstanceOf<LocalKafkaException>()
                    it.message shouldBe startException.message
                }
            }

            "Error on schema registry container startup" {
                // arrange
                val startException = Exception("Unable to start")
                every { schemaRegistryContainer.start() } throws startException
                val sut = LocalKafka(kafkaContainer, schemaRegistryContainer)
                // act
                val cluster = sut.start()
                // assert
                cluster.shouldBeLeft().should {
                    it.shouldBeInstanceOf<LocalKafkaException>()
                    it.message shouldBe startException.message
                }
            }

            "Error on waiting schema registry container startup" {
                // arrange
                val startException = Exception("Unable to start")
                every { schemaRegistryContainer.waitingFor(any()) } throws startException
                val sut = LocalKafka(kafkaContainer, schemaRegistryContainer)
                // act
                val cluster = sut.start()
                // assert
                cluster.shouldBeLeft().should {
                    it.shouldBeInstanceOf<LocalKafkaException>()
                    it.message shouldBe startException.message
                }
            }
        }
    }
}
