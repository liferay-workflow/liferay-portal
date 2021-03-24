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

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import MissingRequiredFieldsPopover from '../../../../src/main/resources/META-INF/resources/js/pages/apps/edit/MissingRequiredFieldsPopover.es';

const HELP_CURSOR = 'help-cursor';

const MissingRequiredFieldsPopoverWrapper = ({
	message,
	nativeField,
	triggerClassName = HELP_CURSOR,
}) => {
	const [showPopover, setShowPopover] = useState(false);

	return (
		<MissingRequiredFieldsPopover
			dataObjectName="TEST"
			message={message}
			nativeField={nativeField}
			onClick={() => setShowPopover(false)}
			setShowPopover={setShowPopover}
			showPopover={showPopover}
			triggerClassName={triggerClassName}
		/>
	);
};

describe('MissingRequiredFieldsPopover', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('render missing required custom fields', () => {
		const {baseElement, getByText} = render(
			<MissingRequiredFieldsPopoverWrapper />
		);

		act(() => {
			jest.runAllTimers();
		});

		const helpCursor = baseElement.querySelectorAll(`.${HELP_CURSOR}`)[1];

		fireEvent.mouseEnter(helpCursor);

		let popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).not.toBeNull();
		expect(getByText('edit-form-view')).toBeEnabled();

		fireEvent.mouseLeave(helpCursor);

		act(() => {
			jest.advanceTimersByTime(1000);
		});

		popoverElement = baseElement.querySelector('.popover');
		expect(popoverElement).toBeNull();

		fireEvent.mouseEnter(helpCursor);

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).not.toBeNull();

		fireEvent.mouseEnter(popoverElement);

		fireEvent.mouseLeave(popoverElement);

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeNull();
	});

	it('render missing required native fields', () => {
		const {baseElement, getByText} = render(
			<MissingRequiredFieldsPopoverWrapper nativeField={true} />
		);

		act(() => {
			jest.runAllTimers();
		});

		const helpCursor = baseElement.querySelectorAll(`.${HELP_CURSOR}`)[1];

		fireEvent.mouseEnter(helpCursor);

		let popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).not.toBeNull();
		expect(getByText('edit-form-view')).toBeEnabled();

		fireEvent.mouseLeave(helpCursor);

		act(() => {
			jest.advanceTimersByTime(1000);
		});

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeNull();
	});

	it('render missing required fields without triggerClassName', () => {
		const {baseElement, getByText} = render(
			<MissingRequiredFieldsPopoverWrapper triggerClassName={null} />
		);

		act(() => {
			jest.runAllTimers();
		});

		const helpCursor = baseElement.querySelectorAll(`.${HELP_CURSOR}`)[1];

		fireEvent.mouseEnter(helpCursor);

		let popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).not.toBeNull();
		expect(getByText('edit-form-view')).toBeEnabled();

		fireEvent.mouseLeave(helpCursor);

		act(() => {
			jest.advanceTimersByTime(1000);
		});

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeNull();
	});
});
