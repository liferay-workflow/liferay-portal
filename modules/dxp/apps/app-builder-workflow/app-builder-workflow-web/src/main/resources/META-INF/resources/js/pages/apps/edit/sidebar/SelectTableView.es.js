/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import React from 'react';

import SelectDropdown from '../../../../components/select-dropdown/SelectDropdown.es';
import {OpenButton} from './DataAndViewsTab.es';

export default function SelectTableView({openButtonProps, ...props}) {
	props = {
		...props,
		emptyResultMessage: Liferay.Language.get(
			'no-table-views-were-found-with-this-name-try-searching-again-with-a-different-name'
		),
		label: Liferay.Language.get('select-a-table-view'),
		stateProps: {
			emptyProps: {
				label: Liferay.Language.get('there-are-no-table-views-yet'),
			},
			loadingProps: {
				label: Liferay.Language.get('retrieving-all-table-views'),
			},
		},
	};

	return (
		<div className="d-flex">
			<SelectDropdown {...props} />

			<OpenButton {...openButtonProps} />
		</div>
	);
}
