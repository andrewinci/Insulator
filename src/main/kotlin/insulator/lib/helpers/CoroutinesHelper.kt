package insulator.lib.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch

fun <T : Any> T.dispatch(block: suspend T.() -> Unit) = GlobalScope.launch(Dispatchers.JavaFx) { block() }