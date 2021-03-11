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
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';
import {Handle} from 'react-flow-renderer';

export default function CreateNode({data: {addStep}}) {
	return (
		<div className="arrow">
			<Handle
				position="bottom"
				style={{left: '50%', visibility: 'hidden'}}
				type="source"
			/>

			<ClayTooltipProvider>
				<div
					className="arrow-plus-button"
					data-tooltip-align="left"
					data-tooltip-delay="0"
					onClick={addStep}
					title={Liferay.Language.get('create-new-step')}
				>
					<ClayIcon className="icon" symbol="plus" />
				</div>
			</ClayTooltipProvider>

			<Handle
				position="top"
				style={{left: '50%', visibility: 'hidden'}}
				type="target"
			/>
		</div>
	);
}
