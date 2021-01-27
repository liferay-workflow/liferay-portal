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
import React from 'react';

const IconWithPopover = ({
	children: content,
	header,
	popoverProps,
	show,
	trigger,
	...restProps
}) => {
	return (
		<ClayPopover
			alignPosition="left"
			disableScroll
			header={header}
			show={show}
			trigger={trigger}
			{...popoverProps}
			{...restProps}
		>
			{content}
		</ClayPopover>
	);
};

const TriggerIcon = ({iconProps, symbol, ...restProps}) => {
	return <ClayIcon symbol={symbol} {...iconProps} {...restProps} />;
};

IconWithPopover.TriggerIcon = TriggerIcon;
export default IconWithPopover;
