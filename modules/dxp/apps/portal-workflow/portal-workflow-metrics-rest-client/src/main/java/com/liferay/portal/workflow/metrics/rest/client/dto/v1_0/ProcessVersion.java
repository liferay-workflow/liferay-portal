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

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.ProcessVersionSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class ProcessVersion implements Cloneable, Serializable {

	public static ProcessVersion toDTO(String json) {
		return ProcessVersionSerDes.toDTO(json);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setVersion(
		UnsafeSupplier<String, Exception> versionUnsafeSupplier) {

		try {
			version = versionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String version;

	@Override
	public ProcessVersion clone() throws CloneNotSupportedException {
		return (ProcessVersion)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProcessVersion)) {
			return false;
		}

		ProcessVersion processVersion = (ProcessVersion)object;

		return Objects.equals(toString(), processVersion.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProcessVersionSerDes.toJSON(this);
	}

}