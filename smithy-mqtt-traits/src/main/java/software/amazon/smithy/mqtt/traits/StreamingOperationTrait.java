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

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;


/**
 *
 */
public final class StreamingOperationTrait extends AbstractTrait
        implements ToSmithyBuilder<StreamingOperationTrait> {

    public static final ShapeId ID = ShapeId.from("smithy.mqtt#streamingOperation");

    private final Topic subscription;

    private StreamingOperationTrait(Builder builder) {
        super(ID, builder.getSourceLocation());
        subscription = Topic.parse(Topic.TopicType.FILTER, builder.subscription);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Topic getSubscriptionTopic() {
        return subscription;
    }

    @Override
    protected Node createNode() {
        NodeMapper mapper = new NodeMapper();
        mapper.disableToNodeForClass(StreamingOperationTrait.class);
        mapper.setOmitEmptyValues(true);
        return mapper.serialize(this).expectObjectNode();
    }

    @Override
    public SmithyBuilder<StreamingOperationTrait> toBuilder() {
        return builder().sourceLocation(getSourceLocation()).subscription(subscription.getTopic());
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public Trait createTrait(ShapeId target, Node value) {
            StreamingOperationTrait result = new NodeMapper().deserialize(value, StreamingOperationTrait.class);
            result.setNodeCache(value);
            return result;
        }
    }

    public static final class Builder extends AbstractTraitBuilder<StreamingOperationTrait, Builder> {
        private String subscription;

        private Builder() {}

        @Override
        public StreamingOperationTrait build() {
            return new StreamingOperationTrait(this);
        }

        public Builder subscription(String subscription) {
            this.subscription = subscription;
            return this;
        }
    }
}
