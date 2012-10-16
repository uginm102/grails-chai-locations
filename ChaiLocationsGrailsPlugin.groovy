class ChaiLocationsGrailsPlugin {
    // the plugin version
    def version = "0.2.3-CHAI"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
		"grails-app/views/*",
		"web-app/css/*",
		"web-app/js/*",
		"web-app/images/*"
    ]

    // TODO Fill in these fields
    def title = "CHAI Locations Plugin" // Headline display name of the plugin
    def author = "Eugene Munyaneza"
    def authorEmail = "emunyaneza@clintonhealthaccess.org"
    def description = '''\
CHAI locations plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://github.org/uginm102/grails-chai-locations"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "BSD3"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Clinton Health Access Initiative", url: "http://www.clintonhealthaccess.org/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ 
		[ name: "Jean Kahigiso", email: "jkahigiso@clintonhealthaccess.org" ],
		[ name: "Sue Lister", email: "slister@clintonhealthaccess.org" ],
		[ name: "François Terrier", email: "fterrier@clintonhealthaccess.org" ]
	]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "http://github.com/uginm102/grails-chai-locations" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
