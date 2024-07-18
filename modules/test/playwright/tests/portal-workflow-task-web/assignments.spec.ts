/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {workflowPagesTest} from '../../fixtures/workflowPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {blogsPagesTest} from '../blogs-web/fixtures/blogsPagesTest';
import {messageBoardsPagesTest} from '../../fixtures/messageBoardsTest';
import { isolatedSiteTest } from '../../fixtures/isolatedSiteTest';
import {userPersonalBarPagesTest} from '../../fixtures/userPersonalBarPagesTest'

export const test = mergeTests(
	isolatedSiteTest,
	apiHelpersTest,
	blogsPagesTest,
	loginTest(),
	workflowPagesTest,
	messageBoardsPagesTest,
	userPersonalBarPagesTest
);

let assetType: string;
let blogTitle: string;
let workflowDefinitionId: number;
let workflowDefinitionName: string;
let workflowXMLDefinition: string;

test.afterEach(
	async ({
		apiHelpers,
		blogsPage,
		configurationTabPage,
		processBuilderPage,
	}) => {
		if (assetType && workflowDefinitionName) {
			await processBuilderPage.goto();

			await configurationTabPage.goTo();

			await configurationTabPage.unassignWorkflowFromAssetType(assetType);
		}

		if (blogTitle) {
			await blogsPage.goto();
			await blogsPage.deleteAllBlogEntries();
		}

		if (workflowDefinitionId) {
			await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
				workflowDefinitionId
			);
		}

		assetType = null;
		blogTitle = null;
		workflowDefinitionId = null;
		workflowDefinitionName = null;
		workflowXMLDefinition = null;
	}
);

test('send user back to my workflow tasks page after assign another user to review', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	blogsPage,
	configurationTabPage,
	diagramViewPage,
	page,
	processBuilderPage,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	workflowDefinitionName = 'Workflow Definition' + getRandomString();

	workflowXMLDefinition = readFileSync(
		__dirname +
			'/dependencies/administrator-role-assignments-workflow-definition.xml',
		'utf-8'
	);

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			{content: workflowXMLDefinition}
		);

	workflowDefinitionId = workflowDefinition.id;

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.company.admin@liferay.com'
		);

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.publishWorkflowDefinition();

	await diagramViewPage.goBack();

	await configurationTabPage.goTo();

	assetType = 'Blogs Entry';

	await configurationTabPage.assignWorkflowToAssetType(
		workflowDefinitionName,
		assetType
	);

	await blogsPage.goto();

	await blogsPage.goToCreateBlogEntry();

	blogTitle = 'Blog Title' + getRandomInt();

	await blogsEditBlogEntryPage.editBlogEntry({
		content: 'Blog content.',
		submitToWorkflow: true,
		title: blogTitle,
	});

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTaskDetailsPage.selectAsset(blogTitle);

	await page.waitForTimeout(3000);

	await workflowTaskDetailsPage.reviewActionMenu.click();

	await workflowTaskDetailsPage.assignToMenuItem.click();

	await page.waitForLoadState('networkidle');

	const user2 =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	await workflowTaskDetailsPage.selectAssignee(user2.id.toString());

	await workflowTaskDetailsPage.doneAssigneeButton.click();

	await expect(workflowTasksPage.assignedToMyRolesLink).toBeVisible();
});

