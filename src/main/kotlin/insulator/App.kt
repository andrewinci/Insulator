package insulator

import insulator.di.DIContainer
import insulator.styles.Controls
import insulator.styles.Root
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.views.configurations.ListClusterView
import tornadofx.App
import tornadofx.FX
import tornadofx.launch

class Insulator : App(ListClusterView::class, Theme::class, Titles::class, Root::class, Controls::class)

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching { launch<Insulator>(args) }
}
