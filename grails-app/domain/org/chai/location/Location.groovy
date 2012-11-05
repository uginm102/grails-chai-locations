/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
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
package org.chai.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;

/**
 * @author Jean Kahigiso M.
 */
class Location extends CalculationLocation {

	Location parent
	LocationLevel level
	
	static hasMany = [dataLocations: DataLocation, children: Location]
	
	static constraints = {
		level nullable: false
		parent(nullable: true, validator: { val, obj ->
			// TODO validate that there are no loops, i.e the graph must be a DAG
			if (val == null) {
				Location.withSession { session ->
					def flushMode = session.getFlushMode()
					session.setFlushMode(FlushMode.MANUAL);
					def roots = Location.findAllByParentIsNull()
					session.setFlushMode(flushMode);
					return roots.empty || roots.equals([obj])
				}
			}
		})
	}
	
	static mapping = {
		table "chai_location_location"
		level column: "level"
		parent column: "parent"
		children cache: true
		dataLocations cache: true
	}
	
	//gets all location children
	List<Location> getChildren(def skipLevels) {
		def result = new ArrayList<Location>();
		for (def child : children) {
			if (skipLevels != null && skipLevels.contains(child.level)) {
				result.addAll(child.getChildren(skipLevels));
			}
			else result.add(child);
		}
		return result;
	}
	
	// returns the children data locations only, without collecting data locations at lower levels
	// if the child's level is skipped, returns the child's data locations
	List<DataLocation> getDataLocations(def skipLevels, def types) {
		List<DataLocation> result = new ArrayList<DataLocation>();
		for (DataLocation dataLocation : dataLocations) {
			if (types == null || types.contains(dataLocation.type)) 
				result.add(dataLocation);
		}
		
		for (Location child : children) {
			if (skipLevels != null && skipLevels.contains(child.level)) {
				result.addAll(child.getDataLocations(skipLevels, types));
			}
		}
		
		return result;				
	}
			
	//gets all location children and data locations
	List<CalculationLocation> getChildrenLocations(def skipLevels, def types) {
		def result = new ArrayList<CalculationLocation>();
		result.addAll(getChildren(skipLevels));
		result.addAll(getDataLocations(skipLevels, types));
		return result;
	}
	
	//gets all location children and data locations (that have data locations)
	/**
	 * Returns all the immediate children, taking into account the skip levels, the data location types specified.
	 * If data is true, returns a list with both immediate location children and data locations. If data is false,
	 * returns only the locations.
	 * 
	 * @param skipLevels
	 * @param types
	 * @return
	 */
	List<CalculationLocation> getChildrenEntitiesWithDataLocations(def skipLevels, def types, boolean data = true) {
		def result = new ArrayList<CalculationLocation>();
		
		def locationChildren = getChildren(skipLevels);
		def locationTree = collectTreeWithDataLocations(skipLevels, types);
		for(def locationChild : locationChildren){
			if(locationTree.contains(locationChild))
				result.add(locationChild);	
		}
		
		if (data) result.addAll(getDataLocations(skipLevels, types));
		
		return result;
	}
	
	//gets all location children, grand-children, etc (that have data locations)
	List<Location> collectTreeWithDataLocations(def skipLevels, def types, boolean data = true) {
		def locations = new ArrayList<Location>();
		collectLocations(locations, null, skipLevels, types);
		if (data) locations.addAll(collectDataLocations(skipLevels, types));
		return locations;
	}
	
	Location getParentOfLevel(LocationLevel level) {
		if (this.level.equals(level)) return this
		else return parent?.getParentOfLevel(level)
	}
	
	boolean collectsData() {
		return false;
	}
	
	String toString() {
		return "Location[Id=" + id + ", Code=" + code + "]";
	}

}