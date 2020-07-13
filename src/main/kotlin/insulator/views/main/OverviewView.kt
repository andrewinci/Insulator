package insulator.views.main

import insulator.views.common.card
import javafx.scene.layout.VBox

class OverviewView : VBox() {
    init {
        card("Brokers")
        card("Topics")
        card("Consumers")
    }
}