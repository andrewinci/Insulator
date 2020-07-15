package insulator

import insulator.lib.koin.DIContainer
import insulator.views.configurations.ListClusterView
import tornadofx.*

class Insulator: App(ListClusterView::class)

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching {  launch<Insulator>(args) }
}