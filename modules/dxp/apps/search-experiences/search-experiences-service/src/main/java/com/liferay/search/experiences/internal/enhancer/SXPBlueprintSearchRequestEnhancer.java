/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.internal.enhancer;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregation;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.search.experiences.rest.dto.v1_0.Aggregation;
import com.liferay.search.experiences.rest.dto.v1_0.Avg;
import com.liferay.search.experiences.rest.dto.v1_0.Cardinality;
import com.liferay.search.experiences.rest.dto.v1_0.Clause;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.General;
import com.liferay.search.experiences.rest.dto.v1_0.Query;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;

import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class SXPBlueprintSearchRequestEnhancer {

	public SXPBlueprintSearchRequestEnhancer(
		Aggregations aggregations,
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		Queries queries, SearchRequestBuilder searchRequestBuilder,
		SXPBlueprint sxpBlueprint) {

		_aggregations = aggregations;
		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_queries = queries;
		_searchRequestBuilder = searchRequestBuilder;

		_configuration = sxpBlueprint.getConfiguration();
	}

	public void enhance() {
		_processAggregations(_configuration.getAggregations());
		_processGeneral(_configuration.getGeneral());
		_processQueries(_configuration.getQueries());
	}

	private void _processAggregations(Map<String, Aggregation> aggregations) {
		for (Map.Entry<String, Aggregation> entry : aggregations.entrySet()) {
			_searchRequestBuilder.addAggregation(
				_toPortalSearchAggregation(entry.getValue(), entry.getKey()));
		}
	}

	private void _processClause(Clause clause) {
		com.liferay.portal.search.query.Query query = _queries.wrapper(
			clause.getQueryJSON());

		if (query != null) {
			_searchRequestBuilder.addComplexQueryPart(
				_complexQueryPartBuilderFactory.builder(
				).occur(
					clause.getOccur()
				).query(
					query
				).build());
		}
	}

	private void _processGeneral(General general) {
		if (general.getApplyIndexerClauses() != null) {
			_searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"search.full.query.suppress.indexer.provided.clauses",
					!general.getApplyIndexerClauses()));
		}
	}

	private void _processQueries(Query[] queries) {
		if (ArrayUtil.isEmpty(queries)) {
			return;
		}

		for (Query query : queries) {
			if (!GetterUtil.getBoolean(query.getEnabled())) {
				continue;
			}

			Clause[] clauses = query.getClauses();

			if (ArrayUtil.isEmpty(clauses)) {
				continue;
			}

			for (Clause clause : clauses) {
				_processClause(clause);
			}
		}
	}

	private com.liferay.portal.search.aggregation.Aggregation
		_toPortalSearchAggregation(Aggregation aggregation1, String name1) {

		com.liferay.portal.search.aggregation.Aggregation
			portalSearchAggregation = null;

		if (aggregation1.getAvg() != null) {
			Avg avg = aggregation1.getAvg();

			portalSearchAggregation = _aggregations.avg(name1, avg.getField());
		}
		else if (aggregation1.getCardinality() != null) {
			Cardinality cardinality = aggregation1.getCardinality();

			CardinalityAggregation cardinalityAggregation =
				_aggregations.cardinality(name1, cardinality.getField());

			cardinalityAggregation.setPrecisionThreshold(
				cardinality.getPrecision_threshold());

			portalSearchAggregation = cardinalityAggregation;
		}
		else {
			throw new IllegalArgumentException();
		}

		if (MapUtil.isEmpty(aggregation1.getAggs())) {
			return portalSearchAggregation;
		}

		Map<String, Aggregation> aggs = aggregation1.getAggs();

		for (Map.Entry<String, Aggregation> entry : aggs.entrySet()) {
			portalSearchAggregation.addChildAggregation(
				_toPortalSearchAggregation(entry.getValue(), entry.getKey()));
		}

		return portalSearchAggregation;
	}

	private final Aggregations _aggregations;
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory;
	private final Configuration _configuration;
	private final Queries _queries;
	private final SearchRequestBuilder _searchRequestBuilder;

}