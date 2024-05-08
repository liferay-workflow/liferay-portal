/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.util;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.workflow.WorkflowException;

/**
 * @author Pedro Leite
 */
public class KaleoDefinitionThreadLocal {

	public static boolean isSkipKaleoDefinitionResourcePermission() {
		return _skipKaleoDefinitionResourcePermissionThreadLocal.get();
	}

	public static <T> T skipKaleoDefinitionResourcePermission(
			UnsafeSupplier<T, WorkflowException> unsafeSupplier)
		throws WorkflowException {

		try (SafeCloseable safeCloseable =
				_skipKaleoDefinitionResourcePermissionThreadLocal.
					setWithSafeCloseable(true)) {

			return unsafeSupplier.get();
		}
	}

	private static final CentralizedThreadLocal<Boolean>
		_skipKaleoDefinitionResourcePermissionThreadLocal =
			new CentralizedThreadLocal<>(
				KaleoDefinitionThreadLocal.class +
					"._skipKaleoDefinitionResourcePermissionThreadLocal",
				() -> Boolean.FALSE);

}