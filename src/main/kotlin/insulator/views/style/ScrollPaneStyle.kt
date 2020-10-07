package insulator.views.style

import tornadofx.* // ktlint-disable no-wildcard-imports

class ScrollPaneStyle : Stylesheet() {

    init {
        root {
            scrollPane {
                focusColor = Theme.backgroundColor
                focusColor = Theme.backgroundColor
                borderWidth = multi(box(0.0.px))
                borderColor = multi(box(Theme.backgroundColor))
                borderColor = multi(box(Theme.backgroundColor))
                backgroundInsets = multi(box(0.0.px))
                backgroundColor = multi(Theme.backgroundColor)
                backgroundColor = multi(Theme.backgroundColor)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                viewport {
                    borderColor = multi(box(Theme.backgroundColor))
                    borderColor = multi(box(Theme.backgroundColor))
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(Theme.backgroundColor)
                    backgroundColor = multi(Theme.backgroundColor)
                }
                corner {
                    borderColor = multi(box(Theme.backgroundColor))
                    borderColor = multi(box(Theme.backgroundColor))
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(Theme.backgroundColor)
                    backgroundColor = multi(Theme.backgroundColor)
                }
            }
        }
    }
}
