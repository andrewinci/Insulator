package insulator.views.main.topic

import insulator.viewmodel.RecordViewModel
import insulator.viewmodel.TopicViewModel
import insulator.views.common.SizedView
import insulator.views.common.card
import tornadofx.*


class TopicView(private val viewModel: TopicViewModel) : SizedView(viewModel.nameProperty.value, 800.0, 800.0) {

    override val root = card(viewModel.nameProperty.value) {
        button("Consume") { action { viewModel.consume() } }

        tableview(viewModel.records) {
            column("Key", RecordViewModel::keyProperty).minWidth(200.0)
            column("Value", RecordViewModel::valueProperty)
            prefHeight = 600.0 //todo: remove hardcoded and retrieve
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            viewModel.stopConsumer()
        }
        super.onDock()
    }


}