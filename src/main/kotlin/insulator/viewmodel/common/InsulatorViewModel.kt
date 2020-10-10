package insulator.viewmodel.common

import insulator.viewmodel.main.MainViewModel
import insulator.views.common.InsulatorTabView
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ViewModel

abstract class InsulatorViewModel : ViewModel() {

    fun setMainContent(title: String, view: InsulatorTabView<*>): Unit =
        find<MainViewModel>().setContent(title, view)

    val error = SimpleObjectProperty<Throwable?>(null)
}
