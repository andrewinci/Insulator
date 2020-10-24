package insulator.views.configurations

import helper.FxContext
import insulator.viewmodel.configurations.ClusterViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk

class ClusterViewTest : StringSpec({

    "Render without exceptions" {
        FxContext().use {
            // arrange
            val sut = ClusterView(ClusterViewModel(mockk(relaxed = true), mockk()))
            // act
            val root = sut.root
            // assert
            root shouldNotBe null
        }
    }
})
