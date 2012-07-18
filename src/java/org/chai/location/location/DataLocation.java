package org.chai.location.location;
/** 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.location.Exportable;
import org.chai.location.util.Utils;

@Entity(name="DataLocation")
@Table(name="chai_location_data_location")
public class DataLocation extends CalculationLocation implements Exportable {

	private Location location;
	private DataLocationType type;
	
	@ManyToOne(targetEntity=Location.class)
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@ManyToOne(targetEntity=DataLocationType.class)
	public DataLocationType getType() {
		return type;
	}
	
	public void setType(DataLocationType type) {
		this.type = type;
	}
	
	@Override
	@Transient
	public boolean collectsData() {
		return true;
	}

	@Override
	@Transient
	public List<DataLocation> getDataLocations() {
		List<DataLocation> result = new ArrayList<DataLocation>();
		result.add(this);
		return result;
	}

	@Override
	@Transient
	public List<Location> getChildren() {
		return new ArrayList<Location>();
	}
	
	@Override
	public List<DataLocation> getDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<DataLocation> result = new ArrayList<DataLocation>();
		if (types == null || types.contains(type)) result.add(this);
		return result;
	}

	@Override
	public List<Location> getChildren(Set<LocationLevel> skipLevels) {
		return getChildren();
	}

	@Override
	@Transient
	public Location getParent() {
		return location;
	}

	@Override
	public String toString() {
		return "DataLocation[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
}
