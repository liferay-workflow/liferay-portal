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

package com.liferay.portal.workflow.metrics.web.internal.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Locale;

/**
 * @author Feliphe Marinho
 */
public class WorkflowMetricsUtil {

	public static String getTimeFormat(Locale locale) {
		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat)DateFormat.getTimeInstance(
				DateFormat.SHORT, locale);

		return simpleDateFormat.toPattern();
	}

}