package insulator.viewmodel.common

import insulator.lib.helpers.runOnFXThread
import insulator.ui.common.InsulatorTabView
import insulator.viewmodel.main.MainViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ViewModel

abstract class InsulatorViewModel : ViewModel() {

    private lateinit var mainViewModel: MainViewModel

    fun setParent(mvm: MainViewModel) {
            mainViewModel = mvm
    }

    fun setMainContent(title: String, view: InsulatorTabView): Unit =
        mainViewModel.runOnFXThread { showTab(title, view) }

    val error = SimpleObjectProperty<Throwable?>(null)
}
