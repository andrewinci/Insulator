package insulator.helper

import javafx.beans.Observable
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> Array<T>.toObservable(op: (T) -> String): ObservableList<String> = FXCollections.observableArrayList(toList().map { op(it) }.toList())
fun <T> createListBindings(op: () -> Collection<T>, vararg observable: Observable): ObservableList<T> {
    val result = FXCollections.observableArrayList(op())
    observable.forEach {
        it.addListener { result.clear().also { result.addAll(op()) } }
    }
    return result
}
