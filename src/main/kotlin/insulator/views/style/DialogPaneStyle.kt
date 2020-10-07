package insulator.views.style

import tornadofx.* // ktlint-disable no-wildcard-imports

class DialogPaneStyle : Stylesheet() {

    init {
        root {
            dialogPane {
                padding = tornadofx.box(Theme.viewPadding)
                headerPanel {
                    borderColor = multi(box(Theme.backgroundColor))
                    borderColor = multi(box(Theme.backgroundColor))
                    backgroundColor = multi(Theme.backgroundColor)
                    backgroundColor = multi(Theme.backgroundColor)
                    backgroundInsets = multi(box(0.0.px))
                    padding = box(0.0.px)
                }
                buttonBar {
                    padding = box(0.0.px, (-10.0).px, 0.0.px, 0.0.px)
                    cancel {
                        textFill = Theme.alertColor
                        and(hover) {
                            textFill = Theme.backgroundColor
                            backgroundColor = multi(Theme.alertColor)
                        }
                    }
                }
                maxHeight = 140.0.px
                prefHeight = 140.0.px
                minHeight = 140.0.px
            }
        }
    }
}
