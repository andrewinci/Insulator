package insulator.views.configurations

import insulator.viewmodel.ClusterViewModel
import tornadofx.*

class AddClusterView : Fragment("Add cluster") {
    private val viewModel: ClusterViewModel by inject()

    override val root = form {
        fieldset {
            field("Cluster name") { textfield(viewModel.nameProperty).required() }
            field("Endpoint (url:port)") { textfield(viewModel.endpointProperty).required() }
            field("Use SSL") { checkbox(property = viewModel.useSSLProperty) }
            field("SSL Truststore Location") { textfield(viewModel.sslTruststoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
            field("SSL Truststore Password") { textfield(viewModel.sslTruststorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
            field("SSL Keystore Location") { textfield(viewModel.sslKeystoreLocationProperty).requiredWhen(viewModel.useSSLProperty) }
            field("SSL KeyStore Password") { textfield(viewModel.sslKeyStorePasswordProperty).requiredWhen(viewModel.useSSLProperty) }
            buttonbar {
                button("Test connection") { isDisable = true }
                button("Add") {
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
    }

}