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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TopicTest {
    @Test
    public void requiresThatLabelsSpanWholeLevel() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/{baz}bam"));
    }

    @Test
    public void requiresThatLabelsContainOneCharacter() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/{}"));
    }

    @Test
    public void requiresThatLabelsContainValidCharacters() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/{nope nope}"));
    }

    @Test
    public void doesNotAllowDuplicateLabels() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/{nope}/{nope}"));
    }

    @Test
    public void doesNotSupportSingleLevelWildCardsOnTopics() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/+/nope"));
    }

    @Test
    public void doesNotSupportMultiLevelWildCardsOnTopics() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/nope/#"));
    }

    @Test
    public void detectsLabelSyntaxError() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, "foo/bar/nope/}"));
    }

    @Test
    public void doesNotAllowEmpty() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.TOPIC, ""));
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.FILTER, ""));
    }

    @Test
    public void doesNotAllowMixedSingleLevelWildcard() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.FILTER, "test/+d/bar"));
    }

    @Test
    public void doesNotAllowMixedMultiLevelWildcard() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.FILTER, "test/uff#dah/bar"));
    }

    @Test
    public void doesNotAllowSegmentsAfterMultiLevelWildcardTopicFilter() {
        assertThrows(TopicSyntaxException.class, () -> Topic.parse(Topic.TopicType.FILTER, "test/#/bar"));
    }

    @Test
    public void parsesTopicWithNoLabels() {
        Topic topic = Topic.parse(Topic.TopicType.TOPIC, "foo/bar/baz");

        assertThat(topic.toString(), equalTo("foo/bar/baz"));
        assertThat(topic.getLevels(), contains(
                new Topic.Level("foo"),
                new Topic.Level("bar"),
                new Topic.Level("baz")));
        assertThat(topic.conflictsWith(topic), is(true));
        assertThat(topic.getLabels(), empty());
        assertFalse(topic.hasLabel("foo"));
        assertThat(topic, equalTo(topic));
    }

    @Test
    public void parsesTopicFilterWithNoLabels() {
        Topic topic = Topic.parse(Topic.TopicType.FILTER, "foo/+/baz/#");

        assertThat(topic.toString(), equalTo("foo/+/baz/#"));
        assertThat(topic.getLevels(), contains(
                new Topic.Level("foo"),
                new Topic.Level("+"),
                new Topic.Level("baz"),
                new Topic.Level("#")));
        assertThat(topic.conflictsWith(topic), is(true));
        assertThat(topic.getLabels(), empty());
        assertFalse(topic.hasLabel("foo"));
        assertThat(topic, equalTo(topic));
    }

    @Test
    public void parsesTopicWithLabels() {
        Topic topic = Topic.parse(Topic.TopicType.TOPIC, "foo/{foo}/bar/{baz}");

        assertThat(topic, equalTo(topic));
        assertThat(topic.toString(), equalTo("foo/{foo}/bar/{baz}"));

        assertThat(topic.getLevels(), contains(
                new Topic.Level("foo"),
                new Topic.Level("foo", true),
                new Topic.Level("bar"),
                new Topic.Level("baz", true)));
        assertThat(topic.getLabels(), contains(
                new Topic.Level("foo", true),
                new Topic.Level("baz", true)));

        assertTrue(topic.hasLabel("foo"));
        assertTrue(topic.hasLabel("baz"));
        assertFalse(topic.hasLabel("bar"));
    }

    @Test
    public void topicEquality() {
        Topic topic1 = Topic.parse(Topic.TopicType.TOPIC, "foo/bar");
        Topic topic2 = Topic.parse(Topic.TopicType.TOPIC, "foo/{bar}");

        assertThat(topic1, equalTo(topic1));
        assertThat(topic1, not(equalTo(topic2)));
        assertThat(topic1, not(equalTo(null)));
    }

    @Test
    public void labelsPrintWithBraces() {
        Topic.Level level = new Topic.Level("foo", true);

        assertTrue(level.isLabel());
        assertThat(level.toString(), equalTo("{foo}"));
    }

    @Test
    public void nonLabelsPrintWithoutBraces() {
        Topic.Level level = new Topic.Level("foo");

        assertFalse(level.isLabel());
        assertThat(level.toString(), equalTo("foo"));
    }

    @Test
    public void levelEquality() {
        Topic.Level level1 = new Topic.Level("foo", true);
        Topic.Level level2 = new Topic.Level("baz", true);
        Topic.Level level3 = new Topic.Level("bar");

        assertThat(level1, equalTo(level1));
        assertThat(level1, not(equalTo(level2)));
        assertThat(level1, not(equalTo(level3)));
        assertThat(level2, not(equalTo(level3)));
        assertThat(level1, not(equalTo(null)));
    }
}
