package insulator.ui.style

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.multi
import tornadofx.px

class TreeViewStyle : Stylesheet() {

    init {
        root {
            treeView {
                focusColor = theme.mainColor
                backgroundColor = multi(theme.backgroundColor)
                borderColor = multi(box(theme.backgroundColor))
                backgroundInsets = multi(box(0.0.px))
                padding = box(0.px, -theme.viewPadding)

                treeCell {
                    padding = box(10.0.px)
                    borderRadius = multi(box(30.0.px))
                    textFill = theme.black

                    and(even) { backgroundColor = multi(theme.backgroundColor) }
                    and(odd) { backgroundColor = multi(theme.backgroundColor) }
                    and(hover) {
                        backgroundColor = multi(theme.mainColor)
                    }
                    and(focused) {
                        backgroundColor = multi(theme.mainColor)
                    }
                }

                arrow {
                    backgroundColor = multi(theme.black)
                }
            }
        }
    }
}
