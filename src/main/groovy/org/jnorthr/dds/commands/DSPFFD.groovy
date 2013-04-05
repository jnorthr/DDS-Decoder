package org.jnorthr.dds.commands;
import org.jnorthr.dds.fields.Field;
import org.jnorthr.dds.drivers.PhysicalFile;

@groovy.transform.Canonical
class DSPFFD
{
    def audit = true

    // partial print of audit
    def printx(tx) { if (audit) print tx; }

    // full println if audit flag on
    def say(tx) { if (audit) println(tx);  }

    // list of Field(s)
    def fields = []

    // PhysicalFile holding file level metadata
    PhysicalFile pf = null;

    // name of this spoolfile
    def DSPFFDFileName="";

    def lines = 0;
    // flag set when reading source lines with field declarations
    boolean fieldsfound = false

    // flag to build up a full set of source lines for a single field
    boolean inprogress = false;

    // holds builtup text string
    String keep=""    


    // map of description to gainan IBM file name equiv.
    def metanames = ["File":"QFILE", "Library":"QLIB", "File location":"QLOC", "Externally described":"QEXT", "Number of record formats":"QRCDFMTS", "Type of file":"QTYPE", "File creation date":"QCRTDAT", 
    "Text 'description'":"QTEXT", "Record format":"QFMT", "Format level identifier":"QLVL", "Number of fields":"QNBRFLDS", "Record length":"QRCDLEN", "Date Format":"QDATFMT", "Coded Character Set Identifier":"QCCSID", "Time format":"QTIMFMT", "Time Format":"QTIMFMT" ]

    def metafieldnames = ["Field text":"QTEXT","Date Format":"QDATFMT", "Coded Character Set Identifier":"QCCSID", "Time Format":"QTIMFMT"]
    def fieldmap = [:]

    // ===================================================
    // default constructor
    public DSPFFD()
    {
        super();
        pf = new PhysicalFile();
    } // end of constructor
    

    // constructor taking DDS file name
    public DSPFFD(String filename)
    {
        super(); 
        say "DSPFFD constructor for $filename"
        pf = new PhysicalFile();
        loadFields(filename)        
    } // end of constructor
    

    // dump content of meta map holding IBM names
    public dumpMetaNames()
    {
        say "\nContent of Meta Names\n---------------------------"
        metanames.each{k, v ->
            say "'$k' = '$v'"
        } // end of each

    } // end of method


    // dump content of map holding IBM names and data values
    public dumpFieldMap()
    {
        say "\nContent of Field Map\n---------------------------"
        fieldmap.each{k, v ->
            say "'$k' = '$v'"
        } // end of each

    } // end of method


    // retain physical file attributes using descriptions like 'Format level identifier'
    public store(String k, String n)
    {
        // convert text descr. into an IBM field name when storing values
        String mn = metanames[k];
        if (mn==null)
        {
            say "... cannot find file level metadata name of $k"
        }
        else
        {
            pf.add( mn, n )
        } // end of else

    } // end of method



    // capture File metadata
    public boolean processFileMetadata(def ln)
    {
        // look for a full-stop
        def j = ln.indexOf(".");
        
        // if no full-stop or we're looking at source lines we know are not what we want, return as unused
        if (j < 0 || fieldsfound)
        {
            return false
        }
        
        else
        {
            // pick out file related metadata (not field related) and keep in a map in object PhysicalFile
            def tok = ln.substring(0,j).trim().toString()

            j = ln.indexOf(":");
            def tok2 = ln.substring(j+1).trim().toString()

            // convert text descr. into an IBM field name when storing values
            this.store( tok, tok2 )

            // line has been used
            return true
        } // end of else
        
    } // end of method
    

    // get a map entry, if found, then return map.value else return 'not declared'
    public getRef(String k)
    {
        String ans = getRef(k, false); 
        return  (ans==null) ? ""  : ans;
    }    // end of getref

