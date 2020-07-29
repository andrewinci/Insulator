package insulator.viewmodel.main.consumergroup

import insulator.lib.helpers.map
import insulator.lib.kafka.AdminApi
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListConsumerGroupViewModel : ViewModel() {
    private val adminApi: AdminApi by di()

    val consumerGroupList: ObservableList<String> by lazy {
        FXCollections.observableArrayList<String>().also { collection ->
            adminApi.listConsumerGroups()
                .map { it.sorted() }
                .map { collection.addAll(it) }
            // todo: handle exception
        }
    }
}
