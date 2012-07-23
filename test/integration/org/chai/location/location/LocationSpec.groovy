package org.chai.location.location

import grails.validation.ValidationException;

import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.IntegrationTests;

class LocationSpec extends IntegrationTests {
	
	def "type cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:"", code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:"", code: CODE(2), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:"", code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:"", type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be empty"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:"", code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:"", code: "", type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data location type code cannot be null"() {
		when:
		new DataLocationType(code: CODE(1)).save(failOnError: true)
		
		then:
		DataLocationType.count() == 1
		
		when:
		new DataLocationType(code:"").save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}
