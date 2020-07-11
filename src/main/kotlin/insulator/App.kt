package insulator

import insulator.koin.DIContainer
import insulator.views.configurations.ClustersView
import javafx.stage.Stage
import tornadofx.*

class Insulator: App(ClustersView::class) {
    override fun start(stage: Stage) {
        stage.width = 300.0
        stage.height = 400.0
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching {  launch<Insulator>(args) }
}