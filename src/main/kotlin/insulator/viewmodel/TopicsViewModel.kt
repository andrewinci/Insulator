package insulator.viewmodel

import arrow.core.Either
import arrow.core.right
import insulator.model.Topic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class TopicsViewModel : ViewModel() {
    val topics: Either<Throwable, ObservableList<Topic>> by lazy {
        FXCollections.observableArrayList(
                (1..100).map { Topic("Sample $it") }.toList()
        ).right()
    }
}