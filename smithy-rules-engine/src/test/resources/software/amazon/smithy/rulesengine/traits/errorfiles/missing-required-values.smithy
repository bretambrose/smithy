$version: "2.0"

namespace smithy.example

use smithy.rules#endpointRuleSet
use smithy.rules#endpointTests

@endpointRuleSet({
    version: "1.0"
    parameters: {
        endpoint: {
            type: "string"
            builtIn: "SDK::Endpoint"
            documentation: "docs"
            default: "https://example.com"
            required: true
        }
    }
    rules: [
        {
            conditions: []
            documentation: "Passthrough"
            error: "Failed to resolve."
            type: "error"
        }
    ]
})
@endpointTests({
    version: "1.0"
    testCases: [
        {
            operationInputs: [{
                operationName: "GetThing"
                operationParams: {
                    "buzz": "a buzz value"
                }
            }]
            expect: {
                error: "Failed to resolve."
            }
        }
    ]
})
@suppress(["RuleSetParameter.TestCase.Unused"])
service InvalidService {
    version: "2022-01-01"
    operations: [GetThing]
}

@readonly
operation GetThing {
    input := {
        @required
        fizz: String
        buzz: String
        fuzz: String
    }
}
