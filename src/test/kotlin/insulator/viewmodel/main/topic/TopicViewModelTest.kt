package insulator.viewmodel.main.topic

import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import javafx.scene.input.Clipboard
import tornadofx.* // ktlint-disable no-wildcard-imports

class TopicViewModelTest : StringSpec({

    "delete call the deleteTopic function from lib with the topic name" {
        TopicViewModelTestContext().use {
            it.sut.delete()
        }
    }

    "copy single element happy path" {
        TopicViewModelTestContext().use {
            // arrange
            val mockClipboard = mockk<Clipboard>(relaxed = true)
            unmockkAll()
            mockkStatic(Clipboard::class)
            every { Clipboard.getSystemClipboard() } returns mockClipboard
            it.sut.selectedItem.set(RecordViewModel("key", "value", 1599913230000L))
            // act
            it.sut.copySelectedRecordToClipboard()
            // assert
            verify(exactly = 1) { mockClipboard.putString("2020-09-12 12:20:30\tkey\tvalue") }
        }
    }
})

private class TopicViewModelTestContext : FxContext() {
    val mockkTopic = Topic.empty()
    val mockAdminApi = mockk<AdminApi> {
        coEvery { describeTopic(any()) } returns Topic("Topic name").right()
        coEvery { deleteTopic(any()) } returns null.right()
    }

    val sut = TopicViewModel(mockkTopic, mockAdminApi, mockk(relaxed = true), mockk(relaxed = true))
}
