package org.chai.location

class LocationTest {

	static belongsTo = [location: Location]
	
	static constraints = {
		location (nullable: false)
	}
	
}
