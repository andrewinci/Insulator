package insulator

import tornadofx.*


class MyView: View() {
    override val root = vbox {
        label("Waiting")
    }
}

class MyApp: App(MyView::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}