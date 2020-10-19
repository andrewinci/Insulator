package insulator.views.main

import helper.FxContext
import insulator.viewmodel.main.MainViewModel
import insulator.views.main.topic.ListTopicView
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.layout.VBox

class MainViewTest : StringSpec({

    "Render view without exceptions" {
        TestContext().use {
            // arrange
            val sut = MainView(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }

    "Check default main view len" {
        TestContext().use {
            // arrange
            val sut = MainView(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
            // act
            sut.onDock()
            // assert
            sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH
        }
    }

    //todo: move to tab viewmodel unittest
//    "Check default main view + a tab len should" {
//        TestContext().use {
//            // arrange
//            val sut = MainView(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
//            // act
//            sut.onDock()
//            it.mainViewModel.contentTabs.add(Tab("1", VBox()))
//            // assert
//            sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH + CONTENT_WIDTH
//        }
//    }


//    "Add and remove a tab leave the width unchanged" {
//        TestContext().use {
//            // arrange
//            val sut = MainView(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
//            // act
//            sut.onDock()
//            it.mainViewModel.contentTabs.add(Tab("1", VBox()))
//            it.mainViewModel.contentTabs.removeLast()
//            // assert
//            sut.currentStage?.minWidth shouldBe SIDEBAR_WIDTH + CONTENT_LIST_WIDTH
//        }
//    }
})

private class TestContext : FxContext() {
//    val mainViewModel = mockk<MainViewModel>(relaxed = true) {
//        every { contentTabs } returns FXCollections.observableArrayList()
//        every { contentList } returns SimpleObjectProperty<Parent>()
//        every { error } returns SimpleObjectProperty()
//    }
//    val listTopicView = mockk<ListTopicView>()
//
//    init {
//        configureFxDi(mainViewModel, listTopicView)
//    }
}
