// see more examples in above link to include a file filter
package org.jnorthr.dds.utilities;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.jnorthr.dds.utilities.ConfigHandler;
import org.jnorthr.dds.utilities.tools.ChkObj;
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
 * Chooser class description
 *
 * Groovy class to choose a local file system path name to a target. If calling module provides a handle to a ConfigHandler, then the selected target 
 * path is re-written back to that as binding[pathname]
 * 
 */  
 public class Chooser
 {
    /** an O/S specific char. as a file path divider */
    String fs = java.io.File.separator;

    /** an O/S specific location for the user's home folder name */ 
    String home = System.getProperty("user.home");
    
   /** 
    * Variable fc - A handle to the java file chooser.
    */  
    JFileChooser fc;


   /** 
    * Variable file A file handle to the selected target.
    */  
    File file;


   /** 
    * Variable handle to the current environmental file named .make.properties.
    */  
    ConfigHandler ch = new ConfigHandler();


    /** true to call ConfigHandler.rewrite after changing the pathname else it's assumed the calling module passed ina ch handle that we write to */
    boolean rewriteFlag = true;

   /** 
    * Variable chooserpath Value pointing home folder or most recently selected target folder.
    */  
    String chooserpath=ch.get('pathname');


    /** is the chosen file a directory folder ? */
    boolean folder = false;

    /** true to print a joblog ? */
    boolean auditFlag = false;

   /** 
    * Default Constructor 
    * 
    * @param string indicating the starting folder location for this chooser to begin at
    * @return Chooser object
    */     
    public Chooser()
    {
        fc = new JFileChooser(chooserpath);
        // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); FILES_AND_DIRECTORIES FILES_ONLY
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Select a File or Folder");
    } // end of constructor


   /** 
    * Non-Default Constructor 
    * 
    * @param string indicating the starting folder location for this chooser to begin at
    * @return Chooser object
    */     
    public Chooser(String newpath)
    {
        chooserpath = newpath;
        fc = new JFileChooser(chooserpath);

        // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); FILES_AND_DIRECTORIES FILES_ONLY
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Select a File or Folder");
    } // end of constructor


   /** 
    * Non-Default Constructor 
    * 
    * @param string indicating the starting folder location for this chooser to begin at
    * @return Chooser object
    */     
    public Chooser(ConfigHandler nch)
    {
    this.ch = nch;
        chooserpath = ch.get("pathname");
        fc = new JFileChooser(chooserpath);

        // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); FILES_AND_DIRECTORIES FILES_ONLY
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Select a File or Folder");
    } // end of constructor


   /** 
    * Method to print audit log.
    * 
    * @param text to show in log
    * @return void
    */     
    public void say(txt)
    {
        if (auditFlag) { println txt; }
    }  // end of method


   /** 
    * Method to only allow user to choose files but not folders.
    * 
    * @return void
    */     
    public void selectFiles()
    {
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        fc.setDialogTitle("Select only a File");
    }  // end of method


   /** 
    * Method to only allow user to choose folders but not files.
    * 
    * @return void
    */     
    public void selectFolders()
    {
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        fc.setDialogTitle("Select only a Folder");
    }  // end of method


   /** 
    * Method to get user choice of file.
    *
    * the final path of the chosen file can be found in the chooserpath variable
    * 
    * @return the filename of the user selected target
    */     
    public String getChoice()
    {
        String chosenFileName = "";

        folder = false;
        say "---> b4 showOpenDialog()"
        int result = fc.showOpenDialog( null );
        say "---> after showOpenDialog() result="+result

        switch ( result )
        {
           case   JFileChooser.APPROVE_OPTION:
                  file = fc.getSelectedFile();
                  chooserpath =  fc.getCurrentDirectory().getAbsolutePath();

                  if (file.isDirectory()) 
                  {
                      chooserpath = file.toString().trim();
                  ch.put("pathname",chooserpath);
                      ch.put("filename", "")
                      folder = true;
                      chosenFileName = chooserpath;
                      say "Approved got Dir.= chooserpath="+chooserpath+" chosen file name=[$chosenFileName]";
                  }
                  else
                  {
                    // keep chooserpath value
                    chosenFileName = file.toString().trim();           
                    int j = chosenFileName.lastIndexOf(fs)
                    if ( j > -1 ) chosenFileName = chosenFileName.substring(j+1).trim();         

                    say "getChoice() = chooserpath="+chooserpath+" chosen file name="+chosenFileName;
                ch.put("pathname",chooserpath);
                    ch.put("filename",chosenFileName)
                  } // end of else
                  
                  if (rewriteFlag)
                  {
                      ch.writeConfig();
                  } // end of if

                  break;

           case   JFileChooser.CANCEL_OPTION:
           case   JFileChooser.CLOSED_OPTION:
                  chosenFileName = null;
                  break;

           case   JFileChooser.ERROR_OPTION:
                  chosenFileName = null;
                  break;
        } // end of switch

        return chosenFileName;
    }  // end of method



   /** 
    * Method to display internal variables.
    * 
    * @return formatted content of internal variables
    */     
    public String toString()
    {
        return """home=${home}
fs=${java.io.File.separator}
chooserpath=${chooserpath}
file=${file.toString()}
fc=${fc.toString()}
rewriteFlag=${rewriteFlag}
folder=${folder}
auditFlag=${auditFlag}
"""
    }  // end of method


   /** 
    * Method to return the answer to the question: was the chosen file a directory folder?
    * 
    * @return true if selected file was a folder else false if not a folder or no choice made yet
    */     
    public boolean isFolder()
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
        println "--- starting Chooser ---"
        String home = System.getProperty("user.home");

        Chooser obj = new Chooser();
        println "--- getChoice() ---"
    obj.selectFolders()
        String fn = obj.getChoice();
        println "chosen filename is =[$fn] and chooserpath=[${obj.chooserpath}]"
        println "was ${obj.chooserpath} a folder ? "+obj.isFolder();
        println "\nChooser = [${obj.toString()}]"
        println "\n----------------------\nGet new Chooser using the pathname in the ConfigHandler binding"
        
        ConfigHandler ch = new ConfigHandler();
        obj = new Chooser(ch);
        obj.selectFiles();
        println "--- getChoice() ---"
        fn = obj.getChoice();
        println "chosen filename is =[$fn] and chooserpath=[${obj.chooserpath}]"


        println "--- the end of Chooser ---"
        System.exit(0);
    } // end of main

} // end of class