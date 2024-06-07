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

public class PublishResponseTraitTest {
    private static final ShapeId GET_ID = ShapeId.from("smithy.example#GetShadow");
    private static final ShapeId UPDATE_ID = ShapeId.from("smithy.example#UpdateShadow");

    @Test
    public void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("publish-response.smithy"))
                .assemble()
                .unwrap();

        Shape getOperation = result.expectShape(GET_ID);
        assertTrue(getOperation.hasTrait(PublishResponseTrait.class));
        PublishResponseTrait getTrait = getOperation.expectTrait(PublishResponseTrait.class);

        List<Topic> getSubscriptions = getTrait.getSubscriptions();
        assertEquals(getSubscriptions.size(), 0);

        Shape updateOperation = result.expectShape(UPDATE_ID);
        assertTrue(updateOperation.hasTrait(PublishResponseTrait.class));
        PublishResponseTrait updateTrait = updateOperation.expectTrait(PublishResponseTrait.class);

        List<Topic> updateSubscriptions = updateTrait.getSubscriptions();
        assertEquals(updateSubscriptions.size(), 2);
        assertThat(updateSubscriptions,
                contains(Topic.parse(Topic.TopicType.FILTER, "$aws/things/{thingName}/shadow/update/accepted"),
                        Topic.parse(Topic.TopicType.FILTER, "$aws/things/{thingName}/shadow/update/rejected")));

    }
}
