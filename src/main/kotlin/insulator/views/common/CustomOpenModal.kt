package insulator.views.common

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

// PATCH: https://github.com/edvin/tornadofx/issues/928
@JvmOverloads
fun UIComponent.customOpenWindow(
    stageStyle: StageStyle = StageStyle.DECORATED,
    modality: Modality = Modality.NONE,
    owner: Window? = currentWindow,
    resizable: Boolean? = null
): Stage? {
    if (modalStage != null) {
        with(modalStage!!){
            isIconified = false
            show()
            toFront()
        }
        return modalStage
    }
    modalStage = Stage(stageStyle)
    // modalStage needs to be set before this code to make close() work in blocking mode
    with(modalStage!!) {
        aboutToBeShown = true
        if (resizable != null) isResizable = resizable
        titleProperty().bind(titleProperty)
        initModality(modality)
        if (owner != null) initOwner(owner)

        if (customGetRootWrapper().scene != null) {
            scene = customGetRootWrapper().scene
            this.properties["tornadofx.scene"] = customGetRootWrapper().scene
        } else {
            javafx.scene.Scene(customGetRootWrapper()).apply {
                FX.applyStylesheetsTo(this)
                scene = this
                this.properties["tornadofx.scene"] = this
            }
        }

        val primaryStage = FX.getPrimaryStage(scope)
        if (primaryStage != null) icons += primaryStage.icons
        hookGlobalShortcuts()
        onBeforeShow()
        setOnShown {
            callInternalMethod("callOnDock")
            if (FX.reloadStylesheetsOnFocus || FX.reloadViewsOnFocus) {
                callInternalMethod("configureReloading")
            }
            aboutToBeShown = false
        }
        setOnHidden {
            modalStage = null
            callInternalMethod("callOnUndock")
        }
        show()
    }
    return modalStage
}

private fun UIComponent.callInternalMethod(method: String) {
    UIComponent::class.functions.find { it.name == method }?.call(this)
}

@Suppress("UNCHECKED_CAST")
private fun UIComponent.customGetRootWrapper(): Parent {
    val wrapperProp = UIComponent::class.memberProperties.find { it.name == "wrapperProperty" }?.get(this) as SimpleObjectProperty<Parent>
    return wrapperProp.value ?: this.root
}
