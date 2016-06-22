/*
* A set of supporting methods for DDS 
*/
package org.jnorthr.dds;

import org.jnorthr.dds.fields.Field;

// allow key declarations
@groovy.transform.Canonical
class DDSSupport
{
    def audit = true
    def flag = false
    def logflag =  false

    def log
    def logfilepath = System.getProperty("LOGFILEPATH");
    def logfilename = "log.txt"
    // /Users/jimnorthrop/Dropbox/Projects/DDS-Decoder/src/main/groovy/org/jnorthr/dds
    def ddsfilepath = System.getProperty("DDSFILEPATH");

    // related to FRF container    
    boolean hasValidFRF = false;
    
    // name of field ref file
    def RefFile="";

    // Object handle to DDS for the loaded Field Reference File
    def FRF


    // path constructor
    public DDSSupport(String path, String ddspath)
    {
    	super();
    	logfilepath = path;
    	ddsfilepath = ddspath;
    	println "Starting DDSSupport constructor at path "+logfilepath;
    } // end of constructor


	// produce log with CR/LF
    boolean say(tx) 
	{ 
		return say(tx, true)
	} // end of say

	// extended say to write newline (true) when log of text; 
    boolean  say(tx, boolean newline) 
	{ 
		if (audit) 
		{

		    if (logflag)
		    {
		    	log.append(tx) 			// write to target
		    	if (newline) { log.append("\n") }
		    	return true
		    } // end of if

		    else
		    {
		    	logflag = true;
				log = new File(logfilepath+logfilename) // construct File

			    log.append("\n===================================================================\n")
			    log.append("writing :"+logfilepath+logfilename+"\n")
				log.append(tx) 			// write to target
		    	if (newline) { log.append("\n") }
		    	return true
		    } // end of else

		} // end of if audit
		
		else
			return false;
	} // end of say

    // output a bit of text without a newline ending
    boolean saysomething(tx) { return say(tx, false); }



    // pull field attributes from the external field reference file
    public getRefField( Field f, def text )
    {
                def fi = FRF.getRef(text)
                def m = "Field Ref File $RefFile lookup for <$text>" 
                if ((fi!=null))
                {
                    m += " - found it "
                    f.metadataUsage = fi.metadataUsage
                    f.size    = fi.size
                    f.decimals = fi.decimals
                    f.type    = fi.type
                    f.usage   = fi.usage
                    f.alias   = fi.alias
                    f.text    = fi.text
                    f.colhdg1 = fi.colhdg1
                    f.colhdg2 = fi.colhdg2
                    f.colhdg3 = fi.colhdg3   
                    f.edtwrd  = fi.edtwrd
                    f.edtcde  = fi.edtcde
                    f.comment = fi.comment
                    // key related values are not copied over
                }
                else
                {
                    m += "${text.trim()} not found in ${RefFile}"
                } // end of if

                say m    
    } // end of method
    
    
    // find each piece of the column heading; three max of 30 char.s max each
    public decodeHeadings(Field f, def text)
    {
        def r2 = text.trim()
        if (r2.startsWith("'") && r2.endsWith("'") )
        {
            int c = 0;
            // count apostrophies
            r2.each{x-> if (x=="'") { c+=1; } }
            int m = c % 2

            //say "there are $c apostrophes and $m spares; there are ${c/2} words"
            // bail out if not even number of 's
            if (m>0) return
            
            def sb = ""
            boolean inField = false
            int word = 0;
            r2.each{x-> 
                if (x=="'") 
                { 
                    c+=1; 
                    if (inField)
                    {
                        inField = false;
                        word+=1;
                        if (word in 1..3)
                        {
                            say "... found COLHDG$word "+sb.trim();
                        } // end of if
                        
                        switch(word)
                        {
                            case 1: f.colhdg1 = sb.trim(); break;
                            case 2: f.colhdg2 = sb.trim(); break;
                            case 3: f.colhdg3 = sb.trim(); break;                        
                        } // end of switch
                        sb="";
                    } // end of if
                    else
                    {
                        inField = true;
                    } // end of else                    
                }    // end of if
                else
                {
                     if (inField) sb += x;                   
                } // end of else
                 
            }    // end of each
            
        }    // end of if
        
    }    // end of method
    


