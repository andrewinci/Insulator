package insulator.views.main.topic

import helper.cleanupFXFramework
import helper.configureFXFramework
import helper.configureScopeDi
import insulator.di.currentCluster
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.main.topic.CreateTopicViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class CreateTopicViewTest : FunSpec({

    test("Render view without exceptions") {
        // arrange
        val sut = CreateTopicView()
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }

    beforeTest {
        currentCluster = Cluster.empty()
        configureFXFramework()
        configureScopeDi(CreateTopicViewModel())
    }

    afterTest {
        cleanupFXFramework()
    }
})
