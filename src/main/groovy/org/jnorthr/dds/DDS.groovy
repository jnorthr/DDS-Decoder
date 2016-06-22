package org.jnorthr.dds;

import org.jnorthr.dds.fields.Field;

// allow key declarations
@groovy.transform.Canonical
class DDS
{
    DDSSupport ddss;
    def audit = true
    def say(tx) { ddss.say(tx); }
    def printx(tx) { say.ddss.say(tx, false); }

    // the data table name from the PFILE declaration in this DDS
    def pfile="";
    def recordname=""

    // map of field names to Field objects; no duplicates allowed
    def mapfields = [:]
    
    // list of names of fields, comments, formats and record names but not keys; duplicates allowed
    def fields = []
    
    // list of name of keys only but nothing else
    boolean keyProcessingUnderway = false;
    def keys = []
    def keycount = 0;    // how many key fields declared ?
    
    // used to increment a fictional field name stored for each comment
    def commentcount = 0;

    // name of this file
    def DDSFileName="";


    // set when this object holds a field reference file
    boolean fieldreffile = false;      

    long startOfField = 0     // in bytes
    long lengthOfField = 0    // in bytes
    def f
    def tx

    def lines = 0;
    
   // copied over from Test.groovy    
   long s = 0
   def name  
   boolean flag = false
   def line=""
   def keep=""    // holds builtup text string



    // default constructor
    public DDS()
    {
        super();
        ddss = new DDSSupport();
    } // end of constructor
    

    // constructor taking DDS file name
    public DDS(String filename)
    {
        super(); 
        println "DDS constructor for "+filename
        ddss = new DDSSupport();
        say "DDS constructor for $filename"
        loadFields(filename)        
    } // end of constructor
    

    // constructor for field reference file with file name too - flag must be true if this is a FRF
    // note that FRF fields cannot use the REFFILE and REFFLD attribute
    public DDS(def filename, boolean flag)
    {
        this();
        if (flag)    fieldreffile=true;
        ddss = new DDSSupport();
        loadFields(filename)        
    } // end of constructor
    

    // constructor sets up field reference file - need loadFields() to complete
    public DDS(boolean flag)
    {
        this();
        if (flag)    fieldreffile=true;
        ddss = new DDSSupport();
    }    // end of constructor
    
    
    // dump content of map
    public dumpFields()
    {
        say "\nContent of Fields\n---------------------------"
        fields.each{ say (it);
        } // end of each
    
    } // end of method

    
    // dump content of map
    public dumpMap()
    {
        say "\nContent of Map of Fields\n---------------------------"
        mapfields.each{k, v ->
            say "\n\n$k = \n$v"
        } // end of each
    
    } // end of method


    // dump content of key list
    public dumpKeys()
    {
        say "\nContent of Keys\n---------------------------\nKey Name   Text-----------             Descending   Key No."
        keys.each{na ->
            // add .K suffix to field name if it is a key field so it will be unique and
            // not overlay the actual field entry; keyno,descend bits are in na+.K entry
            Field k = getRef(na)
            def i = k.keyno.toString()
            say "K ${k.name.padRight(8)} ${k.text.padRight(30)} ${k.descend.toString().padRight(8)}${i.padLeft(8)}"
        } // end of each
    
    } // end of method



   // dump content of data using map
    public dumpData(data)
    {
        say "\nPosition of Fields\nData size = ${data.size()}\n---------------------------\nStarts Length"
        mapfields.eachWithIndex{k, v, ix ->
            if (v.isField || v.isRecord || v.isComment)
            {
                int start = v.startingbyte;
                int len = v.bytesize
                def x = data.substring( start, start+len  )
                say "${ v.startingbyte.toString().padLeft(4) }   ${v.bytesize.toString().padLeft(4) }    ${v.name.padRight(8)} |${x}|"
            } // end of if
        } // end of each
    
    } // end of method
    
    // revise record header to have full bytesize length of all it's fields
    public updateRecordSize(def rec)
    {
        def rn = rec.trim()+"R"
        say "---> updateRecordSize($rn) to have ${startOfField} bytes"
        Field rf = getRef(rn);
        rf.bytesize = startOfField
        mapfields.put(rn, rf)
    } // end of method


