package insulator.viewmodel.common

import insulator.viewmodel.main.MainViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.View
import tornadofx.ViewModel

abstract class InsulatorViewModel : ViewModel() {

    fun setMainContent(view: View): Unit = find<MainViewModel>().setDetails(view)

    val error = SimpleObjectProperty<Throwable?>(null)
}
