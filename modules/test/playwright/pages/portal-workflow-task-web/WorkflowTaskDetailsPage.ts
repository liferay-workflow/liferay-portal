/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForSuccessAlert} from '../../utils/waitForSuccessAlert';
import {WorkflowTasksPage} from './WorkflowTasksPage';

export class WorkflowTaskDetailsPage {
	readonly approveMenuItem: Locator;
	readonly assignee: FrameLocator;
	readonly assignToMenuItem: Locator;
	readonly comments: Locator;
	readonly subscribeButton: Locator;
	readonly doneAssigneeButton: Locator;
	readonly doneButton: Locator;
	readonly commentBox: Locator;
	readonly commentSectionButton: Locator;
	readonly page: Page;
	readonly rejectMenuItem: Locator;
	readonly reply: Locator;
	readonly viewButton: Locator;
	readonly reviewActionMenu: Locator;
	readonly reviewComment: Locator;
	readonly workflowTasksPage: WorkflowTasksPage;
	readonly detailsMessage: Locator;

	constructor(page: Page) {
		this.approveMenuItem = page.getByRole('menuitem', {name: 'approve'});
		this.assignToMenuItem = page.getByRole('link', {name: 'Assign to...'});
		this.doneAssigneeButton = page
			.frameLocator(
				'iframe[name="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_assignToDialog_iframe_"]'
			)
			.getByRole('button', {name: 'Done'});
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.rejectMenuItem = page.getByRole('menuitem', {name: 'reject'});
		this.reply = page.getByRole('button', {name: 'Reply'});
		this.reviewActionMenu = page.locator(
			'[id="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_kldx___menu"]'
		);
		this.viewButton = page.getByRole('link', {exact: true , name: 'View'});
		this.commentBox = page.frameLocator('iframe').getByRole('textbox');
		this.reviewComment = page.getByRole('textbox', {name: 'Comment'});
		this.page = page;
		this.workflowTasksPage = new WorkflowTasksPage(page);
		this.detailsMessage = page.getByLabel('Ask a user to work on the item.');
		this.comments = page.getByRole('button',{name: 'Comments'})
		this.subscribeButton = page.getByLabel('Subscribe to Comments');
		this.commentSectionButton = page.getByRole('button', {name: 'Comments'});
	}

	async clickDoneButton() {
		await this.doneButton.click();

		await waitForSuccessAlert(this.page);
	}

	async fillReviewComment(comment: string) {
		await this.commentBox.fill(comment);
	}

	async goTo(assetTitle: string) {
		await this.workflowTasksPage.goto();

		await this.selectAsset(assetTitle);
	}

	async selectAsset(assetTitle: string) {
		const assetLink = this.page.getByRole('link', {name: assetTitle});
		await assetLink.click({force: true});
	}

	async selectAssignee(assignee: string) {
		await this.page
			.frameLocator(
				'iframe[name="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_assignToDialog_iframe_"]'
			)
			.getByLabel('Assign to')
			.selectOption(assignee);
	}

	async clickDoneAssigneeButton() {
		await this.page
			.frameLocator(
				'iframe[name="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_assignToDialog_iframe_"]'
			)
			.getByRole('button', {name: 'Done'})
			.click();

		await waitForSuccessAlert(this.page);
	}
}
