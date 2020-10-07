package insulator.views.style

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.multi
import tornadofx.px

class ListViewStyle : Stylesheet() {
    init {
        listView {
            focusColor = Theme.mainColor
            borderColor = multi(box(Theme.backgroundColor))
            backgroundInsets = multi(box(0.0.px))
//            and(focused) {
//                borderRadius = multi(box(30.0.px))
//                backgroundInsets = multi(box(0.0.px))
//            }
            listCell {
                padding = box(10.0.px)
                borderRadius = multi(box(30.0.px))
                Stylesheet.label { textFill = Color.BLACK }
                and(even) { backgroundColor = multi(Color.TRANSPARENT) }
                and(odd) { backgroundColor = multi(Color.TRANSPARENT) }
                and(hover) {
                    backgroundColor = multi(Theme.mainColor)
                }
                and(focused) {
                    backgroundColor = multi(Theme.mainColor)
                }
            }
        }
    }
}
