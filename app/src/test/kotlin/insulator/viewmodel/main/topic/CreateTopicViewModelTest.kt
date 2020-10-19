package insulator.viewmodel.main.topic

import arrow.core.right
import helper.FxContext
import insulator.lib.helpers.dispatch
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class CreateTopicViewModelTest : StringSpec({

    "Happy path" {
        FxContext().use {
            // arrange
            val mockAdminApi = mockk<AdminApi> {
                coEvery { createTopics(any()) } returns Unit.right()
            }
            it.addToDI(AdminApi::class to mockAdminApi)
            val sampleTopic = Topic(name = "sampleTopic")
            val sut = CreateTopicViewModel(CreateTopicModel(sampleTopic))
            // act
            sut.save()
            // assert
            coVerify(exactly = 1) { mockAdminApi.createTopics(sampleTopic) }
        }
    }

    "Save all info" {
        FxContext().use {
            // arrange
            val captureSlot = CapturingSlot<Topic>()
            val mockAdminApi = mockk<AdminApi> {
                coEvery { createTopics(capture(captureSlot)) } returns Unit.right()
            }
            it.addToDI(AdminApi::class to mockAdminApi)
            val sut = CreateTopicViewModel()

            // act
            sut.dispatch {
                with(sut) {
                    nameProperty.set("name")
                    partitionCountProperty.set(3)
                    replicationFactorProperty.set(2)
                    isCompactedProperty.set(true)
                }
                commit()
                save()
            }
            it.waitFXThread()

            // assert
            coVerify(exactly = 1) { mockAdminApi.createTopics(any()) }
            with(captureSlot.captured) {
                name shouldBe "name"
                partitionCount shouldBe 3
                replicationFactor shouldBe 2
                isCompacted shouldBe true
            }
        }
    }
})
