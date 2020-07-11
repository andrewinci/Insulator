package insulator

import insulator.koin.DIContainer
import insulator.views.configurations.ClustersView
import javafx.stage.Stage
import tornadofx.*

class Insulator: App(ClustersView::class)

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching {  launch<Insulator>(args) }
}