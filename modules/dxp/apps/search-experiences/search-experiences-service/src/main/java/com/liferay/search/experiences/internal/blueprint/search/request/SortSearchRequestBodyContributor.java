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

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.geolocation.DistanceUnit;
import com.liferay.portal.search.geolocation.GeoBuilders;
import com.liferay.portal.search.geolocation.GeoDistanceType;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.script.ScriptBuilder;
import com.liferay.portal.search.script.ScriptType;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.GeoDistanceSort;
import com.liferay.portal.search.sort.NestedSort;
import com.liferay.portal.search.sort.ScoreSort;
import com.liferay.portal.search.sort.ScriptSort;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortMode;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterDataImpl;
import com.liferay.search.experiences.internal.blueprint.parser.SXPTemplateVariableParser;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Petteri Karttunen
 */
public class SortSearchRequestBodyContributor
	implements SearchRequestBodyContributor {

	public SortSearchRequestBodyContributor(
		GeoBuilders geoBuilders, Queries queries, Scripts scripts,
		Sorts sorts) {

		_geoBuilders = geoBuilders;
		_queries = queries;
		_scripts = scripts;
		_sorts = sorts;
	}

	@Override
	public void contribute(
		SearchRequestBuilder searchRequestBuilder, SXPBlueprint sxpBlueprint,
		SXPParameterData sxpParameterData) {

		if (_hasSorts(searchRequestBuilder)) {
			return;
		}

		List<Sort> sorts = new ArrayList<>();

		// TODO Replace with real JSON

		JSONObject sortJSONObject = JSONUtil.put("test", "test");

		for (String key : sortJSONObject.keySet()) {
			JSONObject jsonObject = SXPTemplateVariableParser.parse(
				sortJSONObject.getJSONObject(key), sxpParameterData);

			if (jsonObject == null) {
				continue;
			}

			SXPParameter sxpParameter = sxpParameterData.getSXPParameterByName(
				jsonObject.getString("parameter_name"));

			if (sxpParameter == null) {
				continue;
			}

			SortOrder sortOrder = _toSortOrder(
				GetterUtil.getString(sxpParameter.getValue()));

			if (sortOrder == null) {
				continue;
			}

			Sort sort = _toSort(jsonObject, key, sortOrder);

			if (sort != null) {
				sorts.add(sort);
			}
		}

		if (sorts.isEmpty()) {

			// TODO Replace with real JSON

			sorts = _getDefaultSorts(
				JSONUtil.putAll("test", "test"), sxpParameterData);
		}

		for (Sort sort : sorts) {
			searchRequestBuilder.addSort(sort);
		}
	}

	private List<Sort> _getDefaultSorts(
		JSONArray jsonArray, SXPParameterData sxpParameterData) {

		List<Sort> sorts = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			Sort sort = null;

			Object object = jsonArray.get(i);

			if (object instanceof JSONObject) {
				JSONObject jsonObject = SXPTemplateVariableParser.parse(
					(JSONObject)object, sxpParameterData);

				if ((jsonObject == null) || (jsonObject.length() == 0)) {
					continue;
				}

				sort = _toSort(jsonObject);
			}
			else if (object instanceof String) {
				sort = _toSort((String)object, null);
			}

			if (sort != null) {
				sorts.add(sort);
			}
		}

		return sorts;
	}

	private NestedSort _getNestedSort(JSONObject jsonObject) {
		JSONObject nestedJSONObject = jsonObject.getJSONObject("nested");

		NestedSort nestedSort = _sorts.nested(
			nestedJSONObject.getString("path"));

		if (jsonObject.has("filter")) {
			Query query = _getQuery(
				jsonObject,
				new SXPParameterDataImpl("", Collections.emptySet()));

			if (query != null) {
				nestedSort.setFilterQuery(query);
			}
		}

		if (nestedJSONObject.has("nested")) {
			nestedSort.setNestedSort(_getNestedSort(nestedJSONObject));
		}

		return nestedSort;
	}

	private Query _getQuery(
		JSONObject jsonObject, SXPParameterData sxpParameterData) {

		if (jsonObject == null) {
			return null;
		}

		Iterator<String> iterator = jsonObject.keys();

		if (!iterator.hasNext()) {
			return null;
		}

		String key = iterator.next();

		jsonObject = SXPTemplateVariableParser.parse(
			jsonObject.getJSONObject(key), sxpParameterData);

		if (jsonObject == null) {
			return null;
		}

		if (Objects.equals(key, "term")) {
			return _getTermQuery(jsonObject);
		}
		else if (Objects.equals(key, "wrapper")) {
			return _queries.wrapper(jsonObject.getString("query"));
		}

		return null;
	}

	private TermQuery _getTermQuery(JSONObject jsonObject) {
		Iterator<String> iterator = jsonObject.keys();

		if (!iterator.hasNext()) {
			return null;
		}

		Float boost = null;
		String keywords = null;

		String field = iterator.next();

		Object object = jsonObject.get(field);

		if (object instanceof JSONObject) {
			JSONObject fieldJSONObject = (JSONObject)object;

			boost = GetterUtil.getFloat(fieldJSONObject.get("boost"));
			keywords = fieldJSONObject.getString("value");
		}
		else if (object instanceof String) {
			keywords = (String)object;
		}

		if (Validator.isNull(keywords)) {
			return null;
		}

		TermQuery termQuery = _queries.term(field, keywords);

		if (boost > 0.0) {
			termQuery.setBoost(boost);
		}

		return termQuery;
	}

	private boolean _hasSorts(SearchRequestBuilder searchRequestBuilder) {
		return searchRequestBuilder.withSearchContextGet(
			searchContext -> {
				SearchRequest searchRequest =
					(SearchRequest)searchContext.getAttribute("search.request");

				return !ListUtil.isEmpty(searchRequest.getSorts());
			});
	}

	private Sort _toFieldSort(
		String field, JSONObject jsonObject, SortOrder sortOrder) {

		FieldSort fieldSort = _sorts.field(field, sortOrder);

		if (jsonObject.has("missing")) {
			fieldSort.setMissing(jsonObject.getString("missing"));
		}

		if (jsonObject.has("nested")) {
			fieldSort.setNestedSort(_getNestedSort(jsonObject));
		}

		if (jsonObject.has("mode")) {
			fieldSort.setSortMode(
				SortMode.valueOf(
					StringUtil.toUpperCase(jsonObject.getString("mode"))));
		}

		return fieldSort;
	}

	private Sort _toGeoDistanceSort(
		JSONObject jsonObject, SortOrder sortOrder) {

		JSONArray jsonArray = jsonObject.getJSONArray("locations");

		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			return null;
		}

		// TODO Field is null even in the original branch

		String field = null;

		GeoDistanceSort geoDistanceSort = _sorts.geoDistance(field);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONArray locationJSONArray = jsonArray.getJSONArray(i);

			if (locationJSONArray.length() != 2) {
				continue;
			}

			geoDistanceSort.addGeoLocationPoints(
				_geoBuilders.geoLocationPoint(
					locationJSONArray.getDouble(0),
					locationJSONArray.getDouble(1)));
		}

		String unit = jsonObject.getString("unit");

		if (Validator.isNotNull(unit)) {
			unit = StringUtil.toLowerCase(unit);

			for (DistanceUnit distanceUnit : DistanceUnit.values()) {
				if (Objects.equals(distanceUnit.getUnit(), unit)) {
					geoDistanceSort.setDistanceUnit(distanceUnit);

					break;
				}
			}
		}

		String distanceType = jsonObject.getString(
			"distance_type", GeoDistanceType.ARC.name());

		geoDistanceSort.setGeoDistanceType(
			GeoDistanceType.valueOf(StringUtil.toUpperCase(distanceType)));

		String mode = jsonObject.getString("mode");

		if (Validator.isNotNull(mode)) {
			geoDistanceSort.setSortMode(
				SortMode.valueOf(StringUtil.toUpperCase(mode)));
		}

		geoDistanceSort.setSortOrder(sortOrder);

		return geoDistanceSort;
	}

	private Sort _toScoreSort(SortOrder sortOrder) {
		Sort sort = _sorts.score();

		sort.setSortOrder(sortOrder);

		return sort;
	}

	private Script _toScript(JSONObject jsonObject) {
		ScriptBuilder scriptBuilder = _scripts.builder();

		String id = jsonObject.getString("id");
		String source = jsonObject.getString("source");

		if (Validator.isNotNull(id)) {
			scriptBuilder.idOrCode(
				id
			).scriptType(
				ScriptType.STORED
			);
		}
		else if (Validator.isNotNull(source)) {
			scriptBuilder.idOrCode(
				source
			).scriptType(
				ScriptType.INLINE
			);
		}

		if (jsonObject.has("lang")) {
			scriptBuilder.language(jsonObject.getString("lang"));
		}

		JSONObject optionsJSONObject = jsonObject.getJSONObject("options");

		if (optionsJSONObject != null) {
			for (String key : optionsJSONObject.keySet()) {
				scriptBuilder.putParameter(key, optionsJSONObject.get(key));
			}
		}

		JSONObject paramsJSONObject = jsonObject.getJSONObject("params");

		if (paramsJSONObject != null) {
			for (String key : paramsJSONObject.keySet()) {
				scriptBuilder.putParameter(key, paramsJSONObject.get(key));
			}
		}

		return scriptBuilder.build();
	}

	private Script _toScript(String string) {
		ScriptBuilder scriptBuilder = _scripts.builder();

		scriptBuilder.idOrCode(
			string
		).language(
			"painless"
		).scriptType(
			ScriptType.INLINE
		);

		return scriptBuilder.build();
	}

	private Sort _toScriptSort(JSONObject jsonObject, SortOrder sortOrder) {
		Object object = jsonObject.get("script");

		if (object == null) {
			return null;
		}

		Script script = null;

		if (object instanceof JSONObject) {
			script = _toScript((JSONObject)object);
		}
		else if (object instanceof String) {
			script = _toScript((String)object);
		}

		if (script == null) {
			return null;
		}

		ScriptSort scriptSort = _sorts.script(
			script,
			ScriptSort.ScriptSortType.valueOf(
				StringUtil.toUpperCase(jsonObject.getString("type"))));

		scriptSort.setSortOrder(sortOrder);

		return scriptSort;
	}

	private Sort _toSort(JSONObject jsonObject) {
		Iterator<String> iterator = jsonObject.keys();

		String key = iterator.next();

		Object object = jsonObject.get(key);

		if (object instanceof JSONObject) {
			JSONObject sortJSONObject = (JSONObject)object;

			return _toSort(
				jsonObject, key,
				_toSortOrder(sortJSONObject.getString("order")));
		}
		else if (object instanceof String) {
			return _toSort(key, _toSortOrder((String)object));
		}

		return null;
	}

	private Sort _toSort(
		JSONObject jsonObject, String key, SortOrder sortOrder) {

		if (Objects.equals(key, "_geo_distance")) {
			return _toGeoDistanceSort(jsonObject, sortOrder);
		}
		else if (Objects.equals(key, "_score")) {
			return _toScoreSort(sortOrder);
		}
		else if (Objects.equals(key, "_script")) {
			return _toScriptSort(jsonObject, sortOrder);
		}

		return _toFieldSort(key, jsonObject, sortOrder);
	}

	private Sort _toSort(String field, SortOrder sortOrder) {
		if (field.equals("_score")) {
			ScoreSort scoreSort = _sorts.score();

			if (sortOrder != null) {
				scoreSort.setSortOrder(sortOrder);
			}

			return scoreSort;
		}

		Sort sort = _sorts.field(field);

		if (sortOrder != null) {
			sort.setSortOrder(sortOrder);
		}

		return sort;
	}

	private SortOrder _toSortOrder(String string) {
		return SortOrder.valueOf(StringUtil.toUpperCase(string));
	}

	private final GeoBuilders _geoBuilders;
	private final Queries _queries;
	private final Scripts _scripts;
	private final Sorts _sorts;

}