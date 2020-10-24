package insulator.views.main

import helper.FxContext
import insulator.kafka.model.Cluster
import insulator.viewmodel.main.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent

class MainViewTest : StringSpec({

    "view renders correctly" {
        FxContext().use {
            val mainViewModel = mockk<MainViewModel> {
                every { contentList } returns SimpleObjectProperty<Parent>()
            }
            MainView(mainViewModel, mockk(relaxed = true), Cluster.empty())
        }
    }
})
