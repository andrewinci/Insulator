package insulator.views.configurations

import insulator.helper.dispatch
import insulator.ui.component.confirmationButton
import insulator.ui.component.h1
import insulator.viewmodel.configurations.ClusterViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.disableWhen
import tornadofx.editableWhen
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.textfield
import javax.inject.Inject

class ClusterView @Inject constructor(private val viewModel: ClusterViewModel) : View() {

    private val isNewCluster: Boolean by lazy { viewModel.nameProperty.value.isNullOrEmpty() }
    private val canEdit = SimpleBooleanProperty(true)

    override val root = form {
        fieldset {
            h1("Cluster connection")
            field("Cluster name") { customTextField(viewModel.nameProperty) { id = "field-cluster-name" } }
            field("Endpoint (url:port)") { customTextField(viewModel.endpointProperty) { id = "field-endpoint" } }
            fieldset {
                disableWhen(viewModel.useSaslProperty)
                field("Use SSL (Aiven)") { checkbox(property = viewModel.useSSLProperty) { enableWhen(canEdit) } }
                field("SSL Truststore Location") { customTextField(viewModel.sslTruststoreLocationProperty, viewModel.useSSLProperty) }
                field("SSL Truststore Password") { customTextField(viewModel.sslTruststorePasswordProperty, viewModel.useSSLProperty) }
                field("SSL Keystore Location") { customTextField(viewModel.sslKeystoreLocationProperty, viewModel.useSSLProperty) }
                field("SSL KeyStore Password") { customTextField(viewModel.sslKeyStorePasswordProperty, viewModel.useSSLProperty) }
            }
            fieldset {
                disableWhen(viewModel.useSSLProperty)
                field("Use SASL") { checkbox(property = viewModel.useSaslProperty) { enableWhen(canEdit) } }
                field("Username") { customTextField(viewModel.saslUsernameProperty, viewModel.useSaslProperty) }
                field("Password") { customTextField(viewModel.saslPasswordProperty, viewModel.useSaslProperty) }
                field("SCRAM") { checkbox(property = viewModel.useScramProperty) { enableWhen(canEdit) } }
            }
            fieldset {
                h1("Schema registry")
                field("Endpoint") { customTextField(viewModel.schemaRegistryEndpointProperty) }
                field("Username") { customTextField(viewModel.schemaRegistryUsernameProperty) }
                field("Password") { customTextField(viewModel.schemaRegistryPasswordProperty) }
            }
        }
        borderpane {
            left = deleteButton()
            right = saveButton()
        }
        prefWidth = 600.0
    }

    fun show(isEditable: Boolean = true) {
        this.openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
        this.canEdit.set(isEditable)
    }

    private fun EventTarget.customTextField(property: ObservableValue<String>, dependency: BooleanProperty? = null, op: TextField.() -> Unit = {}) =
        textfield(property) {
            editableWhen(canEdit)
            if (dependency != null) enableWhen(dependency)
            op()
        }

    private fun EventTarget.deleteButton() = confirmationButton(
        "Delete",
        "The cluster \"${viewModel.nameProperty.value}\" will be removed.",
        Bindings.createBooleanBinding({ !isNewCluster && canEdit.value }, canEdit)
    ) {
        viewModel.dispatch { delete() }
        close()
    }

    private fun EventTarget.saveButton() =
        button {
            textProperty().bind(Bindings.createStringBinding({ if (canEdit.value) "Save" else "Close" }, canEdit))
            enableWhen(viewModel.isValidProperty)
            action {
                if (text == "Save") viewModel.dispatch { save() }
                close()
            }
        }

    override fun onDock() {
        title = if (isNewCluster) "New cluster" else viewModel.nameProperty.value
        super.onDock()
    }
}
