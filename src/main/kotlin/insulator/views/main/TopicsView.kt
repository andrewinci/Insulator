package insulator.views.main

import insulator.viewmodel.TopicsViewModel
import insulator.views.common.title
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.koin.core.KoinComponent
import org.koin.core.inject
import tornadofx.*

class TopicsView : VBox(), KoinComponent {
    private val viewModel: TopicsViewModel by inject()

    init {
        title("Topics", Color.ORANGERED)
        listview(viewModel.topicsProperty) {
            cellFormat { topic ->
                graphic = label(topic.name)
            }
        }

        alignment = Pos.TOP_CENTER
        spacing = 15.0
        paddingAll = 5.0
        anchorpaneConstraints { topAnchor = 0;rightAnchor = 0;bottomAnchor = 0;leftAnchor = 0 }
    }
}
