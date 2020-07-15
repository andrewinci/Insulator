package insulator.lib.configuration.model

import java.util.*

data class Cluster(val guid: UUID,
                   val name: String,
                   val endpoint: String,
                   val useSSL: Boolean = false,
                   val sslTruststoreLocation: String? = null,
                   val sslTruststorePassword: String? = null,
                   val sslKeystoreLocation: String? = null,
                   val sslKeyStorePassword: String? = null
)