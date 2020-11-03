package insulator.helper

import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> Array<T>.toObservable(): ObservableList<String> = FXCollections.observableArrayList(toList().map { it.toString() }.toList())