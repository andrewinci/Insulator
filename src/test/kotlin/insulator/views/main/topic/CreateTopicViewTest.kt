package insulator.views.main.topic

import helper.FxContext
import insulator.viewmodel.main.topic.CreateTopicViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk

class CreateTopicViewTest : StringSpec({

    "Render view without exceptions" {
        FxContext().use {
            // arrange
            val mockViewModel = CreateTopicViewModel(mockk(relaxed = true), mockk(relaxed = true))
            val sut = CreateTopicView(mockViewModel)
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }
})
