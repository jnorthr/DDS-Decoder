import Field;

@groovy.transform.Canonical
class ProcessDDS
{

    def fields = []
    def fieldmap = [:]

    // related to FRF container    
    boolean hasValidFRF = false;
    def RefFile="";
    def FRF

    long startOfField = 1     // in bytes
    long lengthOfField = 0    // in bytes
    def continueFlag = false
    def f
    def tx
    
    
    // take off surrounding quote marks then return a trimmed version
    public removeQuotes(def r)
    {
        def r2 = r.trim()
        if (r2.startsWith("'"))
        {
            int w = r2.substring(1).indexOf("'")
            def r3 = r2.substring(1,w+1)
            return r3.trim()
        }    // end of if
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
        }    // end of else
        
    }    // end of method


    // take off surrounding quote marks then return a trimmed version
    public keepRefFile(def f, def r)
    {
        def r2 = r.trim() + ".txt"
        def rf = new File(r2)
        if (rf.exists())
        {
            RefFile = r2;
            println "-> Field Reference File will be "+r2
            hasValidFRF = true;  
            FRF = new DDS(true)
            FRF.loadFields(RefFile)
            def fi = FRF.getRef("CODE")
            println "key name is "+fi.name          
        } // end of if
        
    }    // end of method


    // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES 
    public decodeText(Field f, def text)
    {
        def main
        def t = text.trim()
        if ( t.size() < 1 )    return
        
        int s = text.indexOf("(")
        if ( s < 1 )  return
        
        def pre = text.substring(0,s)   
        
        if (pre.equals("TEXT"))
        {
            int r = text.indexOf(")")
            main = text.substring(s+1,r)    
    
            f.text = removeQuotes(main)
        }    // end of if
        
        if (pre.equals("COLHDG"))
        {
            int r = text.indexOf(")")
            main = text.substring(s+1,r)    
            decodeHeadings(f, main.trim() );
        }    // end of if
    
        if (pre.equals("REFFILE"))
        {   
            int r = text.indexOf(")")
            main = text.substring(s+1,r)    
            keepRefFile(f, main.trim() );
        }    // end of if
    
        if (pre.equals("REFFLD") && f.isField )
        {   
            int r = text.indexOf(")")
            main = text.substring(s+1,r)  
            if (hasValidFRF)
            {  
                Field fi = FRF.getRef(main.trim())
                def m
                if ((fi!=null))
                {
                    m = "key name is "+fi.name 
                    f.metadataUsage = fi.metadataUsage
                    f.size = fi.size
                    f.decimals = fi.decimals
                    f.type = fi.type
                    f.usage = fi.usage
                    f.text = fi.text
                    f.colhdg1 = fi.colhdg1
                    f.colhdg2 = fi.colhdg2
                    f.colhdg3 = fi.colhdg3   
                    f.edtfmt = fi.edtfmt
                    f.comment = fi.comment
                }
                else
                {
                    m = "${main.trim()} not found in ${RefFile}"
                } // end of if

                println m

            } // end of if
 
        }    // end of if
    
    }    // end of method


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

            //println "there are $c apostrophes and $m spares; there are ${c/2} words"
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
    


    // ================================
    // use line 
    // ================================
    public Field captureField(line) 
    { 
        print "<"
        print line[0..4]
        print ":"
        print line[5]
        print ":"
        print line[6]
        print ":"
        print line[7..15]
        print ":"
        print line[16]        // R,K,J,S,O ??
        print ":"
        print line[17]    
        print ":"
        print line[18..23]    // field name
        print ":"
        print line[24..29]    // spare
        print ":"
        print line[30..33]    // field size
        print ":"
        print line[34]        // field type P,S,B,F,A,H,L,Z,T or J,E,O,G  for double byte char. sets
        print ":"
        print line[35..37]    // decimals
        print ":"
        print line[38]        // usage: I,O,Both
        print ":"
        
        print line[39..43]    // location - blank for PF and LF
        print ":"
        print line[44..79]    // keywords: see http://publib.boulder.ibm.com/iseries/v5r2/ic2928/index.htm?info/dds/rbafpmstddsover.htm
        // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES
        print ":"
        
        println ">"
        
        // if no field continuation is in progress, then build a new Field object
        if (!continueFlag)
        {
            f = new Field()
            tx = line[44..79];
        }   // end of if
        
        // if this is a comment line, store it
        if (line[6]=='*')
        {
            f.isComment = true
            f.comment = line.substring(6)
            continueFlag = false
            println f;
            fields << f;
        }

        else

        // if this is a Record declaration, do it here
        if (line[16].equals("R") )
        {
            f.isRecord = true
            f.name = line[18..23]
            f.metadataUsage = line[16]    
            decodeText(f, tx)   
            continueFlag = true;
            println f;
            fields << f;
        }

        else

        // if field continuation is in progress, then do not build a new Field object, just use the extra text
        if (continueFlag & line[18..23].trim().size() < 1 )
        { 
            tx = line[44..79]  
            decodeText(f, tx)            
        }    // end of if

        // handle the next field
        else
        {
            // if prior field is already in progress, then print/use it !
            // we have already finished with this field
            if (continueFlag)
            {
                println f
                continueFlag = false
                fields << f;
            }    // end of if
            
            
            // ok, so set up new Field object
            f = new Field()
            f.isField = true;
            tx = line[44..79]
            continueFlag = true
            
            decodeText(f, tx)    
            long s = f.size

            
            f.name = line[18..23]
            f.metadataUsage = line[16]
            
            // keep field type
            if ( line[34] != ' ' ) 
            { 
                f.type = line[34] 
            }   // end of if
            
            // compute field size
            def sz = line[30..33]
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
            
        }
        return f
    }    // end of method
    
    
    public fieldCount()
    {
        fields.size()
    }


    public void loadFields(fn)
    { 
        // as400 DDS decoder
        new File(fn).eachLine{
        ln->
            // pad out the line to void short string causing invalid subscripts
            ln=ln+="                                                                  "    // add trailing blanks
            //println "\n\n"+ln
        
            if ( ln.trim().size() < 1 || ln[5] != 'A' ) 
            {
                println ln
            }
            else
            {
                captureField(ln)
            }    // end of else
        }    // end of each
        
        
        // was a Field objeect already in progress, so use it
        // if prior field is already in progress, then print/use it !
        // we have already finished with this field
        if (continueFlag) 
        {
            println f
            fields << f;
        } // end of if
        
    }    // end of method
    


    public static void main(String[] args)
    {
        def dds = new ProcessDDS()
        dds.loadFields("./dds2.txt")
        println "There were "+dds.fieldCount()+" lines"
    }    // end of psvm

}    // end of class