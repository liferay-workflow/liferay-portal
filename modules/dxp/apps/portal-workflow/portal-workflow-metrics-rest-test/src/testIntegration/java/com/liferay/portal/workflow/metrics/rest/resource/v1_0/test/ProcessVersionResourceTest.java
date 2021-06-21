/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ProcessVersion;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ProcessVersionResourceTest
	extends BaseProcessVersionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());

		_tasks = new ArrayList<>();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_process != null) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), _process);
		}

		if (!ListUtil.isEmpty(_tasks)) {
			_workflowMetricsRESTTestHelper.deleteTasks(
				testGroup.getCompanyId(), _process.getId());
		}
	}

	@Override
	@Test
	public void testGetProcessVersionsPage() throws Exception {
		Page<ProcessVersion> page =
			processVersionResource.getProcessVersionsPage(_process.getId());

		Assert.assertEquals(0, page.getTotalCount());

		Instance instance1 = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), _process.getId());

		_addTask(instance1, _process.getVersion());

		_process.setVersion("2.0");

		_workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process);

		_process.setVersion("3.0");

		_process = _workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process);

		Instance instance2 = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), _process.getId());

		_addTask(instance2, _process.getVersion());

		page = processVersionResource.getProcessVersionsPage(_process.getId());

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new ProcessVersion() {
					{
						version = "1.0";
					}
				},
				new ProcessVersion() {
					{
						version = "3.0";
					}
				}),
			(List<ProcessVersion>)page.getItems());
	}

	@Override
	@Test
	public void testGraphQLGetProcessVersionsPage() throws Exception {
		Assert.assertTrue(true);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"version"};
	}

	private void _addTask(Instance instance, String processVersion)
		throws Exception {

		Task task = _workflowMetricsRESTTestHelper.addTask(
			new Assignee() {
				{
					id = TestPropsValues.getUserId();
				}
			},
			testGroup.getCompanyId(), 10000L, instance,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			instance.getProcessId(), RandomTestUtil.randomLong(),
			TestPropsValues.getUser(), processVersion);

		_tasks.add(task);
	}

	private Process _process;
	private List<Task> _tasks;

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}