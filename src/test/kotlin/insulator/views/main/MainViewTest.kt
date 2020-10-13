package insulator.views.main

import helper.FxContext
import insulator.viewmodel.main.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import javafx.scene.control.Tab
import javafx.scene.layout.VBox
import tornadofx.FX

class MainViewTest : StringSpec({

    "Render view without exceptions" {
        // arrange
        val sut = MainView()
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }

    "Default main view len should be 650" {
        // arrange
        val sut = MainView()
        // act
        sut.onDock()
        // assert
        sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH
    }

    "Default main view + a tab len should be 1400" {
        FxContext().use {
            // arrange
            val sut = MainView()
            val vm = FX.find<MainViewModel>()
            // act
            sut.onDock()
            vm.contentTabs.add(Tab("1", VBox()))
            // assert
            sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH + CONTENT_WIDTH
        }
    }

    "Add and remove a tab leave the width unchanged" {
        FxContext().use {
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
    }
})
