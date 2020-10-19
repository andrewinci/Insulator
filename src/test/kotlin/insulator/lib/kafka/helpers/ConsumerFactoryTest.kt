package insulator.lib.kafka.helpers

import insulator.di.components.ClusterComponent
import insulator.lib.kafka.DeserializationFormat
import io.kotest.core.spec.style.FreeSpec
import io.mockk.mockk
import io.mockk.verify

class ConsumerFactoryTest : FreeSpec({

    "test cache consumer" - {
        // arrange
        val clusterComponent = mockk<ClusterComponent>(relaxed = true)
        val sut = ConsumerFactory(clusterComponent)

        "avro consumer"{
            // act
            sut.build(DeserializationFormat.Avro)
            sut.build(DeserializationFormat.Avro)
            // assert
            verify(exactly = 1) { clusterComponent.avroConsumer() }
        }

        "string consumer" {
            // act
            sut.build(DeserializationFormat.String)
            sut.build(DeserializationFormat.String)
            // assert
            verify(exactly = 1) { clusterComponent.stringConsumer() }
        }
    }

})
