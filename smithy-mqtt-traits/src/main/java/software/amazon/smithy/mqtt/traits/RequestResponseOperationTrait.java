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
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.ToNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

public final class RequestResponseOperationTrait extends AbstractTrait
        implements ToSmithyBuilder<RequestResponseOperationTrait> {

    public static final ShapeId ID = ShapeId.from("smithy.mqtt#requestResponseOperation");

    private final List<Topic> subscriptions;
    private final Topic publishTopic;
    private final List<ResponsePath> responsePaths;

    private RequestResponseOperationTrait(Builder builder) {
        super(ID, builder.getSourceLocation());

        subscriptions = builder.subscriptions.copy().stream().map(topic -> Topic.parse(Topic.TopicType.FILTER, topic))
                .collect(Collectors.toList());
        publishTopic = Topic.parse(Topic.TopicType.TOPIC, builder.publishTopic);
        responsePaths = builder.responsePaths.copy();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Node createNode() {
        NodeMapper mapper = new NodeMapper();
        mapper.disableToNodeForClass(RequestResponseOperationTrait.class);
        mapper.setOmitEmptyValues(true);
        return mapper.serialize(this).expectObjectNode();
    }

    @Override
    public SmithyBuilder<RequestResponseOperationTrait> toBuilder() {
        Builder builder = builder().sourceLocation(getSourceLocation());
        subscriptions.forEach(subscription -> builder.addSubscription(subscription.getTopic()));
        responsePaths.forEach(builder::addResponsePath);

        return builder;
    }

    public List<Topic> getSubscriptions() {
        return subscriptions;
    }

    public Topic getPublishTopic() {
        return publishTopic;
    }

    public List<ResponsePath> getResponsePaths() {
        return responsePaths;
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public Trait createTrait(ShapeId target, Node value) {
            RequestResponseOperationTrait result = new NodeMapper()
                    .deserialize(value, RequestResponseOperationTrait.class);
            result.setNodeCache(value);
            return result;
        }
    }

    public static final class ResponsePath implements ToNode, ToSmithyBuilder<ResponsePath> {
        private final String topic;
        private final String shape;

        private ResponsePath(ResponsePath.Builder builder) {
            this.topic = builder.topic;
            this.shape = builder.shape;
        }

        @Override
        public Node toNode() {
            ObjectNode.Builder builder = Node.objectNodeBuilder()
                    .withMember("topic", Node.from(topic))
                    .withMember("shape", Node.from(shape));

            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ResponsePath other = (ResponsePath) o;
            return topic.equals(other.topic) && shape.equals(other.shape);
        }

        @Override
        public int hashCode() {
            return Objects.hash(topic, shape);
        }

        @Override
        public ResponsePath.Builder toBuilder() {
            return new ResponsePath.Builder().topic(topic).shape(shape);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder implements SmithyBuilder<ResponsePath> {
            private String topic;
            private String shape;

            @Override
            public ResponsePath build() {
                return new ResponsePath(this);
            }

            public ResponsePath.Builder topic(String topic) {
                this.topic = topic;
                return this;
            }

            public ResponsePath.Builder shape(String shape) {
                this.shape = shape;
                return this;
            }
        }
    }

    public static final class Builder extends AbstractTraitBuilder<RequestResponseOperationTrait, Builder> {
        private final BuilderRef<List<String>> subscriptions = BuilderRef.forList();
        private String publishTopic;
        private final BuilderRef<List<ResponsePath>> responsePaths = BuilderRef.forList();

        private Builder() {}

        @Override
        public RequestResponseOperationTrait build() {
            return new RequestResponseOperationTrait(this);
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

        public Builder publishTopic(String publishTopic) {
            this.publishTopic = publishTopic;
            return this;
        }

        public Builder responsePaths(List<ResponsePath> paths) {
            clearResponsePaths();
            this.responsePaths.get().addAll(paths);
            return this;
        }

        public Builder addResponsePath(ResponsePath path) {
            responsePaths.get().add(Objects.requireNonNull(path));
            return this;
        }

        public Builder clearResponsePaths() {
            responsePaths.clear();
            return this;
        }
    }
}
