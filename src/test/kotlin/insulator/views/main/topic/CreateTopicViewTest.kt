package insulator.views.main.topic

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk

class CreateTopicViewTest : StringSpec({

    "Render view without exceptions" {
        FxContext().use {
            // arrange
            val sut = CreateTopicView(mockk())
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }
})
