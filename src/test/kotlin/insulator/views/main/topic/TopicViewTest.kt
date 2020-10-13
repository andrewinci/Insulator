package insulator.views.main.topic

import helper.cleanupFXFramework
import helper.configureFXFramework
import helper.configureScopeDi
import insulator.di.setGlobalCluster
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class TopicViewTest : FunSpec({

    test("Render view without exceptions") {
        // arrange
        val sut = TopicView()
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }

    beforeTest {
        setGlobalCluster(Cluster.empty())
        configureFXFramework()
        configureScopeDi(
            mockk<TopicViewModel>(relaxed = true) {
                every { records } returns FXCollections.observableList(mutableListOf())
                every { filteredRecords } returns SimpleObjectProperty<ObservableList<RecordViewModel>>()
            }
        )
    }

    afterTest {
        cleanupFXFramework()
    }
})
