package insulator.viewmodel.main

import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import insulator.di.currentCluster
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.SchemaRegistry
import insulator.views.main.schemaregistry.ListSchemaView
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class MainViewModelTest : FunSpec({

    test("happy path change view") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns true }
        val topicView = sut.currentViewProperty.value
        sut.runOnFXThread {
            // act
            setCurrentView(ListSchemaView::class.java)
            // assert
            runOnFXThread { sut.currentViewProperty.value shouldNotBe topicView }
        }
    }

    test("do not show the schema list if schema registry is not configured") {
        // arrange
        val sut = MainViewModel()
        currentCluster = mockk { every { isSchemaRegistryConfigured() } returns false }
        val topicView = sut.currentViewProperty.value
        sut.runOnFXThread {
            // act
            setCurrentView(ListSchemaView::class.java)
            // assert
            sut.currentViewProperty.value shouldBe topicView
        }
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
