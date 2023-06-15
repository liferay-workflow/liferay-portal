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

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayCheckbox, ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import React, {useContext, useEffect, useState} from 'react';

import {DiagramBuilderContext} from '../../../../../DiagramBuilderContext';
import SidebarPanel from '../../../SidebarPanel';

const scriptLanguageOptions = [
	{
		label: Liferay.Language.get('groovy'),
		value: 'groovy',
	},
	{
		label: Liferay.Language.get('java'),
		value: 'java',
	},
];

const ScriptedAssignment = ({setContentName}) => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const [showScriptData, setShowScriptData] = useState(
		selectedItem?.data.assignments?.script
	);

	const [scriptLanguage, setScriptLanguage] = useState(
		selectedItem?.data.assignments?.scriptLanguage
	);

	const [scriptCacheable, setScriptCacheable] = useState(
		selectedItem?.data.assignments?.scriptCacheable === 'true'
	);

	const [scriptCacheDuration, setScriptCacheDuration] = useState(
		selectedItem?.data.assignments?.scriptCacheDuration
	);

	const addSourceButtonName = Liferay.Language.get('add-source-code');

	const goToEditor = () => setContentName('scripted-assignment');

	const deleteScript = () => {
		setSelectedItem((previous) => {
			return {
				...previous,
				data: {...previous.data, assignments: null},
			};
		});
	};

	useEffect(() => {
		setShowScriptData(selectedItem?.data.assignments?.script);
	}, [selectedItem]);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('script')}>
			<label htmlFor="script-language">
				{Liferay.Language.get('script-language')}
			</label>

			<ClaySelect
				aria-label="Select"
				defaultValue={scriptLanguage}
				id="script-language"
				onChange={({target}) => {
					setScriptLanguage(target.value);

					setSelectedItem((previous) => {
						return {
							...previous,
							data: {
								...previous.data,
								assignments: {
									...previous.data.assignments,
									scriptLanguage: [target.value],
								},
							},
						};
					});
				}}
			>
				{scriptLanguageOptions &&
					scriptLanguageOptions.map((item) => (
						<ClaySelect.Option
							key={item.value}
							label={item.label}
							value={item.value}
						/>
					))}
			</ClaySelect>

			{showScriptData ? (
				<>
					<ClayLayout.ContentCol
						className="current-node-data-area"
						float
					>
						<ClayLayout.Row
							className="current-node-data-row"
							justify="between"
						>
							<ClayLink
								button={false}
								className="truncate-container"
								displayType="secondary"
								href="#"
								onClick={goToEditor}
							>
								<span>{Liferay.Language.get('script')}</span>
							</ClayLink>

							<ClayButtonWithIcon
								className="delete-button text-secondary trash-button"
								displayType="unstyled"
								onClick={deleteScript}
								symbol="trash"
							/>
						</ClayLayout.Row>
					</ClayLayout.ContentCol>

					<ClayCheckbox
						checked={scriptCacheable}
						id="script-cacheable"
						label={Liferay.Language.get('cacheable')}
						onChange={() => {
							setScriptCacheable(
								(scriptCacheable) => !scriptCacheable
							);

							setSelectedItem((previous) => {
								return {
									...previous,
									data: {
										...previous.data,
										assignments: {
											...previous.data.assignments,
											scriptCacheable: !scriptCacheable,
										},
									},
								};
							});
						}}
					/>

					<label htmlFor="script-cache-duration">
						{Liferay.Language.get('script-cache-duration-minutes')}

						<span
							className="ml-2"
							title={Liferay.Language.get(
								'choose-for-how-long-in-minutes-the-result-of-the-script-will-be-cached'
							)}
						>
							<ClayIcon
								className="text-muted"
								symbol="question-circle-full"
							/>
						</span>
					</label>

					<ClayInput
						aria-label="Select"
						defaultValue="1"
						disabled={!scriptCacheable}
						id="script-cache-duration"
						min="1"
						onChange={({target}) => {
							const {value: newValue} = target;

							setScriptCacheDuration(newValue);

							setSelectedItem((previous) => {
								return {
									...previous,
									data: {
										...previous.data,
										assignments: {
											...previous.data.assignments,
											scriptCacheDuration: newValue,
										},
									},
								};
							});
						}}
						type="number"
						value={scriptCacheDuration}
					/>
				</>
			) : (
				<ClayButton displayType="secondary" onClick={goToEditor}>
					{addSourceButtonName.toUpperCase()}
				</ClayButton>
			)}

			{showScriptData && <></>}
		</SidebarPanel>
	);
};

export default ScriptedAssignment;
