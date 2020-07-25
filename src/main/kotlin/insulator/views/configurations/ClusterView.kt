package insulator.views.configurations

import insulator.Styles
import insulator.viewmodel.configurations.ClusterViewModel
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*

class ClusterView : View() {
    private val viewModel: ClusterViewModel by inject()
    private val isNewCluster: Boolean by lazy { viewModel.nameProperty.value.isNullOrEmpty() }

    override val root = form {
        fieldset {
            label("Cluster connection") { addClass(Styles.h1) }
            field("Cluster name") { textfield(viewModel.nameProperty).required() }
            field("Endpoint (url:port)") { textfield(viewModel.endpointProperty).required() }
            fieldset {
                disableWhen(viewModel.useSaslProperty)
                field("Use SSL (Aiven)") { checkbox(property = viewModel.useSSLProperty) }
                field("SSL Truststore Location") { textfield(viewModel.sslTruststoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL Truststore Password") { textfield(viewModel.sslTruststorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL Keystore Location") { textfield(viewModel.sslKeystoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
                field("SSL KeyStore Password") { textfield(viewModel.sslKeyStorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
            }
            fieldset {
                disableWhen(viewModel.useSSLProperty)
                field("Use SASL") { checkbox(property = viewModel.useSaslProperty) }
                field("Username") { textfield(viewModel.saslUsernameProperty).requiredWhen(viewModel.useSaslProperty) }
                field("Password") { textfield(viewModel.saslPasswordProperty).requiredWhen(viewModel.useSaslProperty) }
            }
            fieldset {
                label("Schema registry") { addClass(Styles.h1) }
                field("Endpoint") { textfield(viewModel.schemaRegistryEndpointProperty) }
                field("Username") { textfield(viewModel.schemaRegistryUsernameProperty) }
                field("Password") { textfield(viewModel.schemaRegistryPasswordProperty) }
            }
            borderpane {
                padding = Insets(0.0, 50.0, 0.0, 50.0)
                left = button("Delete") {
                    if (isNewCluster) isVisible = false
                    addClass(Styles.alertButton)
                    action {
                        val closeWindow = {close()}
                        alert(Alert.AlertType.WARNING,
                                "The cluster \"${viewModel.nameProperty.value}\" will be removed.", null,
                                ButtonType.CANCEL, ButtonType.OK,
                                owner = currentWindow,
                                actionFn = { buttonType ->
                                    when (buttonType) {
                                        ButtonType.OK -> {
                                            viewModel.delete()
                                            closeWindow()
                                        }
                                        else -> Unit
                                    }
                                })
                    }

                }
                right = button("Save") {
                    enableWhen(viewModel.valid)
                    action {
                        viewModel.commit()
                        viewModel.save()
                        viewModel.rollback()
                        close()
                    }
                }
            }
        }
        prefWidth = 600.0
    }

    override fun onDock() {
        title = if (isNewCluster) "New cluster" else viewModel.nameProperty.value
        super.onDock()
    }

}