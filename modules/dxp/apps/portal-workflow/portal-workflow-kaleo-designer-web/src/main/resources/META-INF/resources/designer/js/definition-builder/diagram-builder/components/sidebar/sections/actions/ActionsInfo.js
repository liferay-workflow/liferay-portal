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
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import {limitValue} from '../utils';

const DEFAULT_LIMIT = 1;
const MIN_PRIORITY = 1;

const executionTypeOptions = [
	{
		label: Liferay.Language.get('on-assignment'),
		value: 'onAssignment',
	},
	{
		label: Liferay.Language.get('on-entry'),
		value: 'onEntry',
	},
	{
		label: Liferay.Language.get('on-exit'),
		value: 'onExit',
	},
];

const ActionsInfo = ({identifier, sectionsLength, setSections}) => {
	const {setSelectedItem} = useContext(DiagramBuilderContext);
	const [executionType, setExecutionType] = useState('');
	const [priority, setPriority] = useState();
	const [actionDescription, setActionDescription] = useState('');
	const [actionName, setActionName] = useState('');
	const [template, setTemplate] = useState('');

	const updateSelectedItem = (values) => {
		setSelectedItem((previousItem) => ({
			...previousItem,
			data: {
				...previousItem.data,
				notifications: {
					description: values.map(({description}) => description),
					executionType: values.map(
						({executionType}) => executionType
					),
					name: values.map(({name}) => name),
					notificationType: values.map(
						({notificationType}) => notificationType
					),
					recipientType: values.map(
						({recipientType}) => recipientType
					),
					template: values.map(({template}) => template),
					templateLanguage: values.map(
						({templateLanguage}) => templateLanguage
					),
				},
			},
		}));
	};

	const deleteSection = () => {
		setSections((prevSections) => {
			const newSections = prevSections.filter(
				(prevSection) => prevSection.identifier !== identifier
			);

			updateSelectedItem(newSections);

			return newSections;
		});
	};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('information')}>
			<ClayForm.Group>
				<label htmlFor="actionName">
					{Liferay.Language.get('name')}
				</label>

				<ClayInput
					id="actionName"
					onChange={({target}) => setActionName(target.value)}
					placeholder={Liferay.Language.get('my-action')}
					type="text"
					value={actionName}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="actionDescription">
					{Liferay.Language.get('description')}
				</label>

				<ClayInput
					id="actionDescription"
					onChange={({target}) => setActionDescription(target.value)}
					type="text"
					value={actionDescription}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="template">
					{Liferay.Language.get('template')}
				</label>

				<ClayInput
					component="textarea"
					id="template"
					onChange={({target}) => setTemplate(target.value)}
					placeholder="${userName} sent you a ${entryType} for review in the workflow."
					type="text"
					value={template}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="execution-type">
					{Liferay.Language.get('execution-type')}
				</label>

				<ClaySelect
					aria-label="Select"
					id="execution-type"
					onChange={({target}) => setExecutionType(target.value)}
				>
					{executionTypeOptions.map((item) => (
						<ClaySelect.Option
							key={item.value}
							label={item.label}
							value={item.value}
						/>
					))}
				</ClaySelect>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="priority">
					{Liferay.Language.get('priority')}
				</label>

				<ClayInput
					aria-label="Select"
					id="priority"
					min={MIN_PRIORITY}
					onBlur={({target}) => {
						let {value: newValue} = target;

						newValue = limitValue({
							defaultValue: DEFAULT_LIMIT,
							min: MIN_PRIORITY,
							value: newValue,
						});

						setPriority(newValue);
					}}
					onChange={({target}) => {
						let {value: newValue} = target;
						newValue = newValue.includes('-')
							? newValue.replace('-', '')
							: newValue;

						setPriority(newValue);
					}}
					type="number"
					value={priority}
				/>
			</ClayForm.Group>

			<div className="sheet-subtitle" />

			<div className="section-buttons-area">
				<ClayButton
					className="mr-3"
					disabled={
						actionName.trim() === '' || template.trim() === ''
					}
					displayType="secondary"
					onClick={() =>
						setSections((prev) => {
							return [
								...prev,
								{identifier: `${Date.now()}-${prev.length}`},
							];
						})
					}
				>
					{Liferay.Language.get('add-action')}
				</ClayButton>

				{sectionsLength > 1 && (
					<ClayButtonWithIcon
						className="delete-button"
						displayType="unstyled"
						onClick={deleteSection}
						symbol="trash"
					/>
				)}
			</div>
		</SidebarPanel>
	);
};

ActionsInfo.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default ActionsInfo;
