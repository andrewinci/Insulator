package insulator.views.main

import insulator.Styles
import tornadofx.*

class OverviewView : View() {
    override val root = vbox {
        vbox {
            addClass(Styles.card)
            label("Brokers") { addClass(Styles.h1, Styles.mainColor) }
        }
        vbox {
            addClass(Styles.card)
            label("Topics") { addClass(Styles.h1, Styles.mainColor) }
        }
        vbox {
            addClass(Styles.card)
            label("Consumers") { addClass(Styles.h1, Styles.mainColor) }
        }
        vbox {
            addClass(Styles.card)
            label("Schema registry") { addClass(Styles.h1, Styles.mainColor) }
        }
    }
}

