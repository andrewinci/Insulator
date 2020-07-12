package insulator.viewmodel

import insulator.kafka.AdminApi
import insulator.model.Topic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*

class TopicsViewModel(private val adminApi: AdminApi) : ViewModel() {
    private var updating = false;
    private val internal = FXCollections.observableArrayList<Topic>()

    val topicsProperty: ObservableList<Topic> by lazy {
        update()
        internal
    }

    private fun update() {
        if (updating) return
        updating = true
        GlobalScope.launch {
            adminApi.listTopics()
                    .map {
                        FXCollections.observableArrayList(it)
                    }
                    .unsafeRunAsync {
                        it.map { topics -> internal.addAll(topics) }
                    }
        }
    }
}