    // take off surrounding quote marks then return a trimmed version
    public removeQuotes(def r)
    {
        def r2 = r.trim()
        if (r2.startsWith("'"))
        {
            int w = r2.substring(1).indexOf("'")
            def r3 = r2.substring(1,w+1)
            return r3.trim()
        }   // end of if

        else

        if (r2.startsWith('"'))
        {
            int w = r2.substring(1).indexOf('"')
            def r3 = r2.substring(1,w+1)
            return r3.trim()
        }    // end of if

        else

        {
            return r2
        } // end of else
                
    }    // end of method


    // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES,EDTCDE 
    public decodeSingleCommands(Field f, def t)
    {
        def main
        def text 
        def pre
        int r = 0

        say "... decodeSingleCommands("+t+")"
        def tokens = t.trim().toUpperCase().split().toList()
           
        tokens.each{word ->
            say "   word:"+word
            
            if (word.equals("DESCEND") & f.isKey )
            {
                say "... found DESCEND"
                f.descend = true
            } // end of if
            
        } // end of each
        say "---"
    } // end of method


    // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES,EDTCDE 
    public decodeText(Field f, def t, boolean fieldreffile)
    {
    	// don't do blank lines
    	if (t.trim().size() < 1) return

        def main
        def text 
        def pre
        int at = 0
        int r = 0
        int s = 0
        boolean flag = true
        def t2 = t
        
        say "decodeText($t)"
        decodeSingleCommands(f,t2)
        
        // break tokens into ) delimited words
        def tokens = t.trim().split(/\)/).toList()
        
        // only look for REF declaration in 'R'ecord lines
        if (f.isRecord)
        {
   	        // look for and process REF first but only on isRecord lines
	        saysomething "\n... looking for REF declarations:"
	        
	        tokens.each{wd ->
	            saysomething "wd=<$wd> sz=${wd.trim().size()} first ( is at ${wd.indexOf('(')} f.isRecord=${f.isRecord}    "
	            if ( wd.trim().size() > 3 )
	            {
	                r = wd.indexOf("(")
	                if (r > 0)
	                {
	                    pre = wd.substring(0,r).trim()
	                    main = wd.substring(r+1).trim()    
	                    if (pre.equals("REF") ) 
	                    { 
	                        say "... token <$wd> pre=<$pre> main=<$main> "
	                        
	                        if (fieldreffile)
	                        {
	                            say "... found REF $main - but cannot use within a field reference file"                
	                        }
	                        else
	                        {
	                            say "... found REF $main"
	                            keepRefFile(main ) 
	                        }; // end of else
	                        
	                    };    // end of if pre.equals
	                    
	                } // end of if r>0
	                
	            };    // end of if
	                
	        } // end of each
	        
	        say ";"

        }; // end of if



        // only look for REFFLD declaration in Field lines
        if (f.isField)
        {
	        // look for and process REFFLD next but only on isField lines where a REF has already been declared, 
		// an 'R' reference code is in column 28 and the ref file does exist.
	        say "\n... looking for REFFLD declarations"
	        tokens.each{word ->
	            say "... tokens.each(<$word>)"
	            r = word.indexOf("(")
	            if (r > 0)
	            {
	                pre = word.substring(0,r).trim()
	                main = word.substring(r+1).trim()    
	                if (pre.equals("REFFLD") )
	                {   
	                    say "... token <$word> pre=<$pre> main=<$main> hasValidFRF=<${hasValidFRF}>"
	                    if (fieldreffile & !f.hasReference)
	                    {
	                        say "... found REFFLD $main - but cannot use within a field reference file"                
	                    }
	                    else
	                    {
	                        if (hasValidFRF && f.hasReference)
	                        {
	                            say "... found REFFLD $main"
	                            say "... going to look for <$main>"
	                            getRefField(f, main );
	                        } // end of if
	                        
	                    } // end of else
	                };    // end of if

	            }; // end of if r > 0

	        } // end of each
        }; // end of if



