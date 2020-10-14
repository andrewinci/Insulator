package insulator.viewmodel.main

import arrow.core.right
import helper.FxContext
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.SchemaRegistry
import insulator.views.configurations.ClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.TopicView
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.scene.control.TabPane
import javafx.scene.layout.VBox
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewModelTest : StringSpec({

    "happy path change view" {
        FxContext().use {
            // arrange
            it.setup()
            val sut = MainViewModel()
            it.addToDI(Cluster::class to it.cluster.copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample endpoint")))
            val newView = ListSchemaView::class
            // act
            sut.runOnFXThread { setContentList(newView) }
            it.waitFXThread()
            // assert
            val currentView = find<ListSchemaView>()
            sut.contentList.value shouldBe currentView.root
        }
    }

    "do not show the schema list if schema registry is not configured" {
        FxContext().use {
            // arrange
            it.setup()
            val sut = MainViewModel()
            val topicView = sut.contentList.value
            // act
            sut.runOnFXThread { setContentList(ListSchemaView::class) }
            it.waitFXThread()
            // assert
            sut.contentList.value shouldBe topicView
        }
    }

    "switch to an unsupported view show an error" {
        FxContext().use {
            // arrange
            it.setup()
            val sut = MainViewModel()
            it.addToDI(Cluster::class to it.cluster.copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample endpoint")))
            val newView = ClusterView::class
            // act
            sut.runOnFXThread { setContentList(newView) }
            it.waitFXThread()
            // assert
            sut.error.value shouldNotBe null
        }
    }

    "showTab doesn't create twice the same tab" {
        FxContext().use {
            // arrange
            it.setup()
            val sut = MainViewModel()
            val mockTabPane = TabPane()
            sut.contentTabs = mockTabPane.tabs
            val newView = "sampleView" to mockk<TopicView>(relaxed = true) {
                every { root } returns VBox()
            }
            // act
            sut.runOnFXThread { showTab(newView.first, newView.second) }
            sut.runOnFXThread { showTab(newView.first, newView.second) }
            it.waitFXThread()
            // assert
            sut.contentTabs.size shouldBe 1
        }
    }
})

private fun FxContext.setup() = this.addToDI(
    AdminApi::class to mockk<AdminApi>(relaxed = true),
    SchemaRegistry::class to mockk<SchemaRegistry>(relaxed = true) {
        every { getAllSubjects() } returns listOf("").right()
    }
)