    // get a map entry, if found, then return map.value else return 'not declared'
    public getRef(String k)
    {
        def ky = k.trim()
        if (this.mapfields[ky]==null)
        {
                throw new IllegalStateException("Field $ky cannot be found in $DDSFileName")
        } // end of if

        return this.mapfields[ky]; 
    }

    // return full list of all field names including record and comments too; actual objects are in mapfields;
    public getFields()
    {
        return fields;
    } // end of method


    // return full list of all field names used as a key
    public getKeys()
    {
        return keys;
    } // end of method
    
    
    // return full map of all fields including record and comments too
    public getFieldMap()
    {
        return mapfields;
    } // end of method
    
    
    // get Field object by field name
    public getFieldData(def fieldname, def data)
    {
        def ky = fieldname.trim()
        say "getFieldData for <"+ky+">"
        def v = this.getRef(ky);
        int start = v.startingbyte;
        int len = v.bytesize
        def x=""
        if (len > 0 )
        {
            x = data.substring( start, start+len )
            say "${ v.startingbyte.toString().padLeft(4) }   ${v.bytesize.toString().padLeft(4) }    ${v.name.padRight(8)} |${x}|";
        }; // end of if
        
        return x;
    } // end of method

    // --------- end of field reference
    

   
   // have all DDS text lines for this entry, so process this entry
   public process(txt)
   {
         say "processing <"+txt+">"
         def field = captureField(txt.padRight(80))
         if (field!=null) say field;
         say "==== end of process ===============\n"
   } // end of method

     
    // ================================
    // use line 
    // ================================
    public Field captureField(line) 
    { 
        ddss.show(line);
        
        // build a new Field object
        f = new Field()
        tx= line.substring(44)
        boolean keepFlag = false;
        

        // if this is a comment line, store it
        if (line[6]=='*')
        {
            f.isComment = true
            commentcount+=1;
            f.name="COMMENT${commentcount}"
            f.comment += line.substring(6)
            keepFlag = true
        } // end of if

        else

        // if this is a Record declaration, do it here
        if (line[16].equals("R") )
        {
            f.isRecord = true
            f.name = line[18..27].trim()
            recordname = f.name.trim()
            f.metadataUsage = line[16]    
            ddss.decodeText(f, tx, fieldreffile)   
            keepFlag = true
            say "---> this is a R declaration for recordname=$recordname from $line"
        }   // end of handling R type records


        else

        // if this is a key declaration, do it here;
        // we do not create a new field, just find and update an existing field 
        // with DESCEND, keyno and isKey flags
        if (line[16].equals("K") )
        {
            keyProcessingUnderway = true
            def xname = line[18..27].trim()
            def v = this.getRef(xname);

            v.isKey = true
            v.keyno = ++keycount;
            
            ddss.decodeText(v, tx, fieldreffile)
                    
            keys << xname       
        }   // end of handling K type records

        // not a 'K' key declaration, so try others
        else

        if (line[16].equals("S") )
        {
            keyProcessingUnderway = true

            def xname = line[18..27].trim()
            def v = this.getRef(xname);

            v.hasSelect = true
            
            ddss.decodeText(v, tx, fieldreffile)
        }   // end of handling S type records

        // not an 'S' key selection declaration, so try OMIT
        else

        if (line[16].equals("O") )
        {
            keyProcessingUnderway = true

            def xname = line[18..27].trim()
            def v = this.getRef(xname);

            v.hasOmit = true
            
            ddss.decodeText(v, tx, fieldreffile)
        }   // end of handling S type records

        else

        // find 'AND' / 'OR' continuation lines for SELECT and OMIT
        if (line[16].equals(" ") && keyProcessingUnderway)
        {
            def xname = line[18..27].trim()
            def v = this.getRef(xname);
            ddss.decodeText(v, tx, fieldreffile)
        } // end of else

        // handle the next field
        else
        {
            // ok, so set up new Field object - not an 'R' or 'K' or 'S' or 'O'
            f.isField = true;
            keepFlag = true;

            f.hasReference = ( line[28] != 'R' ) ? false : true; 
                        
            ddss.decodeText(f, tx, fieldreffile)    
            long s = f.size

            
            f.name = line[18..27].trim()
            f.metadataUsage = line[16]
            
            // keep field type
            if ( line[34] != ' ' ) 
            { 
                f.type = line[34] 
            }   // end of if
            
            // compute field size
            def sz = line[29..33]
            if (sz.trim().size()>0)
            {
                s = sz.trim().toInteger();
                f.size = s
            } // end of if
            
            // compute record byte locations & lengths
            lengthOfField = (f.type!="P") ? s : ( ( s / 2 ) + ( s % 2 ) )
            
            f.startingbyte = startOfField
            f.bytesize = lengthOfField
            startOfField += lengthOfField
    
    
            // compute number of decimal places if field is numeric
            def d = line[35..37]
            if ( d.trim().size() > 0 )
            {
                f.decimals = d.toInteger();
                if ( f.type.equals( "A" )  )  { f.type = 'S' }
            }    // end of if
            
        }    // end of else


        // place name of field into a list if flag syas so
        if (keepFlag)
        {        
            // keep field in list
            // construct key as name+
            def mk = f.name.trim()+f.metadataUsage
            if (mk.trim().size()>0)
            {
                fields << mk.trim();
                mapfields.put(mk.trim(), f)
                return f
            }; // end of if

            else
            {
                say "... could not find a name in $txt"
            }; // end of else
            
        } // end of if


        return null;
                
    } // end of method
    
    
    // return a count of only the isField entries, ignoring comment and Record entries
    public fieldCount()
    {
        fields.each{f-> 
            say "... fields.each = $f"
        } // end of each

        int i = 0;
        fields.each{f-> 
        
            def g = this.getRef(f);
            if (g.isField) { i+=1; } 
        } // end of each
        
        return i
    } // end of method

