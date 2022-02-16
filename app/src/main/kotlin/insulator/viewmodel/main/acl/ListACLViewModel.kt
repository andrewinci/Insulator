package insulator.viewmodel.main.acl

import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.AdminApi
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.SortedFilteredList
import javax.inject.Inject

class ListACLViewModel @Inject constructor(
    private val adminClient: AdminApi
) : InsulatorViewModel() {

    private val aclsProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedACLProperty = SimpleStringProperty()
    val searchItemProperty = SimpleStringProperty()

    val filteredACLProperty = SortedFilteredList(aclsProperty).apply {
        filterWhen(searchItemProperty) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
    }.filteredItems

    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "ACLs count: ${filteredACLProperty.size}/${aclsProperty.size}"
        },
        aclsProperty,
        filteredACLProperty
    )

    init {
        dispatch { refresh() }
    }

    suspend fun refresh() = adminClient
        .listACLs()
        .map { it.sorted() }
        .map {
            it.runOnFXThread {
                aclsProperty.clear()
                aclsProperty.addAll(it)
            }
        }.mapLeft {
            error.set(Error(it.message ?: "Unable to load the acl list"))
        }

    suspend fun showACL() {
    }
}