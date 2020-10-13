package insulator.viewmodel.main

import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import helper.waitFXThread
import insulator.di.setGlobalCluster
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.SchemaRegistry
import insulator.views.configurations.ClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.TopicView
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.scene.control.TabPane
import javafx.scene.layout.VBox
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewModelTest : FunSpec({

    test("happy path change view") {
        // arrange
        val sut = MainViewModel()
        setGlobalCluster(mockk { every { isSchemaRegistryConfigured() } returns true })
        val newView = ListSchemaView::class
        // act
        sut.runOnFXThread { setContentList(newView) }
        waitFXThread()
        // assert
        val currentView = FX.getComponents()[newView] as ListSchemaView
        sut.contentList.value shouldBe currentView.root
    }

    test("do not show the schema list if schema registry is not configured") {
        // arrange
        val sut = MainViewModel()
        setGlobalCluster(mockk { every { isSchemaRegistryConfigured() } returns false })
        val topicView = sut.contentList.value
        // act
        sut.runOnFXThread { setContentList(ListSchemaView::class) }
        waitFXThread()
        // assert
        sut.contentList.value shouldBe topicView
    }

    test("switch to an unsupported view show an error") {
        // arrange
        val sut = MainViewModel()
        setGlobalCluster(mockk { every { isSchemaRegistryConfigured() } returns true })
        val newView = ClusterView::class
        // act
        sut.runOnFXThread { setContentList(newView) }
        waitFXThread()
        // assert
        sut.error.value shouldNotBe null
    }

    test("showTab doesn't create twice the same tab") {
        // arrange
        val sut = MainViewModel()
        val mockTabPane = TabPane()
        sut.contentTabs = mockTabPane.tabs
        val newView = "sampleView" to mockk<TopicView>(relaxed = true) {
            every { root } returns VBox()
        }
        // act
        sut.runOnFXThread { showTab(newView.first, newView.second) }
        sut.runOnFXThread { showTab(newView.first, newView.second) }
        waitFXThread()
        // assert
        sut.contentTabs.size shouldBe 1
    }

    beforeTest {
        configureFXFramework()
        configureDi(
            AdminApi::class to mockk<AdminApi>(relaxed = true),
            SchemaRegistry::class to mockk<SchemaRegistry>(relaxed = true) {
                every { getAllSubjects() } returns listOf("").right()
            }
        )
    }

    afterTest {
        cleanupFXFramework()
    }
})
