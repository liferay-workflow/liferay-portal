<%--
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
--%>

<%@ include file="/init.jsp" %>

<span class="taglib-workflow-status">
	<c:if test="<%= Validator.isNotNull(WorkflowStatusTaglibUtil.getId(request)) %>">
		<span class="mr-2 workflow-id">
			<span class="workflow-label"><liferay-ui:message key="id" />:</span>

			<span class="workflow-value"><%= HtmlUtil.escape(WorkflowStatusTaglibUtil.getId(request)) %></span>
		</span>
	</c:if>

	<c:if test="<%= Validator.isNotNull(WorkflowStatusTaglibUtil.getVersion(request)) %>">
		<span class="mr-2 workflow-version">
			<span class="workflow-label"><liferay-ui:message key="version" />:</span>

			<span class="workflow-value"><%= WorkflowStatusTaglibUtil.getVersion(request) %></span>
		</span>
	</c:if>

	<span class="workflow-status">
		<c:if test="<%= WorkflowStatusTaglibUtil.isShowLabel(request) %>">
			<span class="workflow-label"><liferay-ui:message key="status" />:</span>
		</c:if>

		<span class="label label-<%= WorkflowConstants.getStatusStyle(WorkflowStatusTaglibUtil.getStatus(request)) %> status workflow-status-<%= WorkflowConstants.getStatusLabel(WorkflowStatusTaglibUtil.getStatus(request)) %> <%= WorkflowConstants.getStatusCssClass(WorkflowStatusTaglibUtil.getStatus(request)) %> workflow-value">
			<liferay-ui:message key="<%= WorkflowStatusTaglibUtil.getStatusMessage(request) %>" /><%= WorkflowStatusTaglibUtil.getInstanceStatus(request, resourceBundle) %>
		</span>
	</span>

	<c:if test="<%= WorkflowStatusTaglibUtil.isShowHelpMessage(request) && Validator.isNotNull(WorkflowStatusTaglibUtil.getHelpMessage(request)) %>">
		<liferay-ui:icon-help message="<%= WorkflowStatusTaglibUtil.getHelpMessage(request) %>" />
	</c:if>
</span>