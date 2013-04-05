package org.jnorthr.dds;

import org.jnorthr.dds.fields.Field;
import org.jnorthr.dds.DDS;
import org.jnorthr.dds.fields.SelectedField;

@groovy.transform.Canonical
class ProcessDDS
{
    def audit = true
    def say(tx) { if (audit) println tx; }
    
    def printx(tx) { if (audit) print tx; }
    def dds
    def mapfields = [:]
    def names =[]
    def selectedfields = []
    
    def data = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";


    // constructor
    public ProcessDDS()
    {
        super();
        say "ProcessDDS default constructor"
    } // end of default constructor
    
    
    // constructor
    public ProcessDDS(ddsn)
    {
        super();
        say "ProcessDDS constructor for $ddsn"
        setupDDS(ddsn)
    } // end of constructor
    

    // open and load DDS for a file, then gain map of fields
    public setupDDS(ddsn)
    {
        say "setupDDS for $ddsn"
        dds = new DDS(ddsn)
        mapfields = dds.getFieldMap()
    } // end of setupDDS
    

    // loop to read dataset
    public readData(dfn)
    {
        new File(dfn).eachLine
        { ln ->
            say "readData :"+ln
            data = ln;
            def sbu = "";

            selectedfields.each{v -> 
                SelectedField f = v;
                print "---> f.name= ${f.name} : "
                println getData(f.name)
                sbu+=getData(f.name);
            } // end of each
        
            say "Out:"+sbu

        } // end of eachLine
    } // end of readData


    // get Field object by field name
    public getData(def fieldname)
    {
        def ky = fieldname.trim()
        //say "getData for <"+ky+">"
        def v = dds.getRef(ky);
        int start = v.startingbyte;
        int len = v.bytesize
        def x=""
        if (len > 0 )
        {
            x = data.substring( start, start+len )
            //say "${ v.startingbyte.toString().padLeft(4) }   ${v.bytesize.toString().padLeft(4) }    ${v.name.padRight(8)} |${x}|";
        }; // end of if
        
        return x;
    } // end of method


    // this logic takes the names of all the columns for this table and converts then into a list of <SelectedField> objects
    public getNames(String namelist)
    {
        names = namelist.trim().toUpperCase().split(",").toList();
        say "getNames($namelist)"
        def n;
        def fd;
        selectedfields = [];
        
        names.each
        {
            n = it.trim()
            say "---> $n"; 
            try
            {
                fd = dds.getRef(n);
            }
            catch(Exception x)
            {
                say "CPF9801 - Field named $n not found in ${dds.DDSFileName} "
                System.exit(1);
            }
            SelectedField sf = new SelectedField();
            
            sf.startingbyte = fd.startingbyte;
            sf.bytesize = fd.bytesize;
            sf.name = fd.name;
            sf.size = fd.size;
            sf.decimals = fd.decimals;
            sf.type = fd.type;
            sf.usage = fd.usage;
            sf.hasValues = fd.hasValues
            sf.values = fd.values;
            sf.hasSelect = fd.hasSelect;
            sf.select = fd.select;
            sf.hasOmit = fd.hasOmit;
            sf.omit = fd.omit;
            selectedfields << sf;
        } // end of each
        
        return selectedfields; 
    } // unstring names from select



    // ---------------------------------
    // decode the SQL Select stmt; if table name found, load that name into the DDS pointer here;
    // then return just the name of the fields to be found in the declared table
    public sql(String stmt)
    {
        def s = stmt.trim().toUpperCase();
        int ix = s.indexOf(" ")
        
        if (ix < 1 || s.size() < 3)     
        {
                throw new IllegalStateException("SQL Command cannot be found in this context '$stmt' ")
        } // end of if

        def s2;        
        def sel = s.substring(0,ix)
        
        switch(sel)
        {
            case "SELECT" : s2 = s.substring(ix+1);
                            break;
        
            default: throw new IllegalStateException("SQL Command $sel cannot be used in this context '$stmt' ");     
                     System.exit(0);   
        } // end of case
        
        // find FROM in sql syntx
        def ix2 = s2.indexOf("FROM")
        if (ix2 < 1)
        {
                throw new IllegalStateException("SQL Command '$stmt' does not identify table to be used")
        } // end of if

        // extract names of columns to use from this table
        s = s2.substring(0,ix2)
        
        // extract table name
        s2 = s2.substring(ix2+5)
        if (s2.size() < 1)
        {
                throw new IllegalStateException("SQL Command '$stmt' does not identify table to be used")
        } // end of if
        
        // load new DDS for this table name         
        setupDDS(s2)

        return s;
    } // end of sql
    
    


    // =======================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"

        println "==> new Date()="+new Date();
        Date birthDate = Date.parse('yyyy-MM-dd', '1973-9-7')

        def pdds = new ProcessDDS("./qddssrc/dds2.txt");

        println "There were "+pdds.dds.fieldCount()+" fields"

        pdds.mapfields.eachWithIndex{k,v, ix -> 
            Field f = v;
            println "---${(ix+1).toString().padLeft(2)} ${v.name}"
        } // end of each
        
        println "\n==============================="
        println "\nget Field object using field name"
        def fd = pdds.dds.getRef("FRED3");
        println "pdds.dds.getRef(\"FRED3\") fd.name="+fd.name
        
        def xxx = pdds.getData("FRED3")
        println "data for FRED3 is |"+xxx+"|"

        println "\n===============================\nDo SQL Select Choice"

        def fields = pdds.sql("Select A44PEXTCDE, EFFINITCHG, TRAILRATE, A11TSTAMP from ./test23.pf")
        fields = pdds.getNames(fields)

        fields.each{v -> 
            SelectedField f = v;
            print "---> f.name= ${f.name} : "
            println pdds.getData(f.name)
        } // end of each
        
        if (pdds.dds.pfile.size() > 0)
        {
            println "pfile to use :"+pdds.dds.pfile;
            pdds.readData(pdds.dds.pfile);
        } // end of if
        
        // so here, we copy back both the list and the map from the DDS object
        println "======================\nEnd of Job ---"
    }    // end of psvm

}    // end of class