package insulator.views.configurations

import insulator.helper.dispatch
import insulator.ui.component.confirmationButton
import insulator.ui.component.h1
import insulator.viewmodel.configurations.ClusterViewModel
import javafx.beans.binding.Bindings
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
            field("Cluster name") { customTextField(viewModel.nameProperty) { id = "field-cluster-name" }.required() }
            field("Endpoint (url:port)") { customTextField(viewModel.endpointProperty) { id = "field-endpoint" }.required() }
            fieldset {
                disableWhen(viewModel.useSaslProperty)
                field("Use SSL (Aiven)") { checkbox(property = viewModel.useSSLProperty) }
                field("SSL Truststore Location") { customTextField(viewModel.sslTruststoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL Truststore Password") { customTextField(viewModel.sslTruststorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL Keystore Location") { customTextField(viewModel.sslKeystoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL KeyStore Password") { customTextField(viewModel.sslKeyStorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
            }
            fieldset {
                disableWhen(viewModel.useSSLProperty)
                field("Use SASL") { checkbox(property = viewModel.useSaslProperty) }
                field("Username") { customTextField(viewModel.saslUsernameProperty).requiredWhen(viewModel.useSaslProperty) }
                field("Password") { customTextField(viewModel.saslPasswordProperty).requiredWhen(viewModel.useSaslProperty) }
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

    private fun EventTarget.customTextField(property: ObservableValue<String>, op: TextField.() -> Unit = {}) =
        textfield(property) { editableWhen(canEdit); op() }

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
                if (text == "Save") {
                    viewModel.commit()
                    viewModel.dispatch { save() }
                }
                close()
            }
        }

    override fun onDock() {
        title = if (isNewCluster) "New cluster" else viewModel.nameProperty.value
        super.onDock()
    }
}
