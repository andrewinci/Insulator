---
layout: page
title: Configure a SSL connection
parent: Configuration
---
# Configure a SSL connection

To configure a connection to an SSL cluster, open Insulator and click on the `Add new cluster` button.

![Cluster list view]({{site.baseurl}}/images/wiki/Configuration/ClusterList.png)

A configuration window for the new cluster will open.
Fill  `Cluster name` and `Endpoint (url:port)` as showed in the figure below, and select the `Use SSL (Aiven)` checkbox.

![New cluster view]({{site.baseurl}}/images/wiki/Configuration/NewCluster.png)

If you already have the `keystore` and `truststore` files, click on `Select file` buttons to import them. Afterwards, set up `keystore password` and `truststore password`.

Click `Save` when you are happy with the configuration.

# Configure an Aiven cluster

If you don't have a keystore and a truststore file (e.g. if you use Aiven Kafka cluster), dowload `Access key`, `Access certificate` and `CA Certificate` from the Aiven console and rename, respectively, as `service.key`, `service.cert` and `ca.pem`.

![Aiven kafka service]({{site.baseurl}}/images/wiki/Configuration/ServicesAiven.png)

Then, open a terminal windows and navigate to the folder containing the files you saved in the previous steps. and run the following commands:

```bash
openssl pkcs12 -export -inkey service.key -in service.cert -out client.keystore.p12 -name service_key

# When prompted, insert the password for the keystore

keytool -import -file ca.pem -alias CA -keystore client.truststore.jks

# When prompted, insert the password for the truststore and answer yes to `Trust this certificate? [no]:`.
```

Now for `SSL Truststore Location` select `client.truststore.jks` and set up the password.

Do the same for the `SSL Keystore Location` using the `client.truststore.jks`.

![New SSL Cluster view]({{site.baseurl}}/images/wiki/Configuration/NewSSLCluster.png)

See Aiven documentation for more details at https://help.aiven.io/en/articles/3660348-kafkatool-integration-with-aiven-for-kafka