package insulator.views.configurations

import insulator.helper.dispatch
import insulator.ui.component.confirmationButton
import insulator.ui.component.h1
import insulator.viewmodel.configurations.ClusterViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
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
import tornadofx.required
import tornadofx.requiredWhen
import tornadofx.textfield
import javax.inject.Inject

class ClusterView @Inject constructor(private val viewModel: ClusterViewModel) : View() {

    private val isNewCluster: Boolean by lazy { viewModel.nameProperty.value.isNullOrEmpty() }
    private val canEdit = SimpleBooleanProperty(true)

    override val root = form {
        fieldset {
            h1("Cluster connection")
            field("Cluster name") { textfield(viewModel.nameProperty) { id = "field-cluster-name"; editableWhen(canEdit) }.required() }
            field("Endpoint (url:port)") { textfield(viewModel.endpointProperty) { id = "field-endpoint"; editableWhen(canEdit) }.required() }
            fieldset {
                disableWhen(viewModel.useSaslProperty)
                field("Use SSL (Aiven)") { checkbox(property = viewModel.useSSLProperty) }
                field("SSL Truststore Location") { textfield(viewModel.sslTruststoreLocationProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSSLProperty) }
                field("SSL Truststore Password") { textfield(viewModel.sslTruststorePasswordProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSSLProperty) }
                field("SSL Keystore Location") { textfield(viewModel.sslKeystoreLocationProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSSLProperty) }
                field("SSL KeyStore Password") { textfield(viewModel.sslKeyStorePasswordProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSSLProperty) }
            }
            fieldset {
                disableWhen(viewModel.useSSLProperty)
                field("Use SASL") { checkbox(property = viewModel.useSaslProperty) }
                field("Username") { textfield(viewModel.saslUsernameProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSaslProperty) }
                field("Password") { textfield(viewModel.saslPasswordProperty) { editableWhen(canEdit) }.requiredWhen(viewModel.useSaslProperty) }
            }
            fieldset {
                h1("Schema registry")
                field("Endpoint") { textfield(viewModel.schemaRegistryEndpointProperty) { editableWhen(canEdit) } }
                field("Username") { textfield(viewModel.schemaRegistryUsernameProperty) { editableWhen(canEdit) } }
                field("Password") { textfield(viewModel.schemaRegistryPasswordProperty) { editableWhen(canEdit) } }
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
            enableWhen(viewModel.valid)
            action {
                viewModel.commit()
                viewModel.dispatch { save() }
                close()
            }
        }

    override fun onDock() {
        title = if (isNewCluster) "New cluster" else viewModel.nameProperty.value
        super.onDock()
    }
}
