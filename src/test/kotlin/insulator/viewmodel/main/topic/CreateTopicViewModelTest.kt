package insulator.viewmodel.main.topic

import arrow.core.right
import helper.cleanupDi
import helper.configureDi
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.CompletableFuture

class CreateTopicViewModelTest : FunSpec({

    test("Save all info") {
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
        cleanupDi()
    }
})
