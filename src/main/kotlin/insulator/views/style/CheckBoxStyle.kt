package insulator.views.style

import tornadofx.* // ktlint-disable no-wildcard-imports

class CheckBoxStyle : Stylesheet() {

    init {
        root {
            checkBox {
                box {
                    focusColor = theme.backgroundColor
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(theme.backgroundColor)
                    borderRadius = multi(box(0.0.px))
                    borderInsets = multi(box(1.0.px))
                    borderColor = multi(box(theme.mainColor))
                }
            }
        }
    }
}
