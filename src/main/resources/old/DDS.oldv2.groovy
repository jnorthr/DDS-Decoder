import Field;

@groovy.transform.Canonical
class DDS
{

    def audit = false
    def fields = []
    def mapfields = [:]                // keyed map of field names but no record name/comments
    boolean fieldreffile = false;      // set when this object holds a field reference file
    def reffile="";                    // name of field ref file
        
    def fieldCount = 0;
    boolean hasValidFRF = false;
    def RefFile="";

    long startOfField = 1     // in bytes
    long lengthOfField = 0    // in bytes
    def continueFlag = false
    def f
    def tx
    
    public say(tx)  { if (audit) println tx; }
    public printx(tx)  { if (audit) print tx; }

    public DDS(boolean flag)
    {
        super();
        if (flag)    fieldreffile=true;
    }    // end of constructor
    

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
            say "-> Field Reference File will be "+r2
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
        printx "<"
        printx line[0..4]
        printx ":"
        printx line[5]
        printx ":"
        printx line[6]
        printx ":"
        printx line[7..15]
        printx ":"
        printx line[16]        // R,K,J,S,O ??
        printx ":"
        printx line[17]    
        printx ":"
        printx line[18..25]    // field name
        printx ":"
        printx line[26..29]    // spare
        printx ":"
        printx line[30..33]    // field size
        printx ":"
        printx line[34]        // field type P,S,B,F,A,H,L,Z,T or J,E,O,G  for double byte char. sets
        printx ":"
        printx line[35..37]    // decimals
        printx ":"
        printx line[38]        // usage: I,O,Both
        printx ":"
        
        printx line[39..43]    // location - blank for PF and LF
        printx ":"
        printx line[44..79]    // keywords: see http://publib.boulder.ibm.com/iseries/v5r2/ic2928/index.htm?info/dds/rbafpmstddsover.htm
        // COLHDG,TEXT,CHECK,DESCEND,EDTFMT,REF,REFLD,VALUES
        printx ":"
        
        say ">"
        
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
            say f;
            fields << f;
        }

        else

        // if this is a Record declaration, do it here
        if (line[16].equals("R") )
        {
            f.isRecord = true
            f.name = line[18..25]
            f.metadataUsage = line[16]    
            decodeText(f, tx)   
            continueFlag = true;
            say f;
            fields << f;
        }

        else

        // if field continuation is in progress, then do not build a new Field object, just field the extra text
        if (continueFlag & line[18..25].trim().size() < 1 )
        { 
            tx = line[44..79]  
            decodeText(f, tx)            
        }    // end of if

        // handle the next field
        else
        {
            // if prior field is already in progress, then print/use it !
            if (continueFlag)
            {
                say f
                continueFlag = false
                fields << f;
                mapfields.put(f.name.trim(),f)
            }    // end of if
            
            
            // ok, so set up new Field object
            f = new Field()
            fieldCount += 1;
            f.isField = true;
            tx = line[44..79]
            continueFlag = true
            f.name = line[18..25].trim()
            f.metadataUsage = line[16]
            
            // keep field type
            if ( line[34] != ' ' ) 
            { 
                f.type = line[34] 
            }   // end of if
            
            // compute field size
            long s = 0
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
            
            decodeText(f, tx)    
        }
        return f
    }    // end of method
    
    
    
    public fieldCount()
    {
        fields.size()
    }

    // get a map entry, if found, then return map.value else return 'not declared'
    def getRef(k)
    {
        def ky = k.trim()
        return (this.mapfields[ky]==null) ? null : this.mapfields[ky] 
    }

    public getField(def fieldname)
    {
        say "getField for <"+fieldname+">"
        mapfields.each{k,v -> say "mapfield entry key="+k}
        say mapfields.get(fieldname)
        //return fld
    }

    public void loadFields(fn)
    {
        if (fieldreffile) reffile = fn;
        
        // as400 DDS decoder
        new File(fn).eachLine{
        ln->
            // pad out the line to void short string causing invalid subscripts
            ln=ln+="                                                                  "    // add trailing blanks
            //say "\n\n"+ln
        
            if ( ln.trim().size() < 1 || ln[5] != 'A' ) 
            {
                say ln
            }
            else
            {
                captureField(ln)
            }    // end of else
        }    // end of each
        
        // was a Field objeect already in progress, so use it
        if (continueFlag) 
        {
            say f
            fields << f;
            mapfields.put(f.name.trim(),f);
            say "f.name=<"+f.name+">"
        } // end of if
        
    }    // end of method
    

    // main method to test DDS code
    public static void main(String[] args)
    {
        println "============================\nStart of Field Ref File logic :"
        // construct DDS with true boolean to identify this load as a Field reference dictionary 
        def dds = new DDS(true)
        def fn = "./REFFILE"
        if (new File(fn).exists())  
        {
            println "$fn exists"
        } // end of if
        else
        {
            def fn2 = fn+".txt"
            if (new File(fn2).exists())  
            {
                println "$fn2 exists"
                fn = fn2
            } // end of if
            else
            {
                throw new IllegalStateException("Filename $fn cannot be found")
            } // end of else
        }    // end of else
        
        dds.loadFields(fn)
        println "There were "+dds.fieldCount()+" lines"
        def fi = dds.mapfields["FRED3"]
        println "key name is "+fi.name

        fi = dds.getRef("CODE")
        println "key name is "+fi.name
        println "There were "+dds.fieldCount+" fields"

        println "============================\nEnd of Job ============\n"
    }    // end of psvm

}    // end of class