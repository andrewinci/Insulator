package insulator.ui.style

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.multi
import tornadofx.px

class ScrollPaneStyle : Stylesheet() {

    init {
        root {
            scrollPane {
                focusColor = theme.backgroundColor
                borderWidth = multi(box(0.0.px))
                borderColor = multi(box(theme.backgroundColor))
                backgroundInsets = multi(box(0.0.px))
                backgroundColor = multi(theme.backgroundColor)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                viewport {
                    borderColor = multi(box(theme.backgroundColor))
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(theme.backgroundColor)
                }
                corner {
                    borderColor = multi(box(theme.backgroundColor))
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(theme.backgroundColor)
                }
            }
        }
    }
}
