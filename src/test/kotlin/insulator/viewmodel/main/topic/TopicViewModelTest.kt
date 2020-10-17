package insulator.viewmodel.main.topic

import arrow.core.Tuple3
import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.FreeSpec
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

class TopicViewModelTest : FreeSpec({

    val topicName = "topic-name"

    "clear remove all records from the list" {
        TestContext().use {
            // arrange
            val sut = TopicViewModel(topicName)
            sut.records.add(mockk())
            // act
            sut.clearRecords()
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

    "subtitle stats are correct" - {
        TestContext().use {
            // arrange
            val messageCount = 100L
            it.addToDI(
                AdminApi::class to mockk<AdminApi> {
                    coEvery { describeTopic(any()) } returns Topic("Topic name", messageCount = messageCount, isCompacted = true, partitionCount = 3).right()
                    coEvery { deleteTopic(any()) } returns null.right()
                }
            )
            val sut = TopicViewModel("test-topic-name")
//            sut.configureFilteredRecords(SimpleObjectProperty(
//                Comparator { o1, o2 -> o1.timestampProperty.value.compareTo(o2.timestampProperty.value) }
//            ))
            "happy path" {
                // act
                sut.records.addAll((1..10).map { RecordViewModel("k", "v", 1) })
                // assert
                sut.subtitleProperty.value shouldBe
                    "Message count: 10/$messageCount - " +
                    "Is internal: false - " +
                    "Partitions count: 3 - " +
                    "Compacted: true"
            }

            "subtitle with filtered elements" {
                // act
                sut.searchItem.value = "123"
                sut.records.addAll((1..10).map { RecordViewModel("k", "v", 1) })
                sut.records.add(RecordViewModel("123", "v", 1))
                // assert
                sut.subtitleProperty.value shouldBe
                    "Message count: 1/$messageCount - " +
                    "Is internal: false - " +
                    "Partitions count: 3 - " +
                    "Compacted: true"
            }
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
                coEvery { start(any(), any(), any(), any()) } answers {
                    arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
                    arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
                    arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
                }
                coEvery { stop() } just runs
                every { isRunning() } returns false
            }
        )
    }
}
