package org.chai.location

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

import grails.plugin.spock.IntegrationSpec;

import org.chai.location.util.Utils;
import org.chai.location.location.DataLocation;
import org.chai.location.location.DataLocationType;
import org.chai.location.location.Location;
import org.chai.location.location.LocationLevel;
import org.chai.location.Ordering;
import org.chai.location.Translation;
import org.chai.location.util.JSONUtils;

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	def sessionFactory
	
	static final String CODE (def number) { return "CODE"+number }
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"
	
	static final String NATIONAL = "National"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"
	
	static final String RWANDA = "Rwanda"
	static final String KIGALI_CITY = "Kigali City"
	static final String NORTH = "North"
	static final String BURERA = "Burera"
	static final String BUTARO = "Butaro DH"
	static final String KIVUYE = "Kivuye HC"
	
	
	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		springcacheService.flushAll()
	}
	
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly
		def hc = newDataLocationType(j(["en":HEALTH_CENTER_GROUP]), HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(j(["en":DISTRICT_HOSPITAL_GROUP]), DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(NATIONAL, 1)
		def province = newLocationLevel(PROVINCE, 2)
		def district = newLocationLevel(DISTRICT, 3)
		def sector = newLocationLevel(SECTOR, 4)
		
		def rwanda = newLocation(j(["en":RWANDA]), RWANDA, country)
		def north = newLocation(j(["en":NORTH]), NORTH, rwanda, province)
		def burera = newLocation(j(["en":BURERA]), BURERA, north, district)
		
		newDataLocation(j(["en":BUTARO]), BUTARO, burera, dh)
		newDataLocation(j(["en":KIVUYE]), KIVUYE, burera, hc)
	}
	
	static def newDataLocationType(def code) {
		return newDataLocationType([:], code)
	}
	
	static def newDataLocationType(def names, def code) {
		return new DataLocationType(names: names, code: code).save(failOnError: true)
	}
	
	static def newDataLocation(def code, def location, def type) {
		return newDataLocation([:], code, location, type)
	}
	
	static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(names: names, code: code, location: location, type: type).save(failOnError: true)
		if (location != null) {
			 location.dataLocations << dataLocation
			 location.save(failOnError: true)
		}
		return dataLocation
	}
	
	static def newLocationLevel(String code, def order) {
		return new LocationLevel(code: code, order: order).save(failOnError: true)
	}
	
	static def newLocation(String code, def level) {
		return newLocation([:], code, null, level)
	}

	static def newLocation(def names, def code, def level) {
		return newLocation(names, code, null, level)
	}
		
	static def newLocation(String code, def parent, def level) {
		return newLocation([:], code, parent, level)
	}
	
	static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(names: names, code: code, parent: parent, level: level).save(failOnError: true)
		level.locations << location
		level.save(failOnError: true)
		if (parent != null) {
			parent.children << location
			parent.save(failOnError: true)
		}
		return location
	}	
	
	static def g(def types) {
		return Utils.unsplit(types, DataLocationType.DEFAULT_CODE_DELIMITER)
	}
	
	static def getLocationLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add LocationLevel.findByCode(level)
		}
		return result;
	}
	
	static def getCalculationLocation(def code) {
		def location = Location.findByCode(code)
		if (location == null) location = DataLocation.findByCode(code)
		return location
	}
	
	static def getLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(Location.findByCode(code))
		}
		return result
	}
	
	static def getDataLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(DataLocation.findByCode(code))
		}
		return result
	}
	static def getDataLocationTypes(def codes){
		def result=[]
		for(String code: codes)
			result.add(DataLocationType.findByCode(code));
		return result;
	}
	
	static s(def list) {
		return new HashSet(list)
	}
	
	
	static j(def map) {
		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
	}
	
	static o(def map) {
		return new Ordering(jsonText: JSONUtils.getJSONFromMap(map));
	}
}
