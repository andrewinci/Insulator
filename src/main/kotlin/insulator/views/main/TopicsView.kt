package insulator.views.main

import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.flatMap
import insulator.viewmodel.TopicsViewModel
import insulator.views.common.title
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.koin.core.KoinComponent
import org.koin.core.inject
import tornadofx.*

class TopicsView : VBox(), KoinComponent {
    private val viewModel: TopicsViewModel by inject()

    init {
        alignment = Pos.TOP_CENTER
        spacing = 15.0
        padding = Insets(5.0)
        anchorpaneConstraints { topAnchor = 0;rightAnchor = 0;bottomAnchor = 0;leftAnchor = 0 }
        title("Topics", Color.ORANGERED)
        viewModel.topics.map {
            listview(it) {
                cellFormat { topic ->
                    graphic = label(topic.name)
                }
            }
        }.handleError {
            alert(Alert.AlertType.ERROR, "Unable to load the list of topics", it.message)
            throw it
        }

    }
}