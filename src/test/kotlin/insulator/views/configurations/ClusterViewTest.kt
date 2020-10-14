package insulator.views.configurations

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class ClusterViewTest : StringSpec({

    "Render without exceptions" {
        FxContext().use {
            // arrange
            val sut = ClusterView()
            // act
            val root = sut.root
            // assert
            root shouldNotBe null
        }
    }
})
