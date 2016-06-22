package org.jnorthr.dds.utilities;

import groovy.transform.ToString
import org.jnorthr.dds.utilities.tools.*

/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** 
 * ConfigHandler class description
 *
 * Groovy class to manage the .make.properties groovy configuration file in Json format and the environmental binding. 
 */  

 @ToString(includeNames=true) 
 public class ConfigHandler
 {
    /** an O/S specific char. as a file path divider */
    String fs = java.io.File.separator;

    /** an O/S specific location for the user's home folder name */ 
    String userhome = System.getProperty("user.home");
    
    /** an O/S specific name */ 
    String os = System.getProperty("os.name");


   /** 
    * Variable configuration Handle to external properties file.
    */  
    def configuration; 


   /** 
    * Variable skeleton Json configuration stream to write to n external properties file.
    */  
    def template = """app {
os='${os}'
}
"""

     /** Variable handle to the check object utility that confirms if an object exists in current directory  */
     ChkObj co;


    /** 
    * Variable binding Describes pairs of key/values used in the template skeleton to clone from.
    */  
    Map binding = [
             auditFlag: false,
             gradleFlag: false, // true makes src/main/groovy etc. foldrs (not build.gradle,etc) - requires installed gradle tool to work
             gitFlag: false, // true to enroll this project in github repo - requires installed git tool to work
             testFlag: false,   // if gradleFlag then testFlag=true to gen src/test/groovy
             asciidocFlag: false,   
             scriptsFlag: false,  // to generate gradle scripts only - not the folders like src/... 
             wrapperFlag: false,  // to generate a gradle wrapper tool within the folders like src/... 
             travisFlag: false,   
             projectfoldername:"",
             pathname : "",
             packagename : "",
             classname : "",
             filesuffix : "",
             fullpackagename:"", 
             outputpathname : "",
             outputfilename : "",

             templatepathname : "resources${fs}main${fs}",
             templatefilename : "resources${fs}main${fs}skeleton.groovy",

             // test variables follow
             testpackagename : "",
             testclassname : "",
             testfilesuffix : "",
             testoutputpathname : "",
             testoutputfilename : "",

             testtemplatepathname : "resources${fs}test${fs}",
             testtemplatefilename : "resources${fs}test${fs}skeleton.groovy",

             propertyfilename : ".make.properties",
             buildgradlename : "resources${fs}build.gradle",
             travisfilename : "resources${fs}.travis.yml",
             gitignorefilename : "resources${fs}gitignore",
             year: 2015
     ] // end of binding



   // ===================================================================
   /** 
    * Default Constructor 
    * 
    * @return ConfigHandler object
    */     
    public ConfigHandler()
    {
        configuration =  new ConfigSlurper().parse(template.toString())
        //put("auditFlag",true);
        setSuffix();
    co = new ChkObj();
        boolean flag = readConfig(this.binding);
        say "flag = $flag for obj.readConfig(obj.binding);"
    } // end of constructor


   /** 
    * Non-Default Constructor with optional flag to produce joblog
    * 
    * @return ConfigHandler object
    */     
    public ConfigHandler(boolean audit)
    {
        configuration =  new ConfigSlurper().parse(template.toString())
        put("auditFlag",audit);
        setSuffix();
    co = new ChkObj();
        boolean flag = readConfig(this.binding);
        say "flag = $flag for obj.readConfig(obj.binding);"
    } // end of constructor



   /** 
    * Method to get a non-boolean String value from the binding using string key as binding entry key.
    * 
    * @param value of key to binding map
    * @return value of the binding entry or null if no such key exists in binding
    */     
    public String get(String key)
    {
       return (binding?.containsKey(key)) ? binding[key] : null;
    }  // end of method


   /** 
    * Method to get a non-blank String value representing the gradle/maven folder structure.
    * 
    * @return value of the maven/gradle folder structure
    */     
    public String getGradlePath()
    {
       return getGradlePath(true);
    }  // end of method

    
   /** 
    * Method to get a non-blank String value representing the gradle/maven folder structure.
    * 
    * @param true to generate a 'main' structure or false for a 'test' structure
    * @return value of the maven/gradle folder structure
    */     
    public String getGradlePath(boolean flag)
    {
        String gp = "";

        if (binding.gradleFlag)
        {
        String type = (flag)  ? "main" : "test";
        gp = "src${fs}${type}"+fs+binding.filesuffix;
    } // end of if
    
       say "doing getGradlePath($flag)=[${gp}]"
       return gp;
    }  // end of method
    

   /** 
    * Method to get a boolean value from the binding using key as binding entry key for a binary flag.
    * 
    * @param value of key to binding map
    * @return true if the binding entry is equal to the string 'true' else returns false
    */     
    public boolean getFlag(String key)
    {
        return ( binding[key] ) ? true : false;
    }  // end of method


   /** 
    * Method to get a gradleFlag as a folder-builder only flag
    * 
    * @return current value of binding.gradleFlag
    */     
    public boolean needsGradleFolders()
    {
        return binding['gradleFlag'];
    }  // end of method


   /** 
    * Method to get a class description from an external file.
    * 
    * @return current content of the external file
    */     
    public String getDesc()
    {
        def dt = userhome+fs+".make.txt";  

        return ( co.chkobj(dt) ) ? new File(dt).text : "";
    }  // end of method



   /** 
    * Method to translate a stored class description into a javadoc compatible equivalent.
    *
    * <p>The input comes from getDesc() method that reads ~/.make.txt file if it exists else blank
    * 
    * @return javadoc equivalent of input
    */     
    public convertDesc()
    {
    // convert \ into \\
    def txt = getDesc();
        // disallow $ symbols
        txt = txt.replaceAll('\\$','§')
        txt = txt.replaceAll('\'','"')
    
    def payload = "";

    boolean flag = false
    txt.eachLine{lns->
        def ln = lns.trim();
        if ( ln.endsWith('\n') ) { ln = ln.substring( 0, ln.size() ) }; 
        
        if (!flag)
        {
            flag = (ln.size() < 1) ?true:false;
                    if(!flag) {payload += " * "+ln+'|'}
            } // end of if
            else
            {
                    if (ln.size() > 0)
                    {
                        String t = (flag) ? " * <p>"+ln : " * "+ln;
                        flag = false;
                        payload+= t+'|';
                    } // end of if
                    else
                    {
                        //payload+='|'
                    } // end of else
            } // end of else
    } // end of each
    
    def payload2 = payload.replaceAll('§','\\$');

    return payload2;
    } // end of method



   /** 
    * Method to store a class description into an external file.
    * 
    * @param string value of the description for this class
    * @return current content of the external file
    */     
    public boolean putDesc(String newtext)
    {
        def dt = userhome+fs+".make.txt";
        new File(dt).withWriter('UTF-8') { writer ->
        writer.write(newtext)
    } // end of writer 
        return true;
    }  // end of method


   /** 
    * Method to put a string value into the binding
    * 
    * @param string value of key to binding map entry
    * @param string value to store in binding map
    * @return value of the binding entry just stored
    */     
    public String put(String key, String val)
    {
        binding[key] = val;
        return binding[key];
    }  // end of method


   /** 
    * Method to put a binary value into the binding
    * 
    * @param string value of key to binding map entry
    * @param binary value to store in binding map
    * @return value of the binding entry just stored
    */     
    public String put(String key, boolean val)
    {
        binding[key] = val;
        return binding[key];
    }  // end of method


   /** 
    * Method to put the pathname output folder string value into the binding
    * 
    * @param string value of key to binding map entry
    * @param string value of output pathname entry to store in binding map
    * @return value of the binding entry just stored
    */     
    public String fix(String key, String val)
    {
        put(key,val);
        binding['pathname'] = val;
        binding['outputpathname'] = val;
        binding['testoutputpathname'] = val;        
        return binding[key];
    }  // end of method

   /** 
    * Method to put a chooser value into the binding using pathname and also the filename as binding entry keys. Then base64 encode the pathname.
    * 
    * @param pn is the pathname for key outputpathname into binding map 
    * @param pn is the full filename for key outputfilename into binding map
    * @return true if binding map was serialized to .make.properties successfully
    */     
    public boolean update(String pn, String fn)
    {
        say "Config.Handler.update($pn, $fn)"
        binding['pathname'] = pn;
        binding['outputpathname'] = pn;
        binding['testoutputpathname'] = pn;
        binding['outputfilename'] = fn;
        boolean ok = true;
        try{
        writeConfig();
    }
    catch(any)
    {
        ok = false;
    } // end of catch
    
        return ok;
    }  // end of method


   /** 
    * Method to print audit log.
    * 
    * @param text to show in log
    * @return void
    */     
    public void say(txt)
    {
        if (binding['auditFlag']) { println txt; }
    }  // end of method


   /** 
    * Method to determine the proper suffix for each file name for both the main and the test outputs. 
    * It uses the current ending values of templatefilename and testtemplatefilename to deduce that choice or 
    * defaults to groovy as output script type.
    * 
    * @return void
    */     
    public void setSuffix()
    {
        int i = templatefilename.lastIndexOf('.');
        String sfx = (i > 0) ? templatefilename.substring(i+1) : "groovy" ;
        put('filesuffix',sfx);

        i = testtemplatefilename.lastIndexOf('.');
        sfx = (i > 0) ? testtemplatefilename.substring(i+1) : "groovy" ;
        put('testfilesuffix',sfx);

    }  // end of method


   /** 
    * Method to declare one suffix for each file name for both the main and the test outputs. 
    * 
    * @return void
    */     
    public void setSuffix(String name)
    {
        int i = name.lastIndexOf('.');
        String sfx = (i > 0) ? name.substring(i+1) : name;
        put('filesuffix',sfx);
        put('testfilesuffix',sfx);

    }  // end of method



   /** 
    * Method to translate Json encoded external property values in .make.properties file into internal binding variables.
    * 
    * Note that values used for the local file system have both \ and / char.s carried in the external file as | char.s
    * and translated on-the-fly during load time into the file separator for the local file system.
    *
    * @param binding map of internal variables
    * @return true if content of internal variables was loaded
    */     
    boolean readConfig(binding)
    {
        say "\n--- reading configuration ---"
        boolean flag = false;

        String props = userhome+fs+binding['propertyfilename'];  
        say "readConfig() looking for file named "+props;
    boolean yn = new File(props).exists();  //co.chkobj(props);
        if ( yn )
        {                
            def config = new File(props).text;
            say """found $props file holding :\n${config}"""
            
            // Read configuration.
            configuration = new ConfigSlurper().parse(config)
            configuration.app.each{k,v-> 
            
                if(v.toLowerCase().equals("true")) {binding[k]=true}
                else
                if(v.toLowerCase().equals("false")) {binding[k]=false}
                else
                {
                    binding[k]=v.toString().collectReplacements(replacewithPlatformSpecificChars)
                    flag = true;

                    if (binding[k]) 
                    {
                        binding[k] = v.toString().collectReplacements(replacewithPlatformSpecificChars)
                        //say "===> has binding key of [${k}] now = [${binding[k]}]"
                    } // end of if
                } // end of else
            } // end of each


            // Check values for app. configuration properties.
            setSuffix();
            return flag
        } // end of if

        // since .make property file is missing we build one here depending on the o/s of this host
        else
        {
            say "\n\nProperty file does not exist ---\ngonna make a new template:[${template.toString()}]"
            configuration =  new ConfigSlurper().parse(template.toString())
            setSuffix();
            put('propertyfilename', '.make.properties');
            put('pathname', userhome);
            writeConfig()
        } // end of else
        
        return flag
    } // end of readConfig


   /** 
    * Closure Method to build a config tool.
    * 
    * @return configSlurper
    */     
    def createConfig = { env ->
        def configSlurper = new ConfigSlurper()
        return configSlurper
    } //end of method


   /** 
    * Method to write internal variables held in a binding.
    * 
    * @return true if content of internal variables was loaded
    */     
    boolean writeConfig()
    {
        boolean flag = false;
        String fn = userhome+fs+binding['propertyfilename'];  
        say "--- writing configuration to $fn ---"

        File fi = new File(fn);
        if (!fi.exists()) 
        {
            fi.write "// The | char. is replaced by local os dependent path separator at properties load time\n\r"
        } // end of if
        else
        {
            String na = fi.canonicalFile.toString();
            say "File $na exists"
        } // end of else

        binding.each{k,vi->
            def v = vi.toString().collectReplacements(replacement)
            //say "... binding k=$k and v=[$v]"
            configuration.app."${k}"="'${ v }'"
        } // end of binding

         
        fi.withWriter('UTF-8') { writer ->
            writer.write "// The | char. is replaced by local os dependent path separator at properties save & load time\n"
            configuration.writeTo(writer)
            flag = true;
        } // end of withWriter

        return flag
    } // end of writeConfig


   /** 
    * Closure to translate string value with o/s specific file separator into a | pipe-encoded string equivalent.
    * 
    * @param val is the string value to translate
    * @return a single char. equivalent
    */     
    def replacement = 
    {
    if (it == '/') 
        {
            '|'
        } 
        else 
        if (it == '\\')  // needs 2 \\ chars as escaped value \
        {
            '|'
        } 
        else 
        {
            null
        } // end of else    
    } // end of closure


   /** 
    * Closure to translate string value with a | pipe-encoded string into o/s specific file separator equivalent.
    * 
    * @param val is the string value to translate
    * @return a single char. equivalent
    */     
    def replacewithPlatformSpecificChars = 
    {
    if (it == '|') 
        {
            fs
        } 
        else 
        {
            null
        }    
    } // end of closure


   // ======================================
   /** 
    * Static method to run class tests.
    * 
    * @param args Value is string array - possibly empty - of command-line values. 
    * @return void
    */     
    public static void main(String[] args)
    {
        println  "\n\n================================================================="
        println "--- starting ConfigHandler ---"

        ConfigHandler obj = new ConfigHandler(true);

        obj.say "ConfigHandler = [${obj.toString()}]"

        obj.say  "\n===========================\n"
        obj.say  obj.binding
        obj.say  "===========================\n\n"
        obj.writeConfig();
        
        obj.say  ""
        obj.say  obj.binding
        obj.say  "===========================\n\n"
    def xxx = obj.convertDesc();
        obj.say  "[${xxx}]\n===========================\n\n"

        println "--- the end of ConfigHandler ---"
    } // end of main

} // end of class