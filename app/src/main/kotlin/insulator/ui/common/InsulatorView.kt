package insulator.ui.common

import insulator.viewmodel.common.InsulatorViewModel
import javafx.scene.control.Alert
import javafx.stage.Screen
import tornadofx.* // ktlint-disable no-wildcard-imports

abstract class InsulatorView(title: String? = null) : View(title) {

    abstract val viewModel: InsulatorViewModel

    open fun onError(throwable: Throwable) {}

    override fun onDock() {
        val handleError: (Throwable?) -> Unit = {
            if (it != null) {
                alert(Alert.AlertType.WARNING, it.message ?: it.toString())
                onError(it)
            }
        }
        if (viewModel.error.value != null) handleError(viewModel.error.value)
        viewModel.error.onChange { handleError(it) }
        super.onDock()
        center()
    }

    fun center() {
        val screenSize = Screen.getPrimary().bounds
        if (super.currentStage == null) return
        with(super.currentStage!!) {
            x = (screenSize.width - width) / 2
        }
    }
}

abstract class InsulatorTabView : InsulatorView() {
    private val listeners = mutableListOf<() -> Unit>()

    open fun onTabClosed() = Unit

    fun setOnCloseListener(op: () -> Unit) = listeners.add(op)

    fun closeTab() = listeners.forEach { it() }
}
