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

import {formatDate} from 'app-builder-web/js/utils/time.es';
import React from 'react';

import UserAvatar from '../../../components/user-avatar/UserAvatar.es';

export default function ActivityItem({
	auditPerson,
	commentLog,
	dateCreated,
	description,
}) {
	return (
		<div className="activity">
			<div className="activity__header">
				<UserAvatar imageURL={auditPerson?.image} />

				<p>
					<span className="person-name">{auditPerson?.name}</span>

					{description}
				</p>
			</div>

			<div className="activity__content">
				{commentLog && <p>{commentLog}</p>}

				<div className="font-weight-semi-bold text-secondary">
					{`${formatDate(dateCreated, 'l')} — ${formatDate(
						dateCreated,
						'LT'
					)}`}
				</div>
			</div>
		</div>
	);
}
