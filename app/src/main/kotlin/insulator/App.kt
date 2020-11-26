package insulator

import insulator.di.DaggerInsulatorComponent
import insulator.helper.dispatch
import insulator.ui.style.AppBarStyle
import insulator.ui.style.ButtonStyle
import insulator.ui.style.CheckBoxStyle
import insulator.ui.style.ComboBoxStyle
import insulator.ui.style.DialogPaneStyle
import insulator.ui.style.ListViewStyle
import insulator.ui.style.MainViewStyle
import insulator.ui.style.Root
import insulator.ui.style.ScrollBarStyle
import insulator.ui.style.ScrollPaneStyle
import insulator.ui.style.TableViewStyle
import insulator.ui.style.TextStyle
import insulator.ui.style.TreeViewStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.NoPrimaryViewSpecified
import tornadofx.launch

class Insulator : App(
    NoPrimaryViewSpecified::class,
    Root::class,
    AppBarStyle::class,
    ButtonStyle::class,
    CheckBoxStyle::class,
    ComboBoxStyle::class,
    ListViewStyle::class,
    TableViewStyle::class,
    TextStyle::class,
    DialogPaneStyle::class,
    ScrollPaneStyle::class,
    MainViewStyle::class,
    ScrollBarStyle::class,
    TreeViewStyle::class
) {
    private val daggerInsulator = DaggerInsulatorComponent.builder().build()

    override fun start(stage: Stage) {
        super.start(stage)
        val view = daggerInsulator.getListClusterView()
        daggerInsulator.getThemeHelper().dispatch {
            stage.scene = createPrimaryScene(view)
            updateUITheme()
            FX.applyStylesheetsTo(stage.scene)
            stage.show()
            view.onDock()
        }
    }
}

fun main(args: Array<String>) {
    runCatching { launch<Insulator>(args) }
}
