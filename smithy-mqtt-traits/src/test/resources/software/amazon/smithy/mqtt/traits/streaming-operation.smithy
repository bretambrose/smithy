$version: "2.0"

namespace smithy.example

@smithy.mqtt#streamingOperation(subscription: "foo/bar")
operation Foo {}
