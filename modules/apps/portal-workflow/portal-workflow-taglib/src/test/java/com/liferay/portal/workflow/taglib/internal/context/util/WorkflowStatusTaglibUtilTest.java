/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.taglib.internal.context.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

/**
 * @author Feliphe Marinho
 */
@RunWith(Enclosed.class)
public class WorkflowStatusTaglibUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public static class NonParameterizedTest {

		@Test
		public void testGetStatus() {
			HttpServletRequest httpServletRequest = Mockito.mock(
				HttpServletRequest.class);

			BeanProperties beanProperties = Mockito.mock(BeanProperties.class);

			Mockito.when(
				beanProperties.getInteger(
					Mockito.anyObject(), Mockito.eq("status"))
			).thenReturn(
				2
			);

			ReflectionTestUtil.setFieldValue(
				BeanPropertiesUtil.class, "_beanProperties", beanProperties);

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.BEAN, httpServletRequest,
				Mockito.mock(Object.class));

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.STATUS, httpServletRequest,
				1);

			Assert.assertEquals(
				Integer.valueOf(2),
				WorkflowStatusTaglibUtil.getStatus(httpServletRequest));

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.BEAN, httpServletRequest,
				null);

			Assert.assertEquals(
				Integer.valueOf(1),
				WorkflowStatusTaglibUtil.getStatus(httpServletRequest));
		}

		@Test
		public void testGetStatusMessage() {
			HttpServletRequest httpServletRequest = Mockito.mock(
				HttpServletRequest.class);

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.STATUS_MESSAGE,
				httpServletRequest, "Status Message");

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.STATUS, httpServletRequest,
				2);

			Assert.assertEquals(
				"Status Message",
				WorkflowStatusTaglibUtil.getStatusMessage(httpServletRequest));

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.STATUS_MESSAGE,
				httpServletRequest, StringPool.BLANK);

			Assert.assertEquals(
				WorkflowConstants.LABEL_DRAFT,
				WorkflowStatusTaglibUtil.getStatusMessage(httpServletRequest));
		}

	}

	@RunWith(Parameterized.class)
	public static class SetInstanceStatusTest {

		@Parameterized.Parameters(
			name = "bean={0}, expectedInstanceStatus={1}, model={2}, showInstanceStatus={3}, status={4}"
		)
		public static List<Object[]> data() {
			return Arrays.asList(
				new Object[][] {
					{null, StringPool.BLANK, null, false, 0},
					{
						Mockito.mock(Object.class), StringPool.BLANK, null,
						false, 0
					},
					{null, StringPool.BLANK, null, true, 0},
					{null, StringPool.BLANK, Object.class, false, 0},
					{null, StringPool.BLANK, null, false, 1},
					{
						Mockito.mock(Object.class), " (review)",
						WorkflowStatusTaglibUtilTest.class, true, 1
					}
				});
		}

		@BeforeClass
		public static void setUpClass() throws Exception {
			WorkflowInstanceLinkLocalService workflowInstanceLinkLocalService =
				Mockito.mock(WorkflowInstanceLinkLocalService.class);

			ReflectionTestUtil.setFieldValue(
				WorkflowInstanceLinkLocalServiceUtil.class, "_service",
				workflowInstanceLinkLocalService);

			Mockito.when(
				workflowInstanceLinkLocalService.getState(
					Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
					Mockito.anyLong())
			).thenReturn(
				"review"
			);

			Language language = Mockito.mock(Language.class);

			Mockito.when(
				language.get(
					Mockito.any(ResourceBundle.class), Mockito.eq("review"))
			).thenReturn(
				"review"
			);

			ReflectionTestUtil.setFieldValue(
				LanguageUtil.class, "_language", language);
		}

		@Test
		public void test() {
			HttpServletRequest httpServletRequest = Mockito.mock(
				HttpServletRequest.class);

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.BEAN, httpServletRequest,
				bean);

			if (bean != null) {
				BeanProperties beanProperties = Mockito.mock(
					BeanProperties.class);

				Mockito.when(
					beanProperties.getLong(
						Mockito.anyObject(), Mockito.eq("companyId"))
				).thenReturn(
					1L
				);

				Mockito.when(
					beanProperties.getLong(
						Mockito.anyObject(), Mockito.eq("groupId"))
				).thenReturn(
					1L
				);

				Mockito.when(
					beanProperties.getLong(
						Mockito.anyObject(), Mockito.eq("primaryKey"))
				).thenReturn(
					1L
				);

				Mockito.when(
					beanProperties.getInteger(
						Mockito.anyObject(), Mockito.eq("status"))
				).thenReturn(
					1
				);

				ReflectionTestUtil.setFieldValue(
					BeanPropertiesUtil.class, "_beanProperties",
					beanProperties);
			}

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.MODEL, httpServletRequest,
				model);

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.SHOW_INSTANCE_STATUS,
				httpServletRequest, showInstanceStatus);

			WorkflowStatusTaglibUtilTest.mockAttribute(
				WorkflowStatusTaglibUtil.Attribute.STATUS, httpServletRequest,
				status);

			Assert.assertEquals(
				expectedInstanceStatus,
				WorkflowStatusTaglibUtil.getInstanceStatus(
					httpServletRequest, Mockito.mock(ResourceBundle.class)));
		}

		@Parameterized.Parameter
		public Object bean;

		@Parameterized.Parameter(1)
		public String expectedInstanceStatus;

		@Parameterized.Parameter(2)
		public Class<?> model;

		@Parameterized.Parameter(3)
		public boolean showInstanceStatus;

		@Parameterized.Parameter(4)
		public Integer status;

	}

	protected static void mockAttribute(
		WorkflowStatusTaglibUtil.Attribute attribute,
		HttpServletRequest httpServletRequest, Object value) {

		Mockito.when(
			httpServletRequest.getAttribute(
				Mockito.eq(attribute.getReference()))
		).thenReturn(
			value
		);
	}

}