    // get a map entry, if found, then return map.value else return 'null' or fail
    public getRef(String k, boolean fail)
    {
        def ky = k.trim()
        if (this.fieldmap[ky]==null && fail)
        {
                throw new IllegalStateException("Field $k cannot be found in $DDSFileName")
        } // end of if

        return this.fieldmap[ky]; 
    }    // end of getref

    
    // source lines for each field come here
    public processField(String ln)
    {
        //say "   ------"
        fieldmap = [:]
        boolean hdg2 = true
        boolean hdg3 = true

        // break tokens into ) delimited words
        def tokens = ln.tokenize('|')
        tokens.each
        { wd ->

            // look for signal sequence in token
            int k = wd.indexOf(". .")
            //printx "   k=$k token:$wd"

            // if no seq. then this is probably a continuation line
            if (k<0) 
            {
                // is there something in first 64 bytes of source line ?
                def t=wd[0..64];
                boolean sn = (t.trim().size()>0) ? true : false 
                def t2 = (sn) ? "(something)" : "(nothing)" 
                //printx t2

                // nothing in first 64 bytes, just use the comment field cols.:66..80
                if (!sn)
                {
                    if (hdg2)
                    {
                        hdg2 = false;
                        def colhdg2 = wd[65..79]
                        fieldmap.put( "QCOLHDG2", colhdg2 );
                    }
                    else
                    if (hdg3)
                    {
                        hdg3 = false;
                        def colhdg3 = wd[65..79]
                        fieldmap.put( "QCOLHDG3", colhdg3 );
                    }

                } // end of if
            }

            // treat this source line as file attributes, not field
            else
            {
                int l = wd.indexOf(":")
                //printx "( : is at $l )"

                // if there's a semicolon, then get text in col 66..80 into t4
                def t4=""
                if (l>0) 
                { 
                    t4=wd.substring(l+1).trim(); 
                    //printx "("+t4+")";
                } // end of if

                // t3 holds key like: Date Format . . . . . . . . . . . . . . . :  *ISO
                def t3 = wd[0..k-1].trim().toString(); 
                //printx "($t3)"

                // here convert Date Format to QDATFMT
                String mn = metafieldnames[t3];
                if (mn==null)
                {
                    printx "... cannot find field level meta field name of $k"
                }
                else
                {
                    //printx "=$mn";   
                    // place value into field map
                    fieldmap.put( mn, t4.toString() )
                } // end of else

            } // end of else

            //say ";"
        } // end of each
        //say "   ------"


       if (ln.indexOf("§") < 0 )
       {
           printx "   using <$ln> "

           def n = ln[2..11]
           say "; name=<$n>"       
           def ty = ln[13..24]
           def digits = ln[25..26]
           def len = ln[27..29]
           def bytes = ln[30..37]
           def start = ln[39..47]
           def usage = ln[56..62]
           def colhdg1 = ln[65..79]
           if (colhdg1.size()>0) {  fieldmap.put( "QCOLHDG1", colhdg1 ); }          
           
           // build a new Field object
           def f = new Field()
           f.isField = true;
           f.name = n
           f.type = ty
           if (len.trim().size() > 0) { f.size = len.toInteger() } 
           if (start.trim().size() > 0) { f.startingbyte = start.toInteger() } 
           if (bytes.trim().size() > 0) { f.bytesize = bytes.toInteger() }
           if (digits.trim().size() > 0) { f.decimals = digits.toInteger() };  
           f.usage = usage[0]

           f.colhdg1 = getRef("QCOLHDG1")
           f.colhdg2 = getRef("QCOLHDG2")
           f.colhdg3 = getRef("QCOLHDG3")
           f.text   = getRef("QTEXT")


           fields << f;
           say f;

           if ( fieldmap.size() > 0 ) dumpFieldMap();
           
       } // end of if
       
    } // end of processField



    // each source line comes here
    public processLine(String ln)
    {
        // Source lines already placed into the metamap then decode() returns false
        boolean wasprocessed = processFileMetadata(ln)

        if (!wasprocessed)
        {
            // file already positioned at lines we want to use / treat as fields
            if (fieldsfound)
            {
                def n = ln[2..3]
                // handle continuation lines
                if (n.trim().size() < 1)
                {
                    if (inprogress) 
                    { 
                        //printx "inprogress: "
                        keep += "|"
                        keep += ln 
                        //say ": now =$keep"
                    }
                    else
                    {
                        say "===> ignoring ln=$ln"
                    } // end of else
                }

                else
                {
                    // if previous field was in progress, then process it : var. keep has the full monty
                    if (inprogress) 
                    {
                        processField(keep);
                        inprogress = false;
                        keep = "";   
                    };

                    inprogress = true;
                    keep = ln;
                } // end of if
            } // end of if

            else
            {                
                say "... did not use line ${lines.toString().padLeft(3)} =>"+ln
                def pre = ln.substring(0,10).trim()
                fieldsfound = (pre.equals("Field")) ? true : false
            } // end of else if fieldsfound
            
        } // end of else
       
    } // end of processLine



    // ============================================================
    // load fields from DSPFFD spoolfile
    // ============================================================
    public void loadFields(fn)
    { 
        def filename = fn
        boolean f = (pf.chkobj(filename))
        pf.confirm(f, filename);
         
        // if provided filename does not exist, try the .txt version 
        if (!f)  
        {
            filename = fn+".txt"
            f = (pf.chkobj(filename))
            pf.confirm(f, filename);

            if (!f)  
            {
                throw new FileNotFoundException(fn+" cannot be found or used");
            } // end of if

        } // end of if
    
    
        DSPFFDFileName = filename;
        
        // as400 DDS decoder
        new File(fn).eachLine
        { 
            lines+=1;

            // pad out the line to void short string causing invalid subscripts
            def line = it.trim();
            say "read line $lines : $it"
            boolean eof = (line.size() > 5 && line.substring(0,6).equals("******") ) ? true : false;


            // since last source line was not found (******) continue
            if (!eof)
            {
                processLine(it.padRight(80));
            } // end of if

        }    // end of each


        // finish processing last field
        if (inprogress) 
        {
            processField(keep);
            inprogress = false;
            keep = "";   
        };

        say "\nloadFields($DSPFFDFileName) complete\n --------------------------------------\n==========================\n"
    }    // end of method
    


    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"

        def dds = new DSPFFD("./qddssrc/dspffd.txt")
        dds.pf.dumpMeta()
        dds.dumpMetaNames()
        
        println "======================\nEnd of Job ---"

        //System.exit;        
    }   // end of psvm

}    // end of class