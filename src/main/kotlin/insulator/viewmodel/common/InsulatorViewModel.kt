package insulator.viewmodel.common

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

abstract class InsulatorViewModel : ViewModel() {
    val error = SimpleObjectProperty<Throwable?>(null)
}
