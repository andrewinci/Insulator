package insulator

import insulator.di.DIContainer
import insulator.views.configurations.ListClusterView
import tornadofx.*

class Insulator : App(ListClusterView::class, Styles::class) {
}

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching { launch<Insulator>(args) }
}