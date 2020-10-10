package insulator.viewmodel.main

import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import helper.waitFXThread
import insulator.di.currentCluster
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.SchemaRegistry
import insulator.views.configurations.ClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewModelTest : FunSpec({

    test("happy path change view") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns true }
        val newView = ListSchemaView::class
        // act
        sut.runOnFXThread { setContent(newView) }
        waitFXThread()
        // assert
        val currentView = FX.getComponents()[newView] as ListSchemaView
        sut.currentViewProperty.value shouldBe currentView
        sut.content.value shouldBe currentView.root
        sut.currentTitle.value shouldBe currentView.title
    }

    test("do not show the schema list if schema registry is not configured") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns false }
        val topicView = sut.currentViewProperty.value
        // act
        sut.runOnFXThread { setContent(ListSchemaView::class) }
        waitFXThread()
        // assert
        sut.currentViewProperty.value shouldBe topicView
    }

    test("toggle sidebar show/hide the sidebar") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns false }
        sut.showSidebar.value shouldBe false
        // act
        sut.runOnFXThread { toggleSidebar() }
        waitFXThread()
        // assert
        sut.showSidebar.value shouldBe true
    }

    test("switch to an unsupported view show an error") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns true }
        val newView = ClusterView::class
        // act
        sut.runOnFXThread { setContent(newView) }
        waitFXThread()
        // assert
        sut.error.value shouldNotBe null
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
