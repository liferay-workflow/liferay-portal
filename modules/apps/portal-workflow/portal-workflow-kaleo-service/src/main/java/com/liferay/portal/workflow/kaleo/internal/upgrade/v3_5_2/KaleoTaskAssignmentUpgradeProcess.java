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

package com.liferay.portal.workflow.kaleo.internal.upgrade.v3_5_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.workflow.kaleo.definition.AssignmentType;

/**
 * @author Ricardo Couso
 */
public class KaleoTaskAssignmentUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update KaleoTaskAssignment set assigneeScriptCacheable = ",
				"[$FALSE$], assigneeScriptCacheDuration = 0 where ",
				"assigneeClassName = '", AssignmentType.SCRIPT.name(),
				"' and assigneeScriptCacheable is null and ",
				"assigneeScriptCacheDuration is null"));
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"KaleoTaskAssignment", "assigneeScriptCacheable BOOLEAN",
				"assigneeScriptCacheDuration INTEGER")
		};
	}

}