package org.jnorthr.dds.fields

@groovy.transform.Canonical
class Key {

    char   usage    = ' '    // R,K,J,S,O blank for AND/OR choices ??
    String name
    String alias    = ""
    String text    = ""


    // key related values
    boolean isKey       = false
    boolean hasSelect   = false
    boolean hasOmit     = false
    boolean andand     = false  // when another OMIT follows the first OMIT
    boolean andor       = false // when another SELECT follows the first SELECT

    boolean descend = false

    int keyno = 0
    String select = ""
    String omit = ""


    String toString()
    {
        def ik = (isKey) ? "yes" : "no "
        def hs = (hasSelect) ? "yes" : "no "
        def ho = (hasOmit) ? "yes" : "no "
        def desc = (descend) ? "yes" : "no "
        def aa = (andand) ? "yes" : "no "
        def ao = (andor) ? "yes" : "no "

        def reply = "usage='$usage' name=${name?.padRight(10)} text='${text?.padRight(30)}' alias='${alias?.padRight(30)}'"

        if (isKey) 
        {
            reply+= " isKey=$ik desc=$desc keyno=$keyno "

        }

        if (hasSelect) 
        {
            def tx = "\nhasSelect=$hs or=$ao select='${select?.padRight(30)}'"  
            reply+=tx
        }

        if (hasOmit) 
        {
            def tx = "\nhasOmit = $ho and= $aa omit='${omit?.padRight(30)}'"             
            reply+=tx
        }

        return reply
    } // end of toString()



    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        def keys = []
        Key s1 = new Key([isKey:true, keyno: 1, usage:'K', name: 'ST', text:'This is a state', alias:'State Abbrev.']) 
        keys << s1

        s1 = new Key([usage:'S', name: 'ST', text:'State of New York', alias:'New York Key',
            hasSelect:true, select:'COMP(EQ ’NY’)']) 
        keys << s1

        s1 = new Key([usage:'O', name: 'ST', text:'Omit this state key', alias:'Ignore State Abbrev. Key',
            hasOmit:true, omit:'COMP(GE 78)']) 
        keys << s1

        s1 = new Key([usage:'O', name: 'ST',  hasOmit:true, andand:true, omit:'COMP(NE 96)']) 
        keys << s1

        keys.each{
            println " "
            println it
        }
        println "======================\nEnd of Job ---"

    }    // end of psvm            
}    // end of Field