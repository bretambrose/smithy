$version: "2.0"

namespace smithy.example

@smithy.mqtt#publishResponse()
operation GetShadow {}

@smithy.mqtt#publishResponse(
    subscriptions: ["$aws/things/{thingName}/shadow/update/accepted", "$aws/things/{thingName}/shadow/update/rejected"]
)
operation UpdateShadow {}
