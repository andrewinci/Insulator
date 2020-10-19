package insulator.viewmodel.main

import arrow.core.right
import helper.FxContext
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.SchemaRegistry
import insulator.views.configurations.ClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewModelTest : StringSpec({

    "happy path change view" {
        FxContext().use {
            // arrange
            val cluster = it.cluster.copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample endpoint"))
            val sut = MainViewModel(cluster, mockk(), mockk())
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
            val sut = MainViewModel(it.cluster, mockk(), mockk())
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
            val cluster = it.cluster.copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample endpoint"))
            val sut = MainViewModel(cluster, mockk(), mockk())
            val newView = ClusterView::class
            // act
            sut.runOnFXThread { setContentList(newView) }
            it.waitFXThread()
            // assert
            sut.error.value shouldNotBe null
        }
    }

    //todo: move to tabViewModel tests
//    "showTab doesn't create twice the same tab" {
//        MainViewModelTestContext().use {
//            // arrange
//            val sut = MainViewModel(it.mockCluster, mockk(), mockk())
//            val mockTabPane = TabPane()
//            sut.contentTabs = mockTabPane.tabs
//            val newView = "sampleView" to mockk<TopicView>(relaxed = true) {
//                every { root } returns VBox()
//            }
//            // act
//            sut.runOnFXThread { showTab(newView.first, newView.second) }
//            sut.runOnFXThread { showTab(newView.first, newView.second) }
//            it.waitFXThread()
//            // assert
//            sut.contentTabs.size shouldBe 1
//        }
//    }
})

class MainViewModelTestContext : FxContext() {
    val mockAdminAny = mockk<AdminApi>(relaxed = true)
    val mockSchemaRegistry = mockk<SchemaRegistry>(relaxed = true) {
        every { getAllSubjects() } returns listOf("").right()
    }
}