package org.jnorthr.dds;

import groovy.text.SimpleTemplateEngine;

/*
* A set of supporting methods to build Spock fixtures 
*/
@groovy.transform.Canonical
public class SpockTemplateEngineSample
{    
    def binding = [now: new Date(), author: 'Jim Northrop',method:'test say() method w/true boolean flag', pack:'org.jnorthr.dds', module:'Sample',num:1, title:'This class tests methods used by other classes']
    
    // get OS system property
    // ex.: export spockoutputpath='/Users/jimnorthrop/Dropbox/Projects/DDS-Decoder/src/test/groovy/org/jnorthr/dds'
    def path = System.getenv("spockoutputpath"); 
    
    // get Java JVM property 
    String property = "user.home";
    String homeDir = System.getProperty(property);
    // System.getenv("PE_CONF_PWD");
 
     def engine = new SimpleTemplateEngine()
   
    // a template of a complete spock spec module in groovy
    def skeleton = """// package \${pack}
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Title
 
@Title("\${title} - created on \${now} by \${author}")   
class \${module}Spec extends Specification {
    // -----------------------------
    // Fields
    // \${module} obj = new \${module}()
    // @Shared sharedObject = new \${module}();
    
    // -----------------------------
    // Helper methods
     
    // -----------------------------
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

    // -----------------------------
    // Feature methods     
\${features}
} // end of class
"""
    
    // a template of a complete spock feature in groovy; replacement values come from the binding
    def feature = """    
    // -----------------------------
    // feature method - included on \${now} by \${author}   
    def "\${method}"() {
        given: \"build one of these \${module} objects \"
            boolean tf = false;
            \${module} obj = new \${module}()
            
        when: \"we say something\"
              tf = obj.say("\${module} \${method}", true);
              
        then: \"we see this result \"
              tf==true;
    } // end of test    
"""


   /**
    * Constructor.
    */
    public SpockTemplateEngineSample()
    {
        super();
        println "Starting SpockTemplateEngineSample default constructor";
        if (path==null) { path = homeDir; }
        prepare();
    } // end of constructor


   /**
    * Identify name of script to be written plus a text description of the Spock feature method name
    *
    * @param module Value of output script name to be written to.
    * @param method Spock feature method name describing which method of the target class.
    * @return undefined.
    */
    public prepare(String module, String method)
    {
        binding['module'] = module;
        prepare(method);
    } // end of method

   /**
    * Declare a text description of the Spock feature method name
    *
    * @param method Spock feature method name describing which method of the target class.
    * @return undefined.
    */
    public prepare(String method)
    {
        binding['method'] = method;
        prepare();
    } // end of method
    

   /**
    * Construct and translate the template using values from the 'binding' object to useful text string. This result is
    * copied into the 'binding' using a key of 'features'.
    *
    * @return undefined.
    */
    public prepare()
    {
        def features = getFeature(feature);
        binding << [features:features];
    } // end of method
    

   /**
    * Construct and translate a script skeleton template using values from the 'binding' object into useful text string. This result is
    * copied into the 'binding' using a key of 'script'. A dump method writes the goods to disk
    *
    * @return undefined.
    */
    public produce()
    {
        String skel = getFeature(skeleton);
        binding << [script:skel]
        dump();
    } // end of method
    
    
   /**
    * Construct an output script file name, checking to see if it exists. If not, the script is written to disk as UTF-8 using the module name. 
    * This result is added to using text from the 'binding' using a key of 'features'.
    *
    * @return undefined.
    */
    public dump()
    {
        def fn = "${path}${binding['module']}Spec.groovy"
        boolean yn = chkobj(fn);
        println "dump() to file ${fn} - does it exist? "+yn;

        def file1 = new File(fn)

        // Use a writer object to make a Spock Spec script with no features:
        if (!yn) file1.withWriter('UTF-8') 
        { writer ->
                writer.writeLine(binding['script']) 
        } // end of writer
        
        // Use a writer object to add text string from the binding to end of existing script file
        if (yn) 
        {     
            def txt = file1.text;
            def i = txt.lastIndexOf("}");
            if (i>-1)
            {
                def tx2 = txt.substring(0,i)+binding['features']
                tx2+='\n'
                tx2+=txt.substring(i).trim();
                
                file1.withWriter('UTF-8') 
                { writer ->
                    writer.writeLine(tx2) 
                } // end of writer
            } // end of if
                            
        } // end of reader
        
    } // end of method
    
        
    
   /**
    * Does this file exist ? A static method to onstruct a file object using 'module' value from the 'binding' object. This object is
    * checked to see if it is present.
    *
    * @param module Value of script name to be tested for existence.
    * @return boolean true if file is present or else false if not present.
    */
    public static boolean chkobj(String module)
    {
        def file1 = new File(module)
        return file1.exists()
    } // end of method
        
        
   /**
    * Create a text description of the Spock feature method
    *
    * @param templatestring Holds the groovy simple template string to be translated.
    * @return String is the translated result of the input template plus values from the binding
    */
    public String getFeature(def templatestring)
    {
        def output = engine.createTemplate(templatestring).make(binding).toString()
        return output.toString()
    } // end of getSpock()


   /**
    * Main method to test this class
    *
    * @param args Standard list of Strings, possibly empty, to feed the target class.
    * @return Void.
    */    
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        println "This is a test harness for the SpockTemplateEngineSample"
        SpockTemplateEngineSample obj = new SpockTemplateEngineSample()
        println "-----------------------------------------"
        println "SPOCK_OUTPUT_PATH is [${obj.path}] and tempDir=[${obj.homeDir}]"
        
        println "-----------------------------------------"
        obj.produce();
        
        obj.prepare("This is a single prepare method");
        obj.produce();
        
        obj.prepare("Budweiser", "This is a double parm prepare method");
        obj.produce();

        println "--- the end ---"
    } // end of main
} // end of class