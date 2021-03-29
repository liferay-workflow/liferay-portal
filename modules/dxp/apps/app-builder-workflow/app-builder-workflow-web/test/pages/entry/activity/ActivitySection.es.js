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
import {
	fireEvent,
	render,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import moment from 'moment';
import React from 'react';

import ActivitySection from '../../../../src/main/resources/META-INF/resources/js/pages/entry/activity/ActivitySection.es';

const TODAY = `${moment(new Date()).format('YYYY-MM-DDThh:mm:ss')}Z`;

const workflowLogs = {
	actions: {},
	facets: [],
	items: [
		{
			auditPerson: {
				additionalName: '',
				contentType: 'UserAccount',
				familyName: 'Test',
				givenName: 'Test',
				id: 20129,
				name: 'Test Test',
				profileURL: '/web/test',
			},
			commentLog: 'Assigned initial task.',
			dateCreated: TODAY,
			description: 'Task initially assigned to the User role.',
			id: 41756,
			previousState: '',
			role: {
				availableLanguages: ['en-US'],
				dateCreated: TODAY,
				dateModified: TODAY,
				description:
					'Authenticated users should be assigned this role.',
				id: 20113,
				name: 'User',
				roleType: 'regular',
			},
			state: 'Step 1',
			type: 'TaskAssign',
			workflowTaskId: 41751,
		},
		{
			auditPerson: {
				additionalName: '',
				contentType: 'UserAccount',
				familyName: 'Test',
				givenName: 'Test',
				id: 20129,
				name: 'Test Test',
				profileURL: '/web/test',
			},
			commentLog: 'Assigned initial task.',
			dateCreated: '2021-03-25T19:21:31Z',
			description: 'Task initially assigned to the Administrator role.',
			id: 41755,
			previousState: '',
			role: {
				availableLanguages: ['en-US'],
				dateCreated: '2021-03-25T19:16:39Z',
				dateModified: '2021-03-25T19:16:39Z',
				description:
					'Administrators are super users who can do anything.',
				id: 20108,
				name: 'Administrator',
				roleType: 'regular',
			},
			state: 'Step 1',
			type: 'TaskAssign',
			workflowTaskId: 41751,
		},
	],
	lastPage: -2,
	page: -1,
	pageSize: -1,
	totalCount: 2,
};

const workflowLogsError = {};

const mockGetItem = jest
	.fn()
	.mockRejectedValueOnce(workflowLogsError)
	.mockResolvedValueOnce(workflowLogs);

jest.mock('data-engine-js-components-web/js/utils/client.es', () => ({
	getItem: () => mockGetItem(),
}));

describe('ActivitySection', () => {
	it('render activity section force error', async () => {
		const {baseElement} = render(
			<ActivitySection workflowInstanceId={99999} />
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(
			baseElement.querySelector('.activity-collapsable')
		).not.toBeInTheDocument();
	});

	it('render activity section', async () => {
		const {baseElement, getByText} = render(
			<ActivitySection workflowInstanceId={41745} />
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(
			baseElement.querySelector('.activity-collapsable')
		).toBeInTheDocument();

		expect(getByText('March 25, 2021')).toBeInTheDocument();

		expect(getByText('today')).toBeInTheDocument();

		const activities = baseElement.querySelectorAll('div.activity');

		expect(activities.length).toBe(2);

		const collapsed = () =>
			baseElement.querySelector(
				'.activity-collapsable__content.collapsed'
			);

		expect(collapsed()).not.toBeInTheDocument();

		await fireEvent.click(
			baseElement.querySelector('.activity-collapsable__header')
		);

		expect(collapsed()).toBeInTheDocument();
	});
});
