package org.chai.location

import grails.validation.ValidationException;

import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.springframework.dao.DataIntegrityViolationException;

class LocationSpec extends IntegrationTests {
	
	def sessionFactory
	
	def "type cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(code: CODE(2), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data location can be maneged and can manage another"(){
		setup:
		setupLocationTree()
		when:
		def manager = DataLocation.findByCode(BUTARO)
		def managed = new DataLocation(code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP),managedBy:manager,location: Location.findByCode(BURERA))
		manager.addToManages(managed)
		manager.save(failOnError: true)
		then:
		manager.manages.asList()[0] == managed
		managed.managedBy == manager
		
	}

	def "deleting location does not delete data locations"() {
		setup:
		setupLocationTree()
		
		when:
		LocationLevel.findByCode(DISTRICT).removeFromLocations(Location.findByCode(BURERA))
		Location.findByCode(NORTH).removeFromChildren(Location.findByCode(BURERA))
		Location.findByCode(BURERA).delete(flush: true)
		
		then:
		thrown DataIntegrityViolationException
	}
	
	def "deleting location does not delete child locations"() {
		setup:
		setupLocationTree()
		
		when:
		LocationLevel.findByCode(NATIONAL).removeFromLocations(Location.findByCode(RWANDA))
		Location.findByCode(RWANDA).delete(flush: true)
		
		then:
		thrown DataIntegrityViolationException
	}
	
	def "root location"() {
		setup:
		def level = newLocationLevel(CODE(1), 1)
		
		when:
		new Location(code: CODE(1), level: level).save(failOnError: true, flush: true)
		
		then:
		Location.count() == 1
		
		when:
		new Location(code: CODE(2), level: level).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be empty"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(code: "", type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	// TODO move to kevin since this is implementation dependent
//	def "data location type code cannot contain delimiter"() {
//		when:
//		new DataLocationType(code: CODE(1)).save(failOnError: true)
//		
//		then:
//		DataLocationType.count() == 1
//		
//		when:
//		new DataLocationType(code: CODE(1)+Utils.DEFAULT_TYPE_CODE_DELIMITER).save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//		
//	}
	
	def "get all children"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def newDataLocation = newDataLocation(["en":"BLAH"], "BLAH", Location.findByCode(NORTH), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		
		when: //with a mix of locations and data locations
		def children = Location.findByCode(NORTH).getChildrenLocations(skipLevels, types)
		
		then:
		children.equals([Location.findByCode(BURERA), DataLocation.findByCode("BLAH")])
		children.findAll{ it -> !it.collectsData() }.size() == 1
		children.findAll{ it -> it.collectsData() }.size() == 1
		
		when: //with only data locations
		children = Location.findByCode(BURERA).getChildrenLocations(skipLevels, types)
		
		then:
		children.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		children.findAll{ it -> !it.collectsData() }.size() == 0
		children.findAll{ it -> it.collectsData() }.size() == 2
	}
	
	def "get children with data"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def newDataLocation = newDataLocation(["en":"BLAH"], "BLAH", Location.findByCode(NORTH), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		
		when: //with data locations
		def children = Location.findByCode(NORTH).getChildrenEntitiesWithDataLocations(skipLevels, types, true)
		
		then:
		children.equals([Location.findByCode(BURERA), DataLocation.findByCode("BLAH")])
		children.findAll{ it -> !it.collectsData() }.size() == 1
		children.findAll{ it -> it.collectsData() }.size() == 1
		
		when: //without data locations
		children = Location.findByCode(NORTH).getChildrenEntitiesWithDataLocations(skipLevels, types, false)
		
		then:
		children.equals([Location.findByCode(BURERA)])
		children.findAll{ it -> !it.collectsData() }.size() == 1
		children.findAll{ it -> it.collectsData() }.size() == 0
	}
	
	def "get location tree with data"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		
		when: //with data locations
		def children = Location.findByCode(NORTH).collectTreeWithDataLocations(skipLevels, types, true)
		
		then:
		children.equals([Location.findByCode(BURERA), Location.findByCode(NORTH), DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		children.findAll{ it -> !it.collectsData() }.size() == 2
		children.findAll{ it -> it.collectsData() }.size() == 2
		
		when: //without data locations
		children = Location.findByCode(NORTH).collectTreeWithDataLocations(skipLevels, types, false)
		
		then:
		children.equals([Location.findByCode(BURERA), Location.findByCode(NORTH)])
		children.findAll{ it -> !it.collectsData() }.size() == 2
		children.findAll{ it -> it.collectsData() }.size() == 0
	}
}
