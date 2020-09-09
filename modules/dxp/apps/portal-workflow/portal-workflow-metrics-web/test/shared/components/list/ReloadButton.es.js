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

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import ReloadButton from '../../../../src/main/resources/META-INF/resources/js/shared/components/list/ReloadButton.es';

describe('ReloadButton', () => {
	test('should call "reload" function when clicked', () => {
		Object.defineProperty(window, 'location', {
			value: {reload: jest.fn()},
			writable: true,
		});

		const {getByText} = render(<ReloadButton />);
		const reloadButton = getByText('reload-page');

		fireEvent.click(reloadButton);

		expect(window.location.reload).toHaveBeenCalled();
	});
});
