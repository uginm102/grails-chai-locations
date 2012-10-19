import org.codehaus.groovy.grails.resolve.GrailsRepoResolver;

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.source.level = 1.6
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
		grailsHome()
        grailsCentral()
		grailsPlugins()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
		
		/**
		 * Configure our resolver.
		 */
		def libResolver = new GrailsRepoResolver(null, null);
		libResolver.addArtifactPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]")
		libResolver.addIvyPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/ivys/ivy-[revision].xml")
		libResolver.name = "github"
//		libResolver.settings = ivySettings
		resolver libResolver
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.18'
    }

    plugins {
		build(":tomcat:$grailsVersion",
              ":release:2.0.4",
              ":rest-client-builder:1.0.2") {
            export = false
        }
		compile ":hibernate:$grailsVersion"
		compile ":i18n-fields:0.6.1-CHAI"
		test ":spock:0.6"
    }
}
