package insulator

import insulator.views.configurations.ClustersView
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(ClustersView::class) {
    override fun start(stage: Stage) {
        stage.width = 300.0
        stage.height = 400.0
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MyApp>(args)
}