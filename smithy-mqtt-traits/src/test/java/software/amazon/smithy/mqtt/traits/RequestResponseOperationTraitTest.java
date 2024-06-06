/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.mqtt.traits;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.List;

public class RequestResponseOperationTraitTest {
    private static final ShapeId ID = ShapeId.from("smithy.example#Foo");

    @Test
    public void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("request-response-operation.smithy"))
                .assemble()
                .unwrap();

        Shape fooOperation = result.expectShape(ID);
        assertTrue(fooOperation.hasTrait(RequestResponseOperationTrait.class));
        RequestResponseOperationTrait trait = fooOperation.expectTrait(RequestResponseOperationTrait.class);

        List<Topic> subscriptions = trait.getSubscriptions();
        assertEquals(subscriptions.size(), 1);
        assertThat(subscriptions, contains(Topic.parse(Topic.TopicType.FILTER, "$aws/things/{thingName}/shadow/get/+")));

        assertEquals("$aws/things/{thingName}/shadow/get", trait.getPublishTopic().getTopic());

        List<RequestResponseOperationTrait.ResponsePath> paths = trait.getResponsePaths();
        assertEquals(paths.size(), 2);

        assertThat(paths, contains(
            RequestResponseOperationTrait.ResponsePath.builder().topic("$aws/things/{thingName}/shadow/get/accepted").shape("GetShadowResponse").build(),
            RequestResponseOperationTrait.ResponsePath.builder().topic("$aws/things/{thingName}/shadow/get/rejected").shape("ErrorResponse").build()
        ));
    }
}
