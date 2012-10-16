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

abstract class IntegrationTests extends IntegrationSpec {
	
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
	
	
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly
		
		def hc = newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(['en':NATIONAL], NATIONAL, 1)
		def province = newLocationLevel(['en':PROVINCE], PROVINCE, 2)
		def district = newLocationLevel(['en':DISTRICT], DISTRICT, 3)
		def sector = newLocationLevel(['en':SECTOR], SECTOR, 4)
			
		def rwanda = newLocation(['en':RWANDA], RWANDA,null,country)
		def north = newLocation(['en':NORTH], NORTH, rwanda, province)
		def burera = newLocation(['en':BURERA], BURERA, north, district)

		def butaro = newDataLocation(['en':BUTARO], BUTARO, burera, dh)
		def kivuye = newDataLocation(['en':KIVUYE], KIVUYE, burera, hc)
	}
	
	public static def newDataLocationType(def names, def code) {
		def dataLocationType = new DataLocationType(code: code)
		setLocaleValueInMap(dataLocationType,names,"Names")
		return dataLocationType.save(failOnError: true)
	}
	
	public static def newLocationLevel(def names, def code, def order) {
		def locationLevel = new LocationLevel(code: code, order: order)
		setLocaleValueInMap(locationLevel,names,"Names")
		return locationLevel.save(failOnError: true)
	}
	
	public static def newLocationLevel(def code, Integer order) {
		def locationLevel = new LocationLevel(code: code, order: order)
		return locationLevel.save(failOnError: true)
	}
	
	public static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(code: code, parent: parent, level: level)
		setLocaleValueInMap(location,names,"Names")
		location.save(failOnError: true)
		level.addToLocations(location)
		level.save(failOnError: true)
		if (parent != null) {
			parent.addToChildren(location)
			parent.save(failOnError: true)
		}
		return location
	}
	
	public static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(code: code, location: location, type: type)
		setLocaleValueInMap(dataLocation,names,"Names")
		dataLocation.save(failOnError: true)
		if (location != null) {
			location.addToDataLocations(dataLocation)
			location.save(failOnError: true)
		}
		if (type != null) {
			type.addToDataLocations(dataLocation)
			type.save(failOnError: true)
	   }
		return dataLocation
	}

	public static def setLocaleValueInMap(def object, def map, def fieldName){
		def methodName = 'set'+fieldName
		//TODO replace with CONF variable if this fails
		def grailsApplication = new Location().domainClass.grailsApplication
		grailsApplication.config.i18nFields.locales.each{ loc ->
			if(map.get(loc) != null)
				object."$methodName"(map.get(loc),new Locale(loc))
			else
				object."$methodName"("",new Locale(loc))
		}
	}
	
}
