package org.jnorthr.dds.drivers

import org.jnorthr.dds.fields.Field;

@groovy.transform.Canonical
class PhysicalFile
{
    def audit = true

    // partial print of audit
    def printx(tx) { if (audit) print tx; }

    // full println if audit flag on
    def say(tx) { if (audit) println(tx);  }

    // map of field names to Field objects; no duplicates allowed
    def metafields = [:]


    // ===================================================
    // default constructor
    public PhysicalFile()
    {
        super();
    } // end of constructor
    
    
    // see if file exists
    public chkobj(String fn)
    {
        return (new File(fn).exists()) ? true : false
    } // end of method
    
    // report on file access success
    public confirm(boolean f, String n)
    {
        def t = (f) ? "exists" : "is missing"
        say "   $n $t"
    } // end of method


    // return full map of all file level field attributes 
    public getFieldMeta()
    {
        return metafields;
    } // end of method


    // dump content of  meta map
    public dumpMeta()
    {
        say "\nContent of Meta Fields\n---------------------------"
        metafields.each{k, v ->
            say "$k = $v"
        } // end of each
    
    } // end of method


    // get a map entry, if found, then return map.value else return 'not declared'
    public getRef(String k)
    {
        return getRef(k,false); 
    } // end of method
    
    
    // get a meta map entry, if found, then return map.value else return 'not declared'
    public getRef(String k, boolean meta)
    {
        def ky = k.trim()
        if (this.metafields[ky]==null && meta)
        {
                throw new IllegalStateException("Field $ky cannot be found in map")
        } // end of if

        return this.metafields[ky]; 
    } // end of method

   

    // retain physical file attributes using IBM name like QFILE
    public add(String k, String n)
    {
        String nv = convert(k, n);

        // use an IBM field name when storing values
        metafields.put( k, nv )
    } // end of method

    // retain physical file attributes using IBM name like QFILE
    public convert(String k, String n)
    {
        def nv = (n.equals("Physical")) ? "*PF" :  n;
    } // end of method



    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
		def path = (args.size() > -1) ? args[0] : ""; 
        def pf = new PhysicalFile()

        def fn = path+"dspffd";
        def b = pf.chkobj(fn);
        pf.confirm(b,fn)

        def n = "QFMT"
        pf.add(n, "TEST1F")
        pf.add("QFILE", fn)

        pf.dumpMeta();
         
        def m = pf.getFieldMeta()
        println "\nContent of File Attributes\n---------------------------";
        
        m.each{k, v ->
            println "'$k' = '$v'"
        } // end of each
    
        println "\n=======================\ngetting QFMT :"+pf.getRef("QFMT");
        println "same with true:"+pf.getRef("QFMT",true)
        println "same with false:"+pf.getRef("QFMT",false)

        println "\n=======================\ngetting bad value QXXX :"+pf.getRef("QXXX");
        
        try{
            println "getting bad value QXXX and true:"+pf.getRef("QXXX",true);
        } catch(Exception s) {println "Exception:"+s.message;}
        println "======================\nEnd of Job ---"
    }   // end of psvm


} // end of class