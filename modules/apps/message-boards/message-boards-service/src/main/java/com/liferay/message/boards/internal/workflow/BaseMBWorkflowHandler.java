/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.workflow;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseMBWorkflowHandler
	extends BaseWorkflowHandler<MBMessage> {

	@Override
	public MBMessage updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		MBMessageLocalService mbMessageLocalService =
			getMBMessageLocalService();

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		serviceContext.setAttribute(
			"workflowReviewComment", isWorkflowReviewComment(workflowContext));

		return mbMessageLocalService.updateStatus(
			userId, classPK, status, serviceContext, workflowContext);
	}

	protected abstract MBMessageLocalService getMBMessageLocalService();

	protected boolean isWorkflowReviewComment(
		Map<String, Serializable> workflowContext) {

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		String namespace = GetterUtil.getString(
			serviceContext.getAttribute("namespace"));

		for (String portletId : PORTLET_IDS) {
			if (namespace.contains(portletId)) {
				return true;
			}
		}

		return false;
	}

	protected static final String[] PORTLET_IDS = {
		"com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet",
		"com_liferay_portal_workflow_web_internal_portlet_UserWorkflowPortlet"
	};

}