   // ============================
   // load fields from DDS file
   public void loadFields(fn)
    { 
        if (fieldreffile)
        {
            say "============================\nStart of Field Ref File logic :"
        } // end of if
        
        // construct DDS with true boolean to identify this load as a Field reference dictionary 
        //def dds = new DDS(true)
        //def fn = "./REFFILE"
        if (new File(fn).exists())  
        {
            say "$fn exists"
        } // end of if
        else
        {
            def fn2 = fn+".txt"
            if (new File(fn2).exists())  
            {
                say "$fn2 exists"
                fn = fn2
            } // end of if
            else
            {
                throw new FileNotFoundException(fn+" cannot be found or used");
            } // end of else
        }    // end of else
    
    
        DDSFileName = fn;
        
        // as400 DDS decoder
        new File(fn).eachLine{
        it->
            // pad out the line to void short string causing invalid subscripts
            def line=it.padRight(80)
        
            if ( line.trim().size() < 6 || line[5] != 'A' ) 
            {
                //say ln
            }
            else
            {
                lines+=1;
                if (line[6]=='*')
                {
                    // do nothing for comments
                }
                else
                {
                    // get next name
                    name = line[18..27].trim()

                    // no name, so just build ups series of DDS source lines
                    if (name.size()<1)
                    {
                        keep += " "+line.substring(44).trim();
                        say "... keeping=>"+keep
                    }

                    else

                    {
                        // time to process the sum of all source DDS lines for this field
                        if (flag)
                        {
                            say "... processing is called with keep=>"+keep
                            process(keep);
                            keep="";
                            flag = false;
                        } // end of if

                        else

                        // also may ck if keywords like REF and PFILE preceed any record/field declarations
                        // since flag is not true, no prior field found, so this must be at start of DDS source
                        if (keep.size() > 0)
                        {
                            say "keep has some data but no field :"+keep
                            processDeclarations(keep);
                        } // end of id

                        // ok, so we start collecting a new set of DDS sources lines
                        flag = true
                        keep = "     "+line.trim();
                        say "keep:$keep"
                    } // end of else

                } // end of else
                
            }    // end of else
        }    // end of each
        
        
        // process previously collected data
        if (flag)
        {
            process(keep);
        } // end of if

        // when DDS is for a normal file, then update the 'R' record declaration field length
        if (!fieldreffile)
        {
            updateRecordSize(recordname);
        } // end of if


        say "loadFields($fn) complete --------------------------------------\n==========================\n"

        if (fieldreffile)
        {
            say "============================\nEnd of Field Ref File logic \n\n\n"
        } // end of if

    }    // end of method
    