test('user must be able to read workflow task from a notification if contained on the flow', async ({
	apiHelpers,
	configurationTabPage,
	diagramViewPage,
	messageBoardsEditThreadPage,
	messageBoardsWidgetPage,
	page,
	processBuilderPage,
	site,
	userPersonalBarPage,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	let user = await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
		'demo.unprivileged@liferay.com'
	);

	let defaultUser = await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
		'test@liferay.com'
	);

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'workflowTaskManagement' + getRandomInt(),
		rolePermissions: [
			{
			  "actionIds": [
				"PERMISSIONS",
				"DELETE",
				"ADD_FILE",
				"REPLY_TO_MESSAGE",
				"LOCK_THREAD",
				"UPDATE",
				"VIEW",
				"SUBSCRIBE",
				"ADD_MESSAGE",
				"MOVE_THREAD",
				"ADD_SUBCATEGORY",
				"UPDATE_THREAD_PRIORITY"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.message.boards.model.MBCategory",
			  "scope": 1
			},
			{
			  "actionIds": [
				"DELETE",
				"PERMISSIONS",
				"VIEW",
				"SUBSCRIBE"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.message.boards.model.MBThread",
			  "scope": 1
			},
			{
			  "actionIds": [
				"CONFIG",
				"PERMISSIONS",
				"PREFERENCES",
				"CONFIGURATION",
				"ACCESS_IN_CONTROL_PANEL",
				"VIEW"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com_liferay_message_boards_web_portlet_MBAdminPortlet",
			  "scope": 1
			},
			{
			  "actionIds": [
				"VIEW_SITE_ADMINISTRATION"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.depot.model.DepotEntry",
			  "scope": 1
			},
			{
			  "actionIds": [
				"VIEW_SITE_ADMINISTRATION"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.portal.kernel.model.Group",
			  "scope": 1
			},
			{
			  "actionIds": [
				"DELETE",
				"PERMISSIONS",
				"UPDATE",
				"VIEW",
				"SUBSCRIBE"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.message.boards.model.MBMessage",
			  "scope": 1
			},
			{
			  "actionIds": [
				"PERMISSIONS",
				"ADD_FILE",
				"BAN_USER",
				"ADD_CATEGORY",
				"REPLY_TO_MESSAGE",
				"LOCK_THREAD",
				"VIEW",
				"SUBSCRIBE",
				"ADD_MESSAGE",
				"MOVE_THREAD",
				"UPDATE_THREAD_PRIORITY"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com.liferay.message.boards",
			  "scope": 1
			},
			{
			  "actionIds": [
				"PERMISSIONS",
				"PREFERENCES",
				"CONFIGURATION",
				"VIEW",
				"ADD_TO_PAGE"
			  ],
			  "primaryKey": "0",
			  "resourceName": "com_liferay_message_boards_web_portlet_MBPortlet",
			  "scope": 1
			}
		  ]
		  ,
		roleType: 'regular'
	});

	await apiHelpers.headlessAdminUser.assignUserToRole(role.name, user.id);

	workflowDefinitionName = 'Workflow Definition' + getRandomInt();
	workflowXMLDefinition = readFileSync(
		__dirname +
			'/dependencies/administrator-role-assignments-workflow-definition.xml',
		'utf-8'
	);

	await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
		workflowDefinitionName,
		{content: workflowXMLDefinition}
	);

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.publishWorkflowDefinition();

	await diagramViewPage.goBack();

	await configurationTabPage.goTo();

	assetType = 'Message Boards Message';

	await configurationTabPage.assignWorkflowToAssetType(
		workflowDefinitionName,
		assetType
	);

	await page.goto(`/web/${site.name}`);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	let threadTitle = "Thread Title" + getRandomInt();

	let threadSubject = "Thread Subject" + getRandomInt();

	await messageBoardsEditThreadPage.publishNewBasicThread(threadTitle,threadSubject);

	await performLogout(page);

	await performLogin(page, defaultUser.alternateName);

	await page.goto(`/web/${site.name}`);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(threadTitle);

	await workflowTasksPage.reject(threadTitle);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	await userPersonalBarPage.notificationBadge.click();

	await page.getByRole('link', {
		name: `Your submission was rejected by ${defaultUser.name}, please modify and resubmit.`,
	}).first().click()

	await workflowTaskDetailsPage.commentSectionButton.click()

	await workflowTaskDetailsPage.subscribeButton.click()

	await performLogout(page);

	await performLogin(page, defaultUser.alternateName);

	await workflowTasksPage.goto();

	await workflowTaskDetailsPage.selectAsset(threadTitle);

	await workflowTaskDetailsPage.commentSectionButton.click()

	await page.waitForTimeout(1000);

	await workflowTaskDetailsPage.subscribeButton.click()

	await page.waitForTimeout(1000);

	await workflowTaskDetailsPage.commentSectionButton.click()

	await workflowTaskDetailsPage.fillReviewComment("Random");

	await workflowTaskDetailsPage.reply.click();

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await userPersonalBarPage.notificationBadge.click();

	await page.getByRole('link', {
			name: `${defaultUser.name} added a new comment to ${threadTitle}.`
	}).click();

	 expect(
		workflowTaskDetailsPage.viewButton.isVisible()
	).toBeTruthy();

	 expect(
			workflowTaskDetailsPage.detailsMessage.isVisible()
	).toBeTruthy();
});