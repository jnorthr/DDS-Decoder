package org.jnorthr.dds.utilities.tools;
import groovy.transform.*;

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
 * ChkObj class description
 *
 * A class to confirm an object exists (or not) within a declared directory folder. 
 * This name is provide to the class constructor.
 *
 * 
 */ 
 @Canonical 
 public class ChkObj
 {
    /** an O/S specific char. as a file path divider */
    String fs = java.io.File.separator;

    /** an O/S specific location for the user's home folder name */ 
    String home = System.getProperty("user.home");
    
   /** 
    * Variable title Value of a variable.
    */  
    String title = "";


   /** 
    * Variable holding name of directory to be examined for existing objects.
    */  
    String filename = home;

   /** 
    * Variable holding name of directory to be examined plus name of object.
    */  
    String fn = "";

   /** 
    * Variable holding name of object.
    */  
    String object = "";


   /** 
    * Variable boolean flag where true when name of directory to be examined plus name of object does exist.
    */  
    boolean yn = false;


   /** 
    * Variable boolean flag where true when existing filename is a directory
    */  
    boolean folder = false;


   /** 
    * Default Constructor 
    * 
    * @return ChkObj object
    */     
    public ChkObj()
    {
        title = "running ChkObj constructor"
    } // end of constructor


   /** 
    * Non-Default Constructor 
    * 
    * This version needs a path to the target folder and it must actually exist
    * @return ChkObj object
    */     
    public ChkObj(String name)
    {
        title = "running ChkObj constructor"
        filename = name;
        File fi = new File(filename);
        assert true==fi.exists(), "Directory $name does not exist"
        assert true==fi.isDirectory(),"$name is not a directory folder"
    } // end of constructor


   /** 
    * Method to display internal variables.
    * 
    * @return formatted content of internal variables
    */     
    public String toString()
    {
        return """
title=${title}
user.home=${home}
pathame=${filename}
object=${object}
fn=${fn}
yn=${yn}
java.io.File.separator=${java.io.File.separator}
"""
    }  // end of string


   /** 
    * Method to see if an object exists in the known directory.
    * 
    * @param filename to be confirmed as existing within the folder declared in the constructor. 
    * @return true if object exists in this folder
    */     
    public boolean has(String object)
    {
    	this.object = object;
    	fn = filename + fs + object;
    	def fi = new File(fn);
        yn = fi.exists();
        folder = false;
	if (yn) { folder = fi.isDirectory() }
        return yn;
    }  // end of method


   /** 
    * Method to see if this object exists.
    * 
    * @param full filename with path info to be confirmed as existing. 
    * @return true if object exists in this folder
    */     
    public boolean chkobj(String fn)
    {
    	def fi = new File(fn);
        yn = fi.exists();
        folder = false;
	if (yn) { folder = fi.isDirectory() }
        return yn;
    }  // end of method


   /** 
    * Method to see if this object is a directory folder.
    * 
    * @return true if this folder is a directory or false if not a directory or does not exist
    */     
    public boolean isDir()
    {
        return folder;
    }  // end of method


   // ======================================
   /** 
    * Method to run class tests.
    * 
    * @param args Value is string array - possibly empty - of command-line values. 
    * @return void
    */     
    public static void main(String[] args)
    {
        println "--- starting ChkObj ---"
	String home = System.getProperty("user.home");
        String fs = java.io.File.separator;

        ChkObj co = new ChkObj(home);

	boolean tf = co.has(".bashrc");
	println "Does .bashrc exist ? "+tf;        
        println "ChkObj = [${co.toString()}]"
        //obj = new ChkObj("${home}${fs}doesnotexist");

        String folder = "${home}${fs}Dropbox${fs}Constructor";
        co = new ChkObj(folder);
	tf = co.has("build.gradle");
	println "Does build.gradle exist ? "+tf;        
        println "ChkObj = [${co.toString()}]"

	tf = co.has("gradlew");
	println "Does gradlew exist ? "+tf;        
        println "ChkObj = [${co.toString()}]"

	tf = co.has("nosuchfile");
	println "Does nosuchfile exist ? "+tf;        
        println "ChkObj = [${co.toString()}]"

        co = new ChkObj();
	tf = co.chkobj(folder);
	println "Does $folder exist according to chkobj() method ? "+tf; 
	println "is $folder a directory ? "+co.isDir();       
        println "ChkObj = [${co.toString()}]"
        
        
    	Map binding = [propertyfilename : ".make.properties"]
	def props = home+fs+binding['propertyfilename'];  
        println "readConfig() looking for file named "+props;
        tf = co.chkobj(props) 
        println "ChkObj = [${co.toString()}]"

        println "--- the end of ChkObj ---"
    } // end of main

} // end of class