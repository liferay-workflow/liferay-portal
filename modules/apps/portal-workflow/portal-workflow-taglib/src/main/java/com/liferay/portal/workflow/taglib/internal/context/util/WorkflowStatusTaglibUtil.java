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

package com.liferay.portal.workflow.taglib.internal.context.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Feliphe Marinho
 */
public class WorkflowStatusTaglibUtil {

	public static final String ATTRIBUTE_NAMESPACE =
		"liferay-portal-workflow:status:";

	public static String getHelpMessage(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(
			httpServletRequest.getAttribute(
				Attribute.HELP_MESSAGE.getReference()));
	}

	public static String getId(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(
			(String)httpServletRequest.getAttribute(
				Attribute.ID.getReference()));
	}

	public static String getInstanceStatus(
		HttpServletRequest httpServletRequest, ResourceBundle resourceBundle) {

		Object bean = _getBean(httpServletRequest);
		Class<?> model = _getModel(httpServletRequest);

		if (Objects.equals(
				getStatus(httpServletRequest),
				WorkflowConstants.STATUS_PENDING) &&
			(bean != null) && (model != null) &&
			_isShowInstanceStatus(httpServletRequest)) {

			try {
				String workflowStatus =
					WorkflowInstanceLinkLocalServiceUtil.getState(
						BeanPropertiesUtil.getLong(bean, "companyId"),
						BeanPropertiesUtil.getLong(bean, "groupId"),
						model.getName(),
						BeanPropertiesUtil.getLong(bean, "primaryKey"));

				return StringBundler.concat(
					StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
					HtmlUtil.escape(
						LanguageUtil.get(resourceBundle, workflowStatus)),
					StringPool.CLOSE_PARENTHESIS);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException.getMessage(), portalException);
				}
			}
		}

		return StringPool.BLANK;
	}

	public static Integer getStatus(HttpServletRequest httpServletRequest) {
		Object bean = _getBean(httpServletRequest);

		if (bean != null) {
			return BeanPropertiesUtil.getInteger(bean, "status");
		}

		return GetterUtil.getInteger(
			String.valueOf(
				httpServletRequest.getAttribute(
					Attribute.STATUS.getReference())));
	}

	public static String getStatusMessage(
		HttpServletRequest httpServletRequest) {

		if (Validator.isNotNull(
				GetterUtil.getString(
					(String)httpServletRequest.getAttribute(
						Attribute.STATUS_MESSAGE.getReference())))) {

			return GetterUtil.getString(
				(String)httpServletRequest.getAttribute(
					Attribute.STATUS_MESSAGE.getReference()));
		}

		return WorkflowConstants.getStatusLabel(getStatus(httpServletRequest));
	}

	public static String getVersion(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(
			(String)httpServletRequest.getAttribute(
				Attribute.VERSION.getReference()));
	}

	public static boolean isShowHelpMessage(
		HttpServletRequest httpServletRequest) {

		return GetterUtil.getBoolean(
			String.valueOf(
				httpServletRequest.getAttribute(
					Attribute.SHOW_HELP_MESSAGE.getReference())),
			true);
	}

	public static boolean isShowLabel(HttpServletRequest httpServletRequest) {
		return GetterUtil.getBoolean(
			String.valueOf(
				httpServletRequest.getAttribute(
					Attribute.SHOW_LABEL.getReference())),
			true);
	}

	public enum Attribute {

		BEAN("bean"), HELP_MESSAGE("helpMessage"), ID("id"),
		MARKUP_VIEW("markupView"), MODEL("model"),
		SHOW_HELP_MESSAGE("showHelpMessage"),
		SHOW_INSTANCE_STATUS("showInstanceStatus"), SHOW_LABEL("showLabel"),
		STATUS("status"), STATUS_MESSAGE("statusMessage"), VERSION("version");

		public String getReference() {
			return ATTRIBUTE_NAMESPACE + _value;
		}

		public String getValue() {
			return _value;
		}

		private Attribute(String value) {
			_value = value;
		}

		private final String _value;

	}

	private static Object _getBean(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getAttribute(Attribute.BEAN.getReference());
	}

	private static Class<?> _getModel(HttpServletRequest httpServletRequest) {
		return (Class<?>)httpServletRequest.getAttribute(
			Attribute.MODEL.getReference());
	}

	private static boolean _isShowInstanceStatus(
		HttpServletRequest httpServletRequest) {

		return GetterUtil.getBoolean(
			String.valueOf(
				httpServletRequest.getAttribute(
					Attribute.SHOW_INSTANCE_STATUS.getReference())));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowStatusTaglibUtil.class);

}