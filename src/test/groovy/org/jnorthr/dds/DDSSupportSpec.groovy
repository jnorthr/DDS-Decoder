package org.jnorthr.dds;

import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Title
 
@Title("This class tests methods used by other classes")   
class DDSSupportSpec extends Specification {
    // Fields
    // DDSSupport obj = new DDSSupport()
    // @Shared sharedObject = new DDSSupport();
    
    // Helper methods
     
    // Feature methods
    // feature method 1
    def "test say() method w/true boolean flag"() {
        setup: "build one of these DDSSupport objects "
            boolean tf = false;
            DDSSupport obj = new DDSSupport("hi","kids")
            
        when: "we say something"
              tf = obj.say("DDSSupport test say() method w/true boolean flag", true);
              
        then: "we see this result "
              tf==true;
    } // end of test    
     
    // Fixture methods 
    // The setup() method is invoked before every feature method
    def setup() {
        println "Setup"
    }
 
    // The cleanup() method is invoked after every feature method.
    def cleanup() {
        println "Clean up"
    }
 
    // The setupSpec() method is invoked before the first feature method is invoked
    def setupSpec() {
        println "Setup specification"
    }
    
    // The cleanupSpec() method is invoked after all feature methods have been invoked
    def cleanupSpec() {
        println "Clean up specification"
    }    
} // end of class