        say "\n... looking for other declarations"
        tokens.each{word ->
            r = word.indexOf("(")
            if (r > 0)
            {
                pre = word.substring(0,r).trim()
                main = word.substring(r+1).trim()    
                say "... token <$word> pre=<$pre> main=<$main> "
    
                // need to add logic here to digest REF first, REFFLD next, then others.
                // only process these on isField declarations
                switch(pre)
                {
                    case "TEXT" :   f.text = removeQuotes(main)
                                    say "... found TEXT "+f.text
                                    break;
                                    
                    case "ALIAS":   if (f.isField) 
                                    { 
                                        f.alias = removeQuotes(main) 
                                        say "... found ALIAS "+f.alias
                                    };
                                    break;
                                    
                    case "VALUES":   if (f.isField) 
                                    { 
                                        f.values = main
                                        f.hasValues = true 
                                        say "... found VALUES "+f.values
                                    };
                                    break;
                                    
                    case "COLHDG":  if (f.isField) 
                                    { 
                                        decodeHeadings(f, main ) 
                                    };
                                    break;

                    case "EDTCDE" : if (f.isField) 
                                    {
                                        f.edtcde = removeQuotes(main)
                                        say "... found EDTCDE "+f.edtcde;
                                    };
                                    break;                                    
                                                                    
                    case "EDTWRD" : if (f.isField) 
                                    {
                                        f.edtwrd = main
                                        say "... found EDTWRD "+f.edtwrd
                                    };
                                    break;                
                                                        
                    case "SELECT":  if (f.isField) 
                                    { 
                                        f.select = main
                                        f.hasSelect = true 
                                        say "... found VALUES "+f.select
                                    };
                                    break;
                                    
                    case "OMIT":  if (f.isField) 
                                    { 
                                        f.omit = main
                                        f.hasOmit = true 
                                        say "... found OMIT "+f.omit
                                    };
                                    break;




                } // end of switch        

            }; // end of if r > 0


        } // end of each
        
    }    // end of method


    // take off surrounding quote marks then return a trimmed version
    public keepRefFile(def r)
    {
        def r2 = "${ddsfilepath}/${r.trim()}"
        def rf = new File(r2)

        if (rf.exists())
        {
            RefFile = r2;
            say "-> Field Reference File will be "+r2
            FRF = new DDS(RefFile, true)
            hasValidFRF = true;
            return r2;  
        } // end of 
        else
		throw new FileNotFoundException(r2+" Reference File cannot be found or used");        
        
    }    // end of method


    // print content of this line
    // see:http://publib.boulder.ibm.com/iseries/v5r2/ic2928/index.htm?info/dds/rbafpmstddsover.htm
    public boolean show(txt)
    {
        def line=txt.padRight(80)    
        saysomething "<"
        saysomething line[0..4]
        saysomething ":"
        saysomething line[5] // form type , usually A
        saysomething ":"
        saysomething line[6]	// comment declaration if * coded
        saysomething ":"
        saysomething line[7..15] // conditioning indicators, ignored in PF and LF DDS
        saysomething ":"
        saysomething line[16]        // R,K,J,S,O in logical files only; R and K in physical files
        saysomething ":"
        saysomething line[17]    
        saysomething ":"
        saysomething line[18..27]    // field name
        saysomething ":"
        saysomething line[28]        // R if reffld used
        saysomething ":"
        saysomething line[29..33]    // field size
        saysomething ":"
        saysomething line[34]        // field type P,S,B,F,A,H,L,Z,T or J,E,O,G  for double byte char. sets
        saysomething ":"
        saysomething line[35..36]    // decimals
        saysomething ":"
        saysomething line[37]        // usage: I,O,Both
        saysomething ":"
        
        saysomething line[38..43]    // location - blank for PF and LF
        saysomething ":"
        saysomething line.substring(44)    
        // keywords: see http://publib.boulder.ibm.com/iseries/v5r2/ic2928/index.htm?info/dds/rbafpmstddsover.htm
        // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES, ALIAS,COMP,CMP,DATEFMT,DATESEP,EDTCDE
        
        say '''>'''
            
    } // end of show
    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        println "This is a test harness for the DDSSupport test plan"
        def dds = new DDSSupport(args[0],args[1])
	}

/*
The system determines the number of bytes actually occupied in storage as follows: 
Data Type - Bytes Occupied in Storage
Character - Number of characters
Hexadecimal - Number of bytes
Binary - 
1 through 4 digits - 2 bytes
5 through 9 digits - 4 bytes
10 through 18 digits - 8 bytes
Zoned decimal - Number of digits
Packed decimal - (Number of digits/2) + 1 (truncated if fractional)
Floating-point (single precision) - 4 bytes
Floating-point (double precision) - 8 bytes
Date - 10 characters without DATFMT keyword
and 6, 8 or 10 characters with DATFMT keyword
Time - 8 characters
Timestamp - 26 characters
*/

} // end of class
