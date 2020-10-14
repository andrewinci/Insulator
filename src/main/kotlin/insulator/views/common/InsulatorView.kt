package insulator.views.common

import insulator.viewmodel.common.InsulatorViewModel
import javafx.scene.control.Alert
import javafx.stage.Screen
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

abstract class InsulatorView<T : InsulatorViewModel>(title: String? = null, viewModelClazz: KClass<T>) : View(title) {
    protected val viewModel: T by lazy {
        find(viewModelClazz, scope = scope)
    }

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

abstract class InsulatorTabView<T : InsulatorViewModel>(viewModelClazz: KClass<T>) : InsulatorView<T>(null, viewModelClazz) {
    private val listeners = mutableListOf<() -> Unit>()

    open fun onTabClosed() = Unit

    fun setOnCloseListener(op: () -> Unit) = listeners.add(op)

    fun closeTab() = listeners.forEach { it() }
}
