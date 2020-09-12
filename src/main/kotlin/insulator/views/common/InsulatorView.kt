package insulator.views.common

import insulator.viewmodel.common.InsulatorViewModel
import javafx.scene.control.Alert
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

abstract class InsulatorView<T : InsulatorViewModel>(title: String? = null, viewModelClazz: KClass<T>) : View(title) {
    protected val viewModel: T by lazy {
        find(viewModelClazz, scope = scope)
    }

    abstract fun onError(throwable: Throwable)

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
    }
}
