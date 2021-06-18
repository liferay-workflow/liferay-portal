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

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0;

import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.ProcessVersion;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessVersionResource;
import com.liferay.portal.workflow.metrics.search.index.ProcessWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.name.WorkflowMetricsIndexNameBuilder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/process-version.properties",
	scope = ServiceScope.PROTOTYPE, service = ProcessVersionResource.class
)
public class ProcessVersionResourceImpl extends BaseProcessVersionResourceImpl {

	@Override
	public Page<ProcessVersion> getProcessVersionsPage(Long processId)
		throws Exception {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			"version", "version");

		termsAggregation.setSize(10000);

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames(
			_taskWorkflowMetricsIndexNameBuilder.getIndexName(
				contextCompany.getCompanyId()));

		BooleanQuery filterBooleanQuery = _queries.booleanQuery();

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustNotQueryClauses(_queries.term("taskId", 0));

		searchSearchRequest.setQuery(
			filterBooleanQuery.addFilterQueryClauses(
				booleanQuery.addMustQueryClauses(
					_queries.term("companyId", contextCompany.getCompanyId()),
					_queries.term("completed", true),
					_queries.term("deleted", false),
					_queries.term("processId", processId))));

		searchSearchRequest.setSize(0);

		Map<String, ProcessVersion> processVersions = Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getAggregationResultsMap
		).map(
			aggregationResultsMap ->
				(TermsAggregationResult)aggregationResultsMap.get("version")
		).map(
			TermsAggregationResult::getBuckets
		).flatMap(
			Collection::stream
		).map(
			Bucket::getKey
		).map(
			version -> {
				ProcessVersion processVersion = new ProcessVersion();

				processVersion.setVersion(version);

				return processVersion;
			}
		).collect(
			LinkedHashMap::new,
			(map, processVersion) -> map.put(
				processVersion.getVersion(), processVersion),
			Map::putAll
		);

		return Page.of(processVersions.values());
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private ProcessWorkflowMetricsIndexer _processWorkflowMetricsIndexer;

	@Reference(target = "(workflow.metrics.index.entity.name=process)")
	private WorkflowMetricsIndexNameBuilder
		_processWorkflowMetricsIndexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestExecutor _searchRequestExecutor;

	@Reference(target = "(workflow.metrics.index.entity.name=task)")
	private WorkflowMetricsIndexNameBuilder
		_taskWorkflowMetricsIndexNameBuilder;

}