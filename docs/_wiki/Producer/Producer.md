---
layout: page
title: Producer
has_children: true
nav_order: 4
---
# Producer

Insulator supports producing new records to the cluster topics.
Two types of producers are currently supported: Avro and String.

The string producer allows the user to create a new record with string key and value.

To produce to a topic, let's start selecting the `Topics` view in the sidebar and search the topic we want to produce to.

![Topic view]({{site.baseurl}}/images/wiki/Producer/SearchTopic.png)

Clicking the `Produce` button will open a new modal window with the Key and Value fields. If the schema registry is configured for the current cluster, it will be possible to switch between `String` and `Avro` producer from the `Serializer` combobox.

![String producer]({{site.baseurl}}/images/wiki/Producer/StringProducer.png)

The Avro producer validates the Value field against the Avro schema for the selected topic and shows helpful validation issue in the Validation area.

In addition to the validation, the Avro producer has autocompletion built-in.
Press `CTRL+SPACE` (`CMD+SPACE` on Mac) to add a missing field on the current cursor position in the `Value` area.

![Avro producer]({{site.baseurl}}/images/wiki/Producer/AvroProducer.png)
