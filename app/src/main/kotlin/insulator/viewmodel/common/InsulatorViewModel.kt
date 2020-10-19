package insulator.viewmodel.common

import insulator.ui.common.InsulatorTabView
import insulator.viewmodel.main.MainViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ViewModel

abstract class InsulatorViewModel : ViewModel() {

    fun setMainContent(title: String, view: InsulatorTabView<*>): Unit =
        find<MainViewModel>().showTab(title, view)

    val error = SimpleObjectProperty<Throwable?>(null)
}
