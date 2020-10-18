package insulator.viewmodel.main.topic

import arrow.core.Tuple3
import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import javafx.collections.FXCollections
import javafx.scene.input.Clipboard
import kotlinx.coroutines.delay
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.CompletableFuture

class TopicViewModelTest : StringSpec({

    "clear remove all records from the list" {
        TopicViewModelTestContext().use {
            // arrange
            it.sut.records.add(mockk())
            // act
            it.sut.clear()
            // assert
            it.sut.records.size shouldBe 0
        }
    }

    "stop an already stopped consumer is ignored" {
        TopicViewModelTestContext().use {
            it.sut.stop()
        }
    }

    "delete call the deleteTopic function from lib with the topic name" {
        TopicViewModelTestContext().use {
            it.sut.delete()
        }
    }

    "consume" {
        TopicViewModelTestContext().use {
            // arrange
            // act
            it.sut.consume()
            delay(1000)
            // assert
            it.sut.records.count() shouldBe 3
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

    "copy all happy path" {
        TopicViewModelTestContext().use {
            // arrange
            val mockClipboard = mockk<Clipboard>(relaxed = true)
            unmockkAll()
            mockkStatic(Clipboard::class)
            every { Clipboard.getSystemClipboard() } returns mockClipboard
            it.sut.filteredRecords.set(
                FXCollections.observableList(
                    listOf(
                        RecordViewModel("key1", "value1", 1599913230000L),
                        RecordViewModel("key2", "value2", 1599913230000L)
                    )
                )
            )
            // act
            it.sut.copyAllRecordsToClipboard()
            // assert
            verify(exactly = 1) { mockClipboard.putString("2020-09-12 12:20:30\tkey1\tvalue1\n2020-09-12 12:20:30\tkey2\tvalue2") }
        }
    }
})

private class TopicViewModelTestContext : FxContext() {
    val mockkTopic = Topic.empty()
    val mockAdminApi = mockk<AdminApi> {
        every { describeTopic(any()) } returns CompletableFuture.completedFuture(Topic("Topic name").right())
        every { deleteTopic(any()) } returns CompletableFuture.completedFuture(null.right())
    }
    val mockConsumer = mockk<Consumer> {
        every { start(any(), any(), any(), any()) } answers {
            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
        }
        every { stop() } just runs
        every { isRunning() } returns false
    }

    val sut = TopicViewModel(mockkTopic, mockAdminApi, mockConsumer, mockk(relaxed = true))
}
