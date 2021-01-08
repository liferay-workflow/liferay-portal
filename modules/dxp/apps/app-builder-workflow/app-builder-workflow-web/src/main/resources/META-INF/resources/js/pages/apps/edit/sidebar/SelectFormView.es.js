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

import ClayIcon from '@clayui/icon';
import ClayPopover from '@clayui/popover';
import EditAppContext from 'app-builder-web/js/pages/apps/edit/EditAppContext.es';
import {sub} from 'app-builder-web/js/utils/lang.es';
import React, {useContext, useState} from 'react';

import SelectDropdown from '../../../../components/select-dropdown/SelectDropdown.es';
import {OpenButton} from './DataAndViewsTab.es';

function SelectFormView({openButtonProps, ...props}) {
	props = {
		...props,
		emptyResultMessage: Liferay.Language.get(
			'no-form-views-were-found-with-this-name-try-searching-again-with-a-different-name'
		),
		label: Liferay.Language.get('select-a-form-view'),
		stateProps: {
			emptyProps: {
				label: Liferay.Language.get('there-are-no-form-views-yet'),
			},
			loadingProps: {
				label: Liferay.Language.get('retrieving-all-form-views'),
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

const Item = ({missingRequiredFields, name}) => {
	const {
		config: {dataObject},
	} = useContext(EditAppContext);
	const [showPopover, setShowPopover] = useState(false);

	return (
		<>
			<span
				className="float-left text-left text-truncate w50"
				title={name}
			>
				{name}
			</span>

			{missingRequiredFields && (
				<ClayPopover
					alignPosition="left"
					disableScroll
					header={
						<>
							<ClayIcon
								className="mr-1 text-info"
								symbol="info-circle"
							/>

							<span>
								{Liferay.Language.get(
									'missing-required-fields'
								)}
							</span>
						</>
					}
					show={showPopover}
					trigger={
						<ClayIcon
							className="dropdown-button-asset float-right help-cursor text-info"
							onMouseOut={() => setShowPopover(false)}
							onMouseOver={() => setShowPopover(true)}
							symbol="info-circle"
						/>
					}
				>
					{sub(
						Liferay.Language.get(
							'this-form-view-does-not-contain-all-required-fields-for-the-x-object'
						),
						[dataObject.name]
					)}
				</ClayPopover>
			)}
		</>
	);
};

SelectFormView.Item = Item;

export default SelectFormView;
