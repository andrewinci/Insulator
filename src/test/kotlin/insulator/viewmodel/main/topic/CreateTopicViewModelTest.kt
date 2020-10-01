package insulator.viewmodel.main.topic

import arrow.core.right
import helper.cleanupDi
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import helper.waitFXThread
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.CompletableFuture

class CreateTopicViewModelTest : FunSpec({

    test("Happy path") {
        // arrange
        val mockAdminApi = mockk<AdminApi> {
            every { createTopics(any()) } returns CompletableFuture.completedFuture(Unit.right())
        }
        configureDi(AdminApi::class to mockAdminApi)
        val sampleTopic = Topic(name = "sampleTopic")
        val sut = CreateTopicViewModel(CreateTopicModel(sampleTopic))
        // act
        sut.save()
        // assert
        verify(exactly = 1) { mockAdminApi.createTopics(sampleTopic) }
    }

    test("Save all info") {
        // arrange
        val captureSlot = CapturingSlot<Topic>()
        val mockAdminApi = mockk<AdminApi> {
            every { createTopics(capture(captureSlot)) } returns CompletableFuture.completedFuture(Unit.right())
        }
        configureDi(AdminApi::class to mockAdminApi)
        val sut = CreateTopicViewModel()

        // act
        sut.runOnFXThread {
            with(sut) {
                nameProperty.set("name")
                partitionCountProperty.set(3)
                replicationFactorProperty.set(2)
                isCompactedProperty.set(true)
            }
            commit()
            save()
        }
        waitFXThread()

        // assert
        verify(exactly = 1) { mockAdminApi.createTopics(any()) }
        with(captureSlot.captured) {
            name shouldBe "name"
            partitionCount shouldBe 3
            replicationFactor shouldBe 2
            isCompacted shouldBe true
        }
    }

    beforeTest { configureFXFramework() }
    afterTest {
        cleanupDi()
        cleanupFXFramework()
    }
})
