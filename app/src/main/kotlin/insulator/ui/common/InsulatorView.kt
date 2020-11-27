package insulator.ui.common

import insulator.ui.style.Root
import insulator.viewmodel.common.InsulatorViewModel
import javafx.scene.control.Alert
import tornadofx.View
import tornadofx.addClass
import tornadofx.alert
import tornadofx.onChange

abstract class InsulatorView(title: String? = null) : View(title) {

    abstract val viewModel: InsulatorViewModel

    open fun onError(throwable: Throwable) {}

    override fun onDock() {
        root.addClass(Root.insulatorView)
        val handleError: (Throwable?) -> Unit = {
            if (it != null) {
                alert(Alert.AlertType.WARNING, owner = currentWindow, header = it.message ?: it.toString())
                onError(it)
                viewModel.error.value = null
            }
        }
        if (viewModel.error.value != null) handleError(viewModel.error.value)
        viewModel.error.onChange { handleError(it) }
        super.onDock()
    }
}

abstract class InsulatorTabView : InsulatorView() {
    private val listeners = mutableListOf<() -> Unit>()

    open fun onTabClosed() = Unit

    fun setOnCloseListener(op: () -> Unit) = listeners.add(op)

    fun closeTab() = listeners.forEach { it() }
}
