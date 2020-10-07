package insulator.views.style

import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports

val styles = arrayOf(
    AppBarStyle::class,
    ButtonStyle::class,
    CheckBoxStyle::class,
    ComboBoxStyle::class,
    ListViewStyle::class,
    Root::class,
    TableViewStyle::class,
    TextStyle::class,
    DialogPaneStyle::class,
    SideBarStyle::class,
    ScrollPaneStyle::class
)

class Root : Stylesheet() {
    init {
        root {
            font = Font.font("Helvetica", 10.0)
            backgroundColor = multi(Theme.backgroundColor)
            padding = box(Theme.viewPadding)
        }
        contextMenu {
            padding = box(0.px)
            minWidth = 100.0.px
            textFill = Color.BLACK
            menuItem {
                padding = box(10.0.px)
                and(focused) {
                    backgroundColor = multi(Theme.mainColor)
                    label {
                        textFill = Theme.backgroundColor
                        textFill = Theme.backgroundColor
                    }
                }
            }
        }
    }
}

data class ThemeTest(
    val black: Color = Color.BLACK,
    val backgroundColor: Color = Color.WHITE,
    val mainColor: Color = c("#FF9100"),
    val mainColorDark: Color = c("#D65400"),
    val alertColor: Color = c("#cc0016"),
    val alertColorDark: Color = c("#960017"),
    val lightGray: Color = c("#ccc"),
    val darkGray: Color = c("#666"),
    val blueColor: Color = Color.BLUE,
    val viewPadding: Dimension<Dimension.LinearUnits> = 1.em,
)

val Theme = ThemeTest()
