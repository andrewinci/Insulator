package insulator.views.style

import tornadofx.* // ktlint-disable no-wildcard-imports

class CheckBoxStyle : Stylesheet() {

    init {
        root {
            checkBox {
                box {
                    focusColor = Theme.backgroundColor
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(Theme.backgroundColor)
                    borderRadius = multi(box(0.0.px))
                    borderInsets = multi(box(1.0.px))
                    borderColor = multi(box(Theme.mainColor))
                }
            }
        }
    }
}
