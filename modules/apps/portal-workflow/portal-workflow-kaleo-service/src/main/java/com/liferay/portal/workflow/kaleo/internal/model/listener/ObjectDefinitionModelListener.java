/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.internal.model.listener;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceUtil;
import com.liferay.portal.workflow.metrics.search.index.InstanceWorkflowMetricsIndexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Selton Guedes
 */
@Component(immediate = true, service = ModelListener.class)
public class ObjectDefinitionModelListener
	extends BaseModelListener<ObjectDefinition> {

	@Override
	public void onAfterUpdate(
		ObjectDefinition originalModel, ObjectDefinition model) {

		if (originalModel.getTitleObjectFieldId() !=
			model.getTitleObjectFieldId()) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					model.getObjectDefinitionId());

			BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

			Stream.of(
				_objectEntryService.getObjectEntries(
					objectDefinition.getObjectDefinitionId())
			).flatMap(
				List::stream
			).map(
				objectEntry -> {
					KaleoInstance instance =
						KaleoInstanceUtil.fetchByCN_CPK_First(
							objectDefinition.getClassName(),
							objectEntry.getObjectEntryId(), null);

					return _instanceWorkflowMetricsIndexer.
						createDefaultDocument(
							objectDefinition.isActive(),
							_createAssetTitleLocalizationMap(
								objectEntry.getModelClassName(),
								objectEntry.getObjectEntryId()),
							objectDefinition.getLabelMap(),
							objectDefinition.getCompanyId(),
							instance.getKaleoInstanceId(),
							objectDefinition.getModifiedDate());
				}
			).map(
				document -> new UpdateDocumentRequest(
					_instanceWorkflowMetricsIndexer.getIndexName(
						objectDefinition.getCompanyId()),
					document.getString("uid"), document) {

					{
						setType(_instanceWorkflowMetricsIndexer.getIndexType());
					}
				}
			).forEach(
				bulkDocumentRequest::addBulkableDocumentRequest
			);

			if (ListUtil.isNotEmpty(
				bulkDocumentRequest.getBulkableDocumentRequests())) {

				if (PortalRunMode.isTestMode()) {
					bulkDocumentRequest.setRefresh(true);
				}

				searchEngineAdapter.execute(bulkDocumentRequest);
			}
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(search.engine.impl=Elasticsearch)"
	)
	protected volatile SearchEngineAdapter searchEngineAdapter;

	private Map<Locale, String> _createAssetTitleLocalizationMap(
		String className, long classPK) {

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(className);

		if (workflowHandler != null) {
			Map<Locale, String> localizationMap = new HashMap<>();

			for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(0L)) {

				localizationMap.put(
					availableLocale,
					workflowHandler.getTitle(classPK, availableLocale));
			}

			return localizationMap;
		}

		return Collections.emptyMap();
	}

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}