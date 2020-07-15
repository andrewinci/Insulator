package insulator.lib.configuration.model

data class Cluster(val name: String,
                   val endpoint: String,
                   val useSSL: Boolean = false,
                   val sslTruststoreLocation: String? = null,
                   val sslTruststorePassword: String? = null,
                   val sslKeystoreLocation: String? = null,
                   val sslKeyStorePassword: String? = null
)