---
layout: page
title: Delete records
parent: Producer
---
# Delete records

For GDPR reasons or in cases when inconsistent data are published, 
we may want to delete records from a topic.

To delete a record from a topic it is enough to produce a new record with the same 
 key but with an empty value. This new record with empty value is called **tombstone**.

Notice that it is possible to delete records **only** from topics with 
the configuration `cleanup.policy` set to `compact`.

In Insulator, it is possible to check the topic configurations from the `Info` view.

![Topic info]({{site.baseurl}}/images/wiki/Producer/TopicInfo.png)

If the topic is compacted, the `Producer` view can be used to produce a **tombstone** and 
delete a record.

Set the same `Key` as the record to delete and check the `Tombstone` box. 

![Send tombstone]({{site.baseurl}}/images/wiki/Producer/SendTombstone.png)

Press `Send` when ready to produce the tombstone.

In the consumed records table, a tombstone is a normal row in which the value is `<null>`.