package insulator.views.main

import insulator.views.common.card
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.*

class OverviewView : View() {
    override val root = vbox {
        card("Brokers")
        card("Topics")
        card("Consumers")
    }
}

