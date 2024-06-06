$version: "2.0"

namespace smithy.example

@smithy.mqtt#requestResponseOperation(
   subscriptions: ["$aws/things/{thingName}/shadow/get/+"]
   publishTopic: "$aws/things/{thingName}/shadow/get"
   responsePaths: [
     {topic:"$aws/things/{thingName}/shadow/get/accepted", shape:"GetShadowResponse"},
     {topic:"$aws/things/{thingName}/shadow/get/rejected", shape:"ErrorResponse"}
   ]
)
operation Foo {}
