package insulator.views.configurations

import insulator.viewmodel.AddClusterViewModel
import tornadofx.*

class AddClusterView : Fragment("Add cluster") {
    private val viewModel: AddClusterViewModel by di()

    override val root = form {
        fieldset {
            field("Cluster name") {
                textfield(viewModel.clusterName)
            }
            field("Endpoint (url:port)") {
                textfield(viewModel.endpoint).required()
            }
            buttonbar {
                button("Test connection") {
                    //todo: add logic to test connection
                }
                button("Add") {
                    enableWhen(viewModel.valid)
                    action {
                        viewModel.save()
                        close()
                    }
                }
            }
        }
    }

}