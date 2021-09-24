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

package com.liferay.search.experiences.internal.blueprint.search.request;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.highlight.FieldConfigBuilder;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilder;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.internal.blueprint.parser.SXPTemplateVariableParser;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;

/**
 * @author Petteri Karttunen
 */
public class HighlightSearchRequestBodyContributor
	implements SearchRequestBodyContributor {

	public HighlightSearchRequestBodyContributor(
		FieldConfigBuilderFactory fieldConfigBuilderFactory,
		HighlightBuilderFactory highlightBuilderFactory) {

		_fieldConfigBuilderFactory = fieldConfigBuilderFactory;
		_highlightBuilderFactory = highlightBuilderFactory;
	}

	@Override
	public void contribute(
		SearchRequestBuilder searchRequestBuilder, SXPBlueprint sxpBlueprint,
		SXPParameterData sxpParameterData) {

		// TODO Replace with real JSON

		JSONObject jsonObject = SXPTemplateVariableParser.parse(
			JSONUtil.put("test", "test"), sxpParameterData);

		if (jsonObject == null) {
			return;
		}

		HighlightBuilder highlightBuilder = _highlightBuilderFactory.builder();

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		if (fieldsJSONObject != null) {
			for (String key : fieldsJSONObject.keySet()) {
				JSONObject fieldJSONObject = fieldsJSONObject.getJSONObject(
					key);

				FieldConfigBuilder fieldConfigBuilder =
					_fieldConfigBuilderFactory.builder(key);

				if (fieldJSONObject.has("fragment_offset")) {
					fieldConfigBuilder.fragmentOffset(
						fieldJSONObject.getInt("fragment_offset"));
				}

				if (fieldJSONObject.has("fragment_size")) {
					fieldConfigBuilder.fragmentSize(
						fieldJSONObject.getInt("fragment_size"));
				}

				if (fieldJSONObject.has("number_of_fragments")) {
					fieldConfigBuilder.numFragments(
						fieldJSONObject.getInt("number_of_fragments"));
				}

				highlightBuilder.addFieldConfig(fieldConfigBuilder.build());
			}
		}

		if (jsonObject.has("fragment_size")) {
			highlightBuilder.fragmentSize(jsonObject.getInt("fragment_size"));
		}

		if (jsonObject.has("number_of_fragments")) {
			highlightBuilder.numOfFragments(
				jsonObject.getInt("number_of_fragments"));
		}

		if (jsonObject.has("post_tags")) {
			highlightBuilder.postTags(
				JSONUtil.toStringArray(jsonObject.getJSONArray("post_tags")));
		}

		if (jsonObject.has("pre_tags")) {
			highlightBuilder.postTags(
				JSONUtil.toStringArray(jsonObject.getJSONArray("pre_tags")));
		}

		if (jsonObject.has("require_field_match")) {
			highlightBuilder.requireFieldMatch(
				jsonObject.getBoolean("require_field_match"));
		}

		if (jsonObject.has("type")) {
			highlightBuilder.highlighterType(jsonObject.getString("type"));
		}

		searchRequestBuilder.highlight(highlightBuilder.build());
	}

	@Override
	public String getName() {
		return "highlight";
	}

	private final FieldConfigBuilderFactory _fieldConfigBuilderFactory;
	private final HighlightBuilderFactory _highlightBuilderFactory;

}