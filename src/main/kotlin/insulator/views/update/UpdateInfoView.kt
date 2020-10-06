package insulator.views.update

import insulator.lib.update.Release
import insulator.styles.Controls
import insulator.ui.component.h1
import insulator.ui.component.h2
import javafx.geometry.Pos
import tornadofx.* // ktlint-disable no-wildcard-imports

class UpdateInfoView(release: Release) : View() {

    override val root = borderpane {
        top = vbox {
            h1("New version available")
            h2("Insulator ${release.version}")
        }
        center = vbox(alignment = Pos.CENTER, spacing = 7.0) {
            hyperlink("Web") { action { FX.application.hostServices.showDocument(release.webUrl) } }
            hyperlink("Debian") { action { FX.application.hostServices.showDocument(release.debianUrl) } }
            hyperlink("Mac os") { action { FX.application.hostServices.showDocument(release.macUrl) } }
            hyperlink("Windows") { action { FX.application.hostServices.showDocument(release.winUrl) } }
        }
        bottom = hbox(alignment = Pos.CENTER) {
            button("Close") { action { close() } }
        }
        addClass(Controls.view)
    }

    override fun onDock() {
        super.currentStage?.resizableProperty()?.set(false)
        super.currentStage?.height = 350.0
        super.currentStage?.width = 400.0
        super.onDock()
    }
}
