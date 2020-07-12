package insulator.views.configurations

import insulator.viewmodel.AddClusterViewModel
import tornadofx.*

class AddClusterView : Fragment("Add cluster") {
    private val viewModel: AddClusterViewModel by di()

    override val root = form {
        fieldset {
            field("Cluster name") { textfield(viewModel.clusterNameProperty) }
            field("Endpoint (url:port)") { textfield(viewModel.endpointProperty) }
            field("Use SSL") { checkbox(property = viewModel.useSSLProperty) }
            field("SSL Truststore Location") { textfield(viewModel.sslTruststoreLocationProperty) }
            field("SSL Truststore Password") { textfield(viewModel.sslTruststorePasswordProperty) }
            field("SSL Keystore Location") { textfield(viewModel.sslKeystoreLocationProperty) }
            field("SSL KeyStore Password") { textfield(viewModel.sslKeyStorePasswordProperty) }
            buttonbar {
                button("Test connection") { isDisable = true }
                button("Add") {
                    enableWhen(viewModel.validProperty)
                    action {
                        viewModel.save()
                        close()
                    }
                }
            }
        }
    }

}