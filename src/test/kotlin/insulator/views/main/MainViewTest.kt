package insulator.views.main

import helper.cleanupFXFramework
import helper.configureFXFramework
import helper.configureScopeDi
import insulator.di.currentCluster
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.main.MainViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.layout.VBox
import tornadofx.FX

class MainViewTest : FunSpec({

    test("Render view without exceptions") {
        // arrange
        val sut = MainView()
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }

    test("Default main view len should be 650") {
        // arrange
        val sut = MainView()
        // act
        sut.onDock()
        // assert
        sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH
    }

    test("Default main view + a tab len should be 1400") {
        // arrange
        val sut = MainView()
        val vm = FX.find<MainViewModel>()
        // act
        sut.onDock()
        vm.contentTabs.add(Tab("1", VBox()))
        // assert
        sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH + CONTENT_WIDTH
    }

    test("Add and remove a tab leave the width unchanged") {
        // arrange
        val sut = MainView()
        val vm = FX.find<MainViewModel>()
        // act
        sut.onDock()
        vm.contentTabs.add(Tab("1", VBox()))
        vm.contentTabs.removeLast()
        // assert
        sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH
    }

    beforeTest {
        currentCluster = Cluster.empty()
        configureFXFramework()
        configureScopeDi(
            mockk<MainViewModel>(relaxed = true) {
                every { contentTabs } returns FXCollections.observableArrayList()
                every { contentList } returns SimpleObjectProperty<Parent>()
                every { error } returns SimpleObjectProperty<Throwable?>(null)
            }
        )
    }

    afterTest {
        cleanupFXFramework()
    }
})