   // ===========================================================================
   // load declarations from top of DDS file - these are not field-related; like REF and PFILE
   public processDeclarations(String keep)
   { 
        String ready = keep.trim().toUpperCase()

        // break tokens into ) delimited words
        def tokens = ready.split(/\)/).toList()
        tokens.each
        { wd ->
            def wd2 = wd.trim()
            int r = wd2.indexOf("(")
            def pre = wd2.substring(0,r).trim()
            def main = wd2.substring(r+1).trim()    

            if (pre.equals("REF") ) 
            {                 
                if (fieldreffile)
                {
                    say "... found REF $main - but cannot use within a field reference file"                
                }
                else
                {
                    ddss.keepRefFile( main ) 
                }; // end of else
            } // end of if


            if (pre.equals("PFILE") )
            {
                pfile = main;   
                say "... PFILE set to use $main"
            } // END OF IF

        } // end of each token

    }    // end of method



    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        def data = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
		def dir = args[0]
		def path = args[1]
		
        def dds = new DDS(path+"TEST23.PF")
	println "step 2"
        dds.dumpFields()
        dds.dumpMap()
        dds.dumpKeys()
        dds.dumpData(data)
//System.exit(0);

        dds = new DDS(true)
        dds.loadFields(dir+"REFFILE.txt")
        
        println "There were "+dds.lines+" lines"
        println "There were "+dds.fieldCount()+" fields"

        dds.dumpMap()

        def fi = dds.mapfields["FRED3"]
        println "dds.mapfields[\"FRED3\"] key name is fi.name="+fi.name

        fi = dds.getRef("CODE")
        println "dds.getRef(\"CODE\") key name is fi.name="+fi.name
        
        
        dds.dumpMap()
        println "==========================================================\nData Layout\n"
        dds.dumpData(data)        
        println "==========================================================\nEnd of Field Reference File Test ---\n\n"

        println "==========================================================\nStart of Field Test of /dds2.txt ---"
        dds = new DDS()
        dds.loadFields(dir+"dds2")
        
        println "There were "+dds.lines+" lines"
        println "There were "+dds.fieldCount()+" fields"

        fi = dds.mapfields["FRED3"]
        println "dds.mapfields[\"FRED3\"] key name is fi.name="+fi.name

        fi = dds.getRef("CUST")
        println "dds.getRef(\"CUST\") key name is fi.name="+fi.name
        
        dds.dumpKeys()
        
        dds.dumpMap()
        
        println "============================\nData Layout\n"
        dds.dumpData(data);        
        println "==========================================================\nEnd of Field Test ---\n\n"

        // check just creating a DDS list from a DDS filename to constructor w/o need to call loadFields
        println "==========================================================\nStart of Filename Test ---"
        dds = new DDS(dir+"dds")
        
        println "There were "+dds.lines+" lines"
        println "There were "+dds.fieldCount()+" fields"

        fi = dds.mapfields["FRED3"]
        println "dds.mapfields[\"FRED3\"] key name is fi.name="+fi.name

        fi = dds.getRef("CUST")
        println "dds.getRef(\"CUST\") key name is fi.name="+fi.name
        
        
        dds.dumpMap()
        println "======================\nData Layout\n"
        dds.dumpData(data);
 
        def na = "FRED3"
        def x = dds.getFieldData(na,data)
        print "... getting data for $na = |"
        println "$x|"


        // try / catch around call to get a non-existent field name
        na = "FRED44"
        try { x = dds.getFieldData(na, data) }
        catch(Exception ex) { println "--> could not find $na field"; }


        println "\n--------------------------------------------------\nRetrieve full List of Field's"
        dds.getFields().eachWithIndex{f, ix -> 
            print "#${ix+1} has <$f>"+" "; 
            Field k = dds.getRef(f)
            println k;
        } // end of each
        

        println "\n--------------------------------------------------\nRetrieve full List of Key's"
        
        dds.dumpKeys()

        println "=========================================================\nEnd of Filename Test ---\n\n"
        

        println "======================\nEnd of Job ---"

    }    // end of psvm

}    // end of class