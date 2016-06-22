import Field;

@groovy.transform.Canonical
class Test
{
    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        println "This is a test harness for the DDS consumer test plan"
        println "cd /Volumes/Data/dev/groovy/DDS to review various components of this suite"
        def dds = new DDS(true)
        dds.loadFields("./qddssrc/REFFILE")
        
        println "There were "+dds.lines+" lines"
        println "There were "+dds.fieldCount()+" fields"

        def fi = dds.mapfields["FRED3"]
        println "dds.mapfields[\"FRED3\"] key name is fi.name="+fi.name

        fi = dds.getRef("CODE")
        println "dds.getRef(\"CODE\") key name is fi.name="+fi.name
        
        
        dds.dumpMap()
        println "======================\nData Layout\n"
        dds.dataMap("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz")        
        println "======================\nEnd of Field Reference File Test ---\n\n"

        println "======================\nStart of Field Test ---"
        dds = new DDS()
        dds.loadFields("./qddssrc/dds2")
        
        println "There were "+dds.lines+" lines"
        println "There were "+dds.fieldCount()+" fields"

        fi = dds.mapfields["FRED3"]
        println "dds.mapfields[\"FRED3\"] key name is fi.name="+fi.name

        fi = dds.getRef("CUST")
        println "dds.getRef(\"CUST\") key name is fi.name="+fi.name
        
        
        dds.dumpMap()
        println "======================\nData Layout\n"
        dds.dataMap("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz")        

        println "======================\nEnd of Job ---"

    }    // end of psvm        
}    // end of class