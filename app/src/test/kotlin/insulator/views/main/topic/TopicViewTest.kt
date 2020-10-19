package insulator.views.main.topic

import helper.FxContext
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class TopicViewTest : StringSpec({

    "Render view without exceptions" {
        FxContext().use {
            // arrange
            it.configureFxDi(
                mockk<TopicViewModel>(relaxed = true) {
                    every { consumerViewModel.records } returns FXCollections.observableList(mutableListOf())
                    every { consumerViewModel.filteredRecords } returns SimpleObjectProperty<ObservableList<RecordViewModel>>()
                }
            )
            val sut = TopicView()
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }
})
