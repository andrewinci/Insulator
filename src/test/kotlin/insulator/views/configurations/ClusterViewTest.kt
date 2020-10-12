package insulator.views.configurations

import helper.cleanupFXFramework
import helper.configureFXFramework
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class ClusterViewTest : FunSpec({

    test("Render without exceptions") {
        // arrange
        val sut = ClusterView()
        // act
        val root = sut.root
        // assert
        root shouldNotBe null
    }

    beforeTest { configureFXFramework() }
    afterTest { cleanupFXFramework() }
})
