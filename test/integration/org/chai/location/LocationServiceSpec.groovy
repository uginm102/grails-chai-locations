package org.chai.location

import grails.validation.ValidationException;

import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;

class LocationServiceSpec extends IntegrationTests {
	
	def locationService;
	
	def "get locations of level"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getChildrenOfLevel(Location.findByCode(location), LocationLevel.findByCode(level)).containsAll(expectedLocations.collect{Location.findByCode(it)})
		
		where:
		location	| level		| expectedLocations
		RWANDA		| NATIONAL	| [RWANDA]
		RWANDA		| PROVINCE	| [NORTH]
		RWANDA		| DISTRICT	| [BURERA]
		NORTH		| DISTRICT	| [BURERA]
		BURERA		| DISTRICT	| [BURERA]
		BURERA		| NATIONAL	| []
	}
	
	def "get parent of level for location"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getParentOfLevel(Location.findByCode(location), LocationLevel.findByCode(level)).equals(Location.findByCode(expectedLocation))

		where:
		location| level		| expectedLocation
		NORTH	| NATIONAL	| RWANDA
		BURERA	| NATIONAL	| RWANDA
		
	}
	
	def "get data location by code"(){
		setup:
		setupLocationTree()
		
		when:
		def dataLocation = DataLocation.findByCode(BUTARO)
		def dataEntOne = locationService.findCalculationLocationByCode(BUTARO, DataLocation.class);
		
		then:
		dataEntOne != null
		dataEntOne.equals(dataLocation)
		
		when:
		def dataEntTwo = locationService.findCalculationLocationByCode(BUTARO, Location.class);
		
		then:
		dataEntTwo == null
		!dataEntTwo.equals(dataLocation)
	}
	
	def "get location location by code"(){
		setup:
		setupLocationTree()
		
		when:
		def location = Location.findByCode(BURERA)
		def locationEntOne = locationService.findCalculationLocationByCode(BURERA,Location.class)
		
		then:
		locationEntOne != null
		locationEntOne.equals(location)
		
		when:
		def locationEntTwo = locationService.findCalculationLocationByCode(BURERA,DataLocation.class)
		
		then:
		locationEntTwo == null
		!locationEntTwo.equals(location)
				
	}
	
	def "search location"() {
		setup:
		setupLocationTree()

		when:
		def result = locationService.searchLocation(Location.class, text,[:])

		then:
		result.equals(expectedResult.collect{Location.findByCode(it)})

		where:
		text  | expectedResult
		"Bur" | [BURERA]
		"Nor" | [NORTH]
		"But" | []
		"n/a" | []
	}

	def "search Datalocation"() {
		setup:
		setupLocationTree()

		when:
		def result = locationService.searchLocation(DataLocation.class, text,[:])
		
		then:
		result.equals(expectedResult.collect{DataLocation.findByCode(it)})

		where:
		text     | expectedResult
		"But"    | [BUTARO]
		"But DH" | [BUTARO]
		"But Kiv"| []
		"Kiv"    | [KIVUYE]
		"Bur"    | []
		"n/a"    | []
	}
	
	def "search Calculationlocation"() {
		setup:
		setupLocationTree()

		when:
		def result = locationService.searchLocation(CalculationLocation.class, text,[:])

		then:
		result.equals(expectedResult.collect{CalculationLocation.findByCode(it)})

		where:
		text  | expectedResult
		"But" | [BUTARO]
		"Bur" | [BURERA]
		"n/a" | []
	}
	
	def "search with limit and offset"() {
		setup:
		setupLocationTree()
		def result
		
		when:
		result = locationService.searchLocation(Location.class, "", [max: 1])
		
		then:
		result*.code.equals([RWANDA])
		
		when:
		result = locationService.searchLocation(Location.class, "", [max: 2])
		
		then:
		result*.code.equals([RWANDA, NORTH])
		
		when:
		result = locationService.searchLocation(Location.class, "", [max: 2, offset: 1])
		
		then:
		result*.code.equals([NORTH, BURERA])
	}
	
	def "search order on names field"() {
		setup:
		setupLocationTree()
		def result
		
		when:
		result = locationService.searchLocation(Location.class, "", [sort: 'names', order: 'asc'])
		
		then:
		result*.code.equals([BURERA, NORTH, RWANDA])
		
		when:
		result = locationService.searchLocation(Location.class, "", [sort: 'names', order: 'desc'])
		
		then:
		result*.code.equals([RWANDA, NORTH, BURERA])
		
		when: "default is asc"
		result = locationService.searchLocation(Location.class, "", [sort: 'names'])
		
		then:
		result*.code.equals([BURERA, NORTH, RWANDA])
	}
	
	def "get list of levels with no skip levels"(){
		setup:
		setupLocationTree()
		def skipLevels = null
		
		when:
		def levels = locationService.listLevels(skipLevels)
		def noSkipLevels = locationService.listLevels()
		
		then:
		levels == noSkipLevels
		levels.size() == 4
	}
	
	def "get list of levels with skip levels"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		
		when:
		def levels = locationService.listLevels(skipLevels)
		def noSkipLevels = locationService.listLevels()
		
		then:
		levels != noSkipLevels
		levels.size() == 3
	}
	
	def "get level before with skip levels"(){
		setup:
		setupLocationTree()
		
		//levelBefore == null
		when:
		def level = LocationLevel.findByCode(PROVINCE)
		def skipLevels = new HashSet([LocationLevel.findByCode(NATIONAL)])
		def levelBefore = locationService.getLevelBefore(level, skipLevels)
		
		then:
		levelBefore == null
		
		//levelBefore skips 1 level
		when:
		level = LocationLevel.findByCode(DISTRICT)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE)])
		levelBefore = locationService.getLevelBefore(level, skipLevels)
		
		then:
		levelBefore.equals(LocationLevel.findByCode(NATIONAL))
		
		//levelBefore skips 2 levels
		when:
		level = LocationLevel.findByCode(SECTOR)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE), LocationLevel.findByCode(DISTRICT)])
		levelBefore = locationService.getLevelBefore(level, skipLevels)
		
		then:
		levelBefore.equals(LocationLevel.findByCode(NATIONAL))
	}
	
	def "get level after with skip levels"(){
		setup:
		setupLocationTree()
		
		//levelAfter == null
		when:
		def level = LocationLevel.findByCode(DISTRICT)
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter == null
		
		//levelAfter skips 1 level
		when:
		level = LocationLevel.findByCode(PROVINCE)
		skipLevels = new HashSet([LocationLevel.findByCode(DISTRICT)])
		levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter.equals(LocationLevel.findByCode(SECTOR))
		
		//levelAfter skips 2 levels
		when:
		level = LocationLevel.findByCode(NATIONAL)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE), LocationLevel.findByCode(DISTRICT)])
		levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter.equals(LocationLevel.findByCode(SECTOR))
	}
	def "test get data locations of type"(){
		setup:
		setupLocationTree()
		
		def locations = new HashSet([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		def typeOne = new HashSet();
		def typeTwo = new HashSet();
		def typeThree = new HashSet();
		
		def hc = DataLocationType.findByCode(HEALTH_CENTER_GROUP);
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP);
		
		typeOne.add(hc);
		typeTwo.add(dh);
		typeThree.add(hc);
		typeThree.add(dh);

		when:
		def caseOne = locationService.getDataLocationsOfType(locations,typeOne);
		def caseTwo = locationService.getDataLocationsOfType(locations,typeTwo);
		def caseThree = locationService.getDataLocationsOfType(locations,typeThree);
		def listDataLocations =[DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)]
		
		then:
		caseOne.equals([DataLocation.findByCode(KIVUYE)]);
		caseTwo.equals([DataLocation.findByCode(BUTARO)]);
		caseThree.size()==2
		listDataLocations.size()==2
		caseThree.equals(listDataLocations);
		
	}
}
