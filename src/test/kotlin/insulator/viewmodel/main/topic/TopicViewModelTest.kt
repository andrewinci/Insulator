package insulator.viewmodel.main.topic

import arrow.core.Tuple3
import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
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

    val topicName = "topic-name"

    "clear remove all records from the list" {
        TestContext().use {
            // arrange
            val sut = TopicViewModel(topicName)
            sut.records.add(mockk())
            // act
            sut.clear()
            // assert
            sut.records.size shouldBe 0
        }
    }

    "stop an already stopped consumer is ignored" {
        TestContext().use {
            // arrange
            val sut = TopicViewModel(topicName)
            // act
            sut.stop()
        }
    }

    "delete call the deleteTopic function from lib with the topic name" {
        TestContext().use {
            // arrange
            val sut = TopicViewModel(topicName)
            // act
            sut.delete()
            // assert
        }
    }

    "consume" {
        TestContext().use {
            // arrange
            val sut = TopicViewModel(topicName)
            // act
            sut.consume()
            delay(1000)
            // assert
            sut.records.count() shouldBe 3
        }
    }

    "copy single element happy path" {
        TestContext().use {
            // arrange
            val mockClipboard = mockk<Clipboard>(relaxed = true)
            unmockkAll()
            mockkStatic(Clipboard::class)
            every { Clipboard.getSystemClipboard() } returns mockClipboard
            val sut = TopicViewModel(topicName)
            sut.selectedItem.set(RecordViewModel("key", "value", 1599913230000L))
            // act
            sut.copySelectedRecordToClipboard()
            // assert
            verify(exactly = 1) { mockClipboard.putString("2020-09-12 12:20:30\tkey\tvalue") }
        }
    }

    "copy all happy path" {
        TestContext().use {
            // arrange
            val mockClipboard = mockk<Clipboard>(relaxed = true)
            unmockkAll()
            mockkStatic(Clipboard::class)
            every { Clipboard.getSystemClipboard() } returns mockClipboard
            val sut = TopicViewModel(topicName)
            sut.filteredRecords.set(
                FXCollections.observableList(
                    listOf(
                        RecordViewModel("key1", "value1", 1599913230000L),
                        RecordViewModel("key2", "value2", 1599913230000L)
                    )
                )
            )
            // act
            sut.copyAllRecordsToClipboard()
            // assert
            verify(exactly = 1) { mockClipboard.putString("2020-09-12 12:20:30\tkey1\tvalue1\n2020-09-12 12:20:30\tkey2\tvalue2") }
        }
    }
})

private class TestContext : FxContext() {
    init {
        addToDI(
            AdminApi::class to mockk<AdminApi> {
                coEvery { describeTopic(any()) } returns Topic("Topic name").right()
                coEvery { deleteTopic(any()) } returns null.right()
            },
            Consumer::class to mockk<Consumer> {
                every { start(any(), any(), any(), any()) } answers {
                    lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                    lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                    lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                }
                every { stop() } just runs
                every { isRunning() } returns false
            }
        )
    }
}
