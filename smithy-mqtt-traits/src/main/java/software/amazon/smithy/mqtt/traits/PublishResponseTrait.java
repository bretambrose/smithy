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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

public final class PublishResponseTrait extends AbstractTrait
        implements ToSmithyBuilder<PublishResponseTrait> {

    public static final ShapeId ID = ShapeId.from("smithy.mqtt#publishResponse");

    private final List<Topic> subscriptions;

    private PublishResponseTrait(Builder builder) {
        super(ID, builder.getSourceLocation());

        subscriptions = builder.subscriptions.copy().stream().map(topic -> Topic.parse(Topic.TopicType.FILTER, topic))
                .collect(Collectors.toList());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Node createNode() {
        NodeMapper mapper = new NodeMapper();
        mapper.disableToNodeForClass(PublishResponseTrait.class);
        mapper.setOmitEmptyValues(true);
        return mapper.serialize(this).expectObjectNode();
    }

    @Override
    public SmithyBuilder<PublishResponseTrait> toBuilder() {
        Builder builder = builder().sourceLocation(getSourceLocation());
        subscriptions.forEach(subscription -> builder.addSubscription(subscription.getTopic()));

        return builder;
    }

    public List<Topic> getSubscriptions() {
        return subscriptions;
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public Trait createTrait(ShapeId target, Node value) {
            PublishResponseTrait result = new NodeMapper()
                    .deserialize(value, PublishResponseTrait.class);
            result.setNodeCache(value);
            return result;
        }
    }

    public static final class Builder extends AbstractTraitBuilder<PublishResponseTrait, Builder> {
        private final BuilderRef<List<String>> subscriptions = BuilderRef.forList();

        private Builder() {}

        @Override
        public PublishResponseTrait build() {
            return new PublishResponseTrait(this);
        }

        public Builder subscriptions(List<String> subscriptions) {
            clearSubscriptions();
            this.subscriptions.get().addAll(subscriptions);
            return this;
        }

        public Builder clearSubscriptions() {
            subscriptions.get().clear();
            return this;
        }

        public Builder addSubscription(String subscription) {
            subscriptions.get().add(Objects.requireNonNull(subscription));
            return this;
        }

        public Builder removeSubscriptiopn(String subscription) {
            subscriptions.get().remove(subscription);
            return this;
        }
    }
}
