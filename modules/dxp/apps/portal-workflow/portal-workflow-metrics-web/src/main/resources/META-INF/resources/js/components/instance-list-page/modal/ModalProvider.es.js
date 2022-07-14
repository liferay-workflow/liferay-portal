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

import React, {createContext, useState} from 'react';

const ModalContext = createContext();

export default function ModalProvider({children, processId}) {
	const [bulkAction, setBulkAction] = useState('');
	const [bulkReassign, setBulkReassign] = useState({
		reassignedTasks: [],
		reassigning: false,
		selectedAssignee: null,
		useSameAssignee: false,
	});
	const [bulkTransition, setBulkTransition] = useState({
		transition: {errors: {}, onGoing: false},
		transitionTasks: [],
	});
	const [fetchOnClose, setFetchOnClose] = useState(false);
	const [selectTasks, setSelectTasks] = useState({
		selectAll: false,
		tasks: [],
	});
	const [singleTransition, setSingleTransition] = useState({
		title: '',
		transitionName: '',
	});
	const [updateDueDate, setUpdateDueDate] = useState({
		comment: undefined,
		dueDate: undefined,
	});
	const [visibleModal, setVisibleModal] = useState('');

	const closeModal = (refetch) => {
		setFetchOnClose(refetch);
		setVisibleModal('');
	};

	const modalState = {
		bulkAction,
		bulkReassign,
		bulkTransition,
		closeModal,
		fetchOnClose,
		openModal: setVisibleModal,
		processId,
		selectTasks,
		setBulkAction,
		setBulkReassign,
		setBulkTransition,
		setSelectTasks,
		setSingleTransition,
		setUpdateDueDate,
		singleTransition,
		updateDueDate,
		visibleModal,
	};

	return (
		<ModalContext.Provider value={modalState}>
			{children}
		</ModalContext.Provider>
	);
}

export {ModalContext};
