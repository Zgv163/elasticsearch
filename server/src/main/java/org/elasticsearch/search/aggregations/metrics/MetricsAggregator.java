/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.metrics;

import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.AggregatorBase;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.CardinalityUpperBound;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.support.AggregationContext;

import java.io.IOException;
import java.util.Map;

public abstract class MetricsAggregator extends AggregatorBase {
    protected MetricsAggregator(String name, AggregationContext context, Aggregator parent, Map<String, Object> metadata)
        throws IOException {
        super(name, AggregatorFactories.EMPTY, context, parent, CardinalityUpperBound.NONE, metadata);
        /*
         * MetricsAggregators may not have sub aggregators so it is safe for
         * us to pass NONE for the super ctor's subAggregatorCardinality.
         */
    }

    /**
     * Called once before any calls to {@link #buildAggregation(long)} so the
     * Aggregator can finish up any work it has to do.
     */
    protected void beforeBuildingResults(long[] ordsToCollect) throws IOException {}

    /**
     * Build an aggregation for data that has been collected into
     * {@code owningBucketOrd}. 
     */
    public abstract InternalAggregation buildAggregation(long ordToCollect) throws IOException;

    @Override
    public final InternalAggregation[] buildAggregations(long[] ordsToCollect) throws IOException {
        beforeBuildingResults(ordsToCollect);
        InternalAggregation[] results = new InternalAggregation[ordsToCollect.length];
        for (int ordIdx = 0; ordIdx < ordsToCollect.length; ordIdx++) {
            results[ordIdx] = buildAggregation(ordsToCollect[ordIdx]);
        }
        return results;
    }
}
