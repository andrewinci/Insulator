package insulator.views.component

import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import tornadofx.onDoubleClick
import tornadofx.selectedItem

fun <T> ListView<T>.action(op: (T) -> Unit) {
    val runOpOnSelected = { if (this.selectedItem != null) op(this.selectedItem!!) }
    this.onDoubleClick {
        runOpOnSelected()
    }
    this.setOnKeyPressed {
        if (it.code == KeyCode.ENTER || it.code == KeyCode.SPACE) runOpOnSelected()
    }
}
