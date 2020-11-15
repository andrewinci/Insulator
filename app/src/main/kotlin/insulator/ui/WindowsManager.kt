package insulator.ui

import insulator.di.ClusterScope
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.View
import java.io.Closeable
import javax.inject.Inject

@ClusterScope
class WindowsManager @Inject constructor() : Closeable {
    private val cache = mutableMapOf<String, View>()

    fun openWindow(id: String, owner: Window?, op: () -> View) {
        val win = Window.getWindows()
            .firstOrNull { it.scene.root.id == id }
        if (win != null) (win as Stage).toFront()
        else cache
            .getOrPut(id, { op().also { it.root.id = id } })
            .openWindow(modality = Modality.APPLICATION_MODAL, stageStyle = StageStyle.UTILITY, owner = owner)
    }

    override fun close() = cache.forEach { it.value.close() }
}
