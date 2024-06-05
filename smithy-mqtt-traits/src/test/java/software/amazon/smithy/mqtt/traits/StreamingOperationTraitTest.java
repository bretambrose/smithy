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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;

public class StreamingOperationTraitTest {
    private static final ShapeId ID = ShapeId.from("smithy.example#Foo");

    @Test
    public void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("streaming-operation.smithy"))
                .assemble()
                .unwrap();

        Shape fooOperation = result.expectShape(ID);
        assertTrue(fooOperation.hasTrait(StreamingOperationTrait.class));
        StreamingOperationTrait trait = fooOperation.expectTrait(StreamingOperationTrait.class);

        Topic topic = trait.getSubscriptionTopic();
        assertEquals(topic.getTopic(), "foo/bar");
        assertEquals(topic.getLevels().size(), 2);
        assertThat(topic.getLevels(), contains(
                new Topic.Level("foo"),
                new Topic.Level("bar")));
        assertThat(topic.getLabels(), empty());
    }
}
