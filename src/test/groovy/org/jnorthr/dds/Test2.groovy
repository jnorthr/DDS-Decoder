package org.jnorthr.dds;
@groovy.transform.Canonical
class Test2 {
String name ="";
int age = 0;
boolean flag = false
    String toString()
    {
        return "name=$name age=$age flag=$flag"
    }
    
    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"

        // GroovyBean: Groovy creates a constructor
        def dds = new Test2([age: 66, name: 'jim'])
        
        println "Test2:"+dds
        println "read: http://mrhaki.blogspot.fr/2009/09/groovy-goodness-using-lists-and-maps-as.html"
        
        // Explicit coersion with as keyword:
        def s2 = [age: 36, name: 'mrhaki'] as Test2
        println "Test2:"+s2


        // Implicit coersion (by type of variable):
        Test2 s3 = [age: 33, name: 'mrshaki']
        println "Test2:"+s3

        println "======================\nEnd of Job ---"

        //System.exit;        
    }   // end of psvm
        
}    // end of Field