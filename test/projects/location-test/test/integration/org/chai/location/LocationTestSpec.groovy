package org.chai.location

import grails.plugin.spock.IntegrationSpec;

class LocationTestSpec extends IntegrationSpec {

	def "test location domain"() {
		setup:
		def locationLevel = new LocationLevel(code: "national").save(failOnError: true)
		def location = new Location(code: "rwanda", level: locationLevel).save(failOnError: true)
		
		when:
		new LocationTest(location: location).save(failOnError: true)
		
		then:
		LocationTest.count() == 1
	}
	
	def "test i18n on locations"() {
		setup:
		def locationLevel = new LocationLevel(code: "national").save(failOnError: true)
		
		when:
		locationLevel.setNames("test", new Locale('en'))
		
		then:
		locationLevel.getNames(new Locale('en')) == "test"
	}
	
}
