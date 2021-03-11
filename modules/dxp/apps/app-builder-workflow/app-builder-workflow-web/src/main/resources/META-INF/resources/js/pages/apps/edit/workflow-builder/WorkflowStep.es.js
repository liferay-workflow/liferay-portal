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

import ClayBadge from '@clayui/badge';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import EditAppContext from 'app-builder-web/js/pages/apps/edit/EditAppContext.es';
import classNames from 'classnames';
import {sub} from 'data-engine-js-components-web/js/utils/lang.es';
import React, {useContext, useState} from 'react';
import {Handle} from 'react-flow-renderer';

import ButtonInfo from '../../../../components/button-info/ButtonInfo.es';
import MissingRequiredFieldsPopover from '../MissingRequiredFieldsPopover.es';

const Card = ({
	actions,
	errors,
	initial,
	isInitialOrFinalSteps,
	name,
	onClick,
	selected,
	stepInfo,
}) => {
	const {
		appId,
		config: {dataObject, formView},
		openFormViewModal,
		state: {app},
		updateFormView,
	} = useContext(EditAppContext);
	const [active, setActive] = useState(false);
	const [showPopover, setShowPopover] = useState(false);
	const duplicatedFields = errors?.formViews?.duplicatedFields || [];

	const {missingRequiredFields: {customField, nativeField} = {}} = formView;

	const handleOnClick = (event, onClick) => {
		event.preventDefault();
		setActive(false);
		onClick();
	};

	return (
		<div
			className={classNames('step-card', selected && 'selected')}
			onClick={onClick}
		>
			<div>
				{name}
				<ButtonInfo items={stepInfo} />
			</div>
			{duplicatedFields.length > 0 && (
				<ClayTooltipProvider>
					<ClayIcon
						className="error tooltip-popover-icon"
						data-tooltip-align="bottom"
						data-tooltip-delay="0"
						fontSize="26px"
						symbol="exclamation-full"
						title={`${Liferay.Language.get(
							'error'
						)}: ${Liferay.Language.get(
							'there-are-form-views-with-duplicated-fields'
						)}`}
					/>
				</ClayTooltipProvider>
			)}
			{!app.active && appId && initial && (customField || nativeField) && (
				<MissingRequiredFieldsPopover
					alignPosition="right"
					dataObjectName={dataObject.name}
					message={{
						custom: sub(
							Liferay.Language.get(
								'the-form-view-for-this-app-was-modified-and-does-not-contain-all-required-fields-for-the-x-object'
							),
							[dataObject.name]
						),
						native: sub(
							Liferay.Language.get(
								'the-form-view-for-this-app-was-modified-and-does-not-contain-all-native-required-fields-it-cannot-be-used-to-create-new-records-for-the-x-object'
							),
							[dataObject.name]
						),
					}}
					nativeField={nativeField}
					onClick={() => {
						setShowPopover(false);
						openFormViewModal(
							dataObject.id,
							dataObject.defaultLanguageId,
							updateFormView,
							formView.id
						);
					}}
					setShowPopover={setShowPopover}
					showPopover={showPopover}
					triggerClassName="help-cursor step-card-icon"
				/>
			)}
			<div className="d-flex">
				{!isInitialOrFinalSteps && (
					<ClayTooltipProvider>
						<ClayDropDown
							active={active}
							data-tooltip-align="bottom"
							data-tooltip-delay="0"
							onActiveChange={setActive}
							title={Liferay.Language.get('options')}
							trigger={
								<ClayButtonWithIcon
									className="border-0"
									displayType="secondary"
									symbol="ellipsis-v"
								/>
							}
						>
							<ClayDropDown.ItemList>
								{actions.map(({label, onClick}, index) => (
									<ClayDropDown.Item
										key={index}
										onClick={(event) =>
											handleOnClick(event, onClick)
										}
									>
										{label}
									</ClayDropDown.Item>
								))}
							</ClayDropDown.ItemList>
						</ClayDropDown>
					</ClayTooltipProvider>
				)}
			</div>
		</div>
	);
};

export default function WorkflowStep({
	data: {
		props: {
			actions,
			badgeLabel,
			errors,
			initial,
			name,
			onClick,
			selected,
			stepInfo,
		},
	},
}) {
	const isInitialOrFinalSteps = initial !== undefined;

	const isFinalStep = isInitialOrFinalSteps && !initial;

	return (
		<>
			<div className="step-wrapper">
				<ClayBadge
					className={classNames(
						'step-badge',
						!isInitialOrFinalSteps && 'index-badge'
					)}
					displayType={selected ? 'primary' : 'secondary'}
					label={badgeLabel}
				/>
				<Card
					actions={actions}
					errors={errors}
					initial={initial}
					isInitialOrFinalSteps={isInitialOrFinalSteps}
					name={name}
					onClick={onClick}
					selected={selected}
					stepInfo={stepInfo}
				/>
			</div>

			{isInitialOrFinalSteps ? (
				isFinalStep ? (
					<Handle
						position="top"
						style={{left: '55%'}}
						type="target"
					/>
				) : (
					<Handle
						position="bottom"
						style={{left: '55%'}}
						type="source"
					/>
				)
			) : (
				<>
					<Handle
						position="bottom"
						style={{left: '55%'}}
						type="source"
					/>
					<Handle
						position="top"
						style={{left: '55%'}}
						type="target"
					/>
				</>
			)}
		</>
	);
}
