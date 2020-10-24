package insulator.ui.style

import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.em
import tornadofx.multi
import tornadofx.px

class TextStyle : Stylesheet() {
    companion object {
        val h1 by cssclass("h1")
        val h2 by cssclass("h2")
        val subTitle by cssclass("sub-title")
    }

    init {
        h1 {
            fontSize = 2.em
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = theme.mainColor
            padding = box(0.2.em, 0.2.em, 0.em, 0.2.em)
        }
        h2 {
            fontSize = 1.5.em
            fontWeight = FontWeight.BOLD
            textFill = theme.black
            padding = box(0.2.em, 0.2.em, 0.em, 0.2.em)
        }
        subTitle {
            fontSize = 1.em
            fontWeight = FontWeight.NORMAL
            textFill = theme.lightGray
            padding = box(0.1.em)
        }

        root {
            field {
                textFill = theme.black.darker()
                label {
                    textFill = theme.black.darker()
                }
            }
        }

        root {
            textField {
                textFill = theme.black
                backgroundRadius = multi(box(2.0.px))
                backgroundInsets = multi(box(0.px, (-1).px, (-1).px, (-1).px), box(0.0.px), box(0.px, (-1).px, 0.px, (-1).px))
                backgroundColor = multi(theme.mainColor, theme.backgroundColor, theme.backgroundColor.brighter())
                and(focused) {
                    backgroundRadius = multi(box(2.0.px))
                    backgroundInsets = multi(box(0.px, (-2).px, (-2).px, (-2).px), box(0.0.px), box(0.px, (-2).px, 0.px, (-2).px))
                    backgroundColor = multi(theme.mainColorDark, theme.backgroundColor, theme.backgroundColor.brighter().brighter())
                }
            }

            textArea {
                textFill = theme.black
                backgroundRadius = multi(box(0.0.px))
                backgroundInsets = multi(box(0.px, (-1).px, (-1).px, (-1).px), box(0.0.px), box(0.px, (-1).px, 0.px, (-1).px))
                backgroundColor = multi(theme.mainColor, theme.backgroundColor, theme.backgroundColor)
                and(focused) {
                    backgroundRadius = multi(box(0.0.px))
                    backgroundInsets = multi(box(0.px, (-2).px, (-2).px, (-2).px), box(0.0.px), box(0.px, (-2).px, 0.px, (-2).px))
                    backgroundColor = multi(theme.mainColorDark, theme.backgroundColor, theme.backgroundColor)
                }
                content {
                    backgroundRadius = multi(box(0.0.px))
                    backgroundInsets = multi(box(0.px, (-2).px, (-2).px, (-2).px), box(0.0.px), box(0.px, (-2).px, 0.px, (-2).px))
                    backgroundColor = multi(theme.mainColorDark, theme.backgroundColor, theme.backgroundColor)
                }
            }
        }
    }
}
