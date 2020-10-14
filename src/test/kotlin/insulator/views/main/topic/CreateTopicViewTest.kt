package insulator.views.main.topic

import helper.FxContext
import insulator.viewmodel.main.topic.CreateTopicViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class CreateTopicViewTest : StringSpec({

    "Render view without exceptions" {
        FxContext().use {

            it.configureFxDi(CreateTopicViewModel())
            // arrange
            val sut = CreateTopicView()
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }
})
