package insulator.views.configurations

import insulator.styles.Controls
import insulator.ui.component.confirmationButton
import insulator.ui.component.h1
import insulator.viewmodel.configurations.ClusterViewModel
import javafx.geometry.Insets
import tornadofx.* // ktlint-disable no-wildcard-imports

class ClusterView : View() {
    private val viewModel: ClusterViewModel by inject()
    private val isNewCluster: Boolean by lazy { viewModel.nameProperty.value.isNullOrEmpty() }

    override val root = form {
        fieldset {
            h1("Cluster connection")
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
                h1("Schema registry")
                field("Endpoint") { textfield(viewModel.schemaRegistryEndpointProperty) }
                field("Username") { textfield(viewModel.schemaRegistryUsernameProperty) }
                field("Password") { textfield(viewModel.schemaRegistryPasswordProperty) }
            }
            borderpane {
                padding = Insets(0.0, 50.0, 0.0, 50.0)
                left = confirmationButton("Delete", "The cluster \"${viewModel.nameProperty.value}\" will be removed.", visible = !isNewCluster) {
                    viewModel.delete()
                    close()
                }
                right = button("Save") {
                    enableWhen(viewModel.valid)
                    action {
                        viewModel.commit()
                        viewModel.save()
                        close()
                    }
                }
            }
        }
        prefWidth = 600.0
        addClass(Controls.view)
    }

    override fun onDock() {
        super.onDock()
        title = if (isNewCluster) "New cluster" else viewModel.nameProperty.value
    }
}
