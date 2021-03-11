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

import EditAppContext from 'app-builder-web/js/pages/apps/edit/EditAppContext.es';
import React, {useContext, useEffect, useRef, useState} from 'react';
import ReactFlow, {
	Controls,
	ReactFlowProvider,
	addEdge,
	removeElements,
} from 'react-flow-renderer';

import {ADD_STEP, REMOVE_STEP, UPDATE_CURRENT_STEP} from '../configReducer.es';
import WorkflowStep from './WorkflowStep.es';

import './dnd.scss';
import CreateNode from './CreateNode.es';

export default function WorkflowBuilder() {
	const {
		config: {currentStep, dataObject, steps},
		dispatchConfig,
	} = useContext(EditAppContext);
	const reactFlowWrapper = useRef(null);
	const [reactFlowInstance, setReactFlowInstance] = useState(null);
	const [elements, setElements] = useState([]);

	const badgeLabel = (index) => {
		if (index === 0) {
			return Liferay.Language.get('start');
		}
		else if (index === steps.length - 1) {
			return Liferay.Language.get('end');
		}

		return index;
	};

	const onClickStep = (id) => {
		dispatchConfig({id, type: UPDATE_CURRENT_STEP});
	};

	const stepInfo = [
		[
			{
				...dataObject,
				label: Liferay.Language.get('data-object'),
			},
		],
		...steps
			.filter(({initial}) => initial === undefined)
			.map(({appWorkflowRoleAssignments = []}) =>
				appWorkflowRoleAssignments.length > 0
					? [
							{
								label: Liferay.Language.get('assignee'),
								name: appWorkflowRoleAssignments
									.map(({roleName}) => roleName)
									.reduce((acc, cur) => `${acc}, ${cur}`),
							},
					  ]
					: []
			),
	];

	let customElements = steps.map((step, index) => {
		step = {
			...step,
			data: {
				props: {
					actions: [
						{
							label: Liferay.Language.get('delete-step'),
							onClick: () =>
								dispatchConfig({
									stepIndex: index,
									type: REMOVE_STEP,
								}),
						},
					],
					badgeLabel: badgeLabel(index),
					errors: step.errors,
					initial: step.initial,
					isInitialOrFinalSteps: step.isInitialOrFinalSteps,
					name: step.name,
					onClick: () => onClickStep(step.id),
					selected: step.id === currentStep.id,
					stepInfo: stepInfo[index] ?? [],
				},
			},
			id: `${step.id}`,
		};

		return step;
	});

	const addStep = () => {
		dispatchConfig({
			id: 1,
			stepPosition: {x: 500, y: 200},
			stepType: 'workflowNode',
			type: ADD_STEP,
		});
	};

	customElements = [
		...customElements,
		{
			id: 'e0-500',
			source: '0',
			target: '500',
			type: 'step',
		},
		{
			data: {addStep},
			id: '500',
			position: {x: 446, y: 200},
			type: 'createNode',
		},
		{
			arrowHeadType: 'arrowclosed',
			id: 'e500-800',
			source: '500',
			target: '800',
			type: 'step',
		},
	];

	useEffect(() => {
		setElements(customElements);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (steps.length > 2) {
			let newNode = steps.find(({id}) => id == currentStep.id);

			newNode = {
				...newNode,
				data: {
					props: {
						actions: [
							{
								label: Liferay.Language.get('delete-step'),
							},
						],
						badgeLabel: badgeLabel(newNode.id),
						errors: newNode.errors,
						initial: newNode?.initial,
						isInitialOrFinalSteps: newNode.isInitialOrFinalSteps,
						name: newNode.name,
						onClick: () => onClickStep(newNode.id),
						selected: newNode.id === currentStep.id,
						stepInfo: stepInfo[newNode.id] ?? [],
					},
				},
				id: `${newNode.id}`,
			};

			setElements((es) => es.concat(newNode));

			const edge = {
				arrowHeadType: 'arrowclosed',
				id: 'e0-1',
				source: '0',
				target: '1',
				type: 'step',
			};

			setElements((es) => es.concat(edge));
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [steps.length]);

	const onConnect = (params) =>
		setElements((els) => addEdge({...params, type: 'step'}, els));

	const onDrop = (event) => {
		event.preventDefault();
		const reactFlowBounds = reactFlowWrapper.current.getBoundingClientRect();

		const position = reactFlowInstance.project({
			x: event.clientX - reactFlowBounds.left,
			y: event.clientY - reactFlowBounds.top,
		});

		const type = event.dataTransfer.getData('application/reactflow');

		dispatchConfig({
			id: 700,
			stepPosition: position,
			stepType: type,
			type: ADD_STEP,
		});

		const newNode = steps.find(({id}) => id == 700);

		setElements((es) => es.concat(newNode));
	};

	const onElementsRemove = (elementsToRemove) =>
		setElements((els) => removeElements(elementsToRemove, els));

	const onLoad = (_reactFlowInstance) =>
		setReactFlowInstance(_reactFlowInstance);

	const onDragOver = (event) => {
		event.preventDefault();
		event.dataTransfer.dropEffect = 'move';
	};

	const nodeTypes = {
		createNode: CreateNode,

		workflowNode: WorkflowStep,
	};

	return (
		<div className="app-builder-workflow-app__builder">
			<div className="dndflow">
				<ReactFlowProvider>
					<div className="reactflow-wrapper" ref={reactFlowWrapper}>
						<ReactFlow
							elements={elements}
							nodeTypes={nodeTypes}
							onConnect={onConnect}
							onDragOver={onDragOver}
							onDrop={onDrop}
							onElementsRemove={onElementsRemove}
							onLoad={onLoad}
						>
							<Controls />
						</ReactFlow>
					</div>
				</ReactFlowProvider>
			</div>
		</div>
	);
}
