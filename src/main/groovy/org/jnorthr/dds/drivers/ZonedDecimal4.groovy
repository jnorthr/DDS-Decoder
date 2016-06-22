/* To Do ...
1. build all fromZonedDecimal method - done but only returns BigDecimal
2. allow other numeric parms like int, short, byte, float, long, BigDecimal - done 
3. need arithmetic ability - seems to work
4. align to decimal places
5. ebcdic input
6. keep latest 'to' value
*/

public class ZonedDecimal
{
    boolean audit = false
    def cvt = [:]
    def ran = 'I'..'R'    
    int decimalplaces = 0;

    // latest zoned decimal string value like 1234P
    String zonedecimalstringvalue=""; 
 
    // the big decimal equivalent of zonedecimalstringvalue
   BigDecimal decimalnumbervalue = 0; 
 
    def say(tx) { if (audit) println tx; }
    
    // ======================================
    // default constructor
    public ZonedDecimal()
    { 
        "IJKLMNOPQR".eachWithIndex{k, ix -> 
            String xk = k.toString()
            String xix = ix.toString()
            cvt[xk]=xix;    // I=0 
            cvt[xix]=xk;    // 0=I
        } // end of each 
        
        //cvt.each{k,v-> print "k=$k and v=$v "; println k.class;}
        
    } // end of constructor
    
    // ======================================
    // pad number to left side with zeros
    public addPadding(String zonedecimalstring, int requiredsize)
    {
        int sz = zonedecimalstring.size()
        if ( sz > requiredsize )     
        {
                throw new NumberFormatException("toZonedDecimal() '$zonedecimalstring' zonedecimalstring size of ${sz} char.s exceeds your maximum length of $requiredsize char.s")
        } // end of if

        // increase string by adding zeros to left side until field is 'requiredsize' characters in length
        def paddedzonedecimalstring = zonedecimalstring.padLeft( requiredsize, '0')
        return paddedzonedecimalstring;    
    } // end of method
    

    // ======================================================
    // BigDecimal version of get value
    // Output: BigDecimal, Input:String
    public BigDecimal fromZonedDecimal(String txt)
    {        
        BigDecimal bigdecimal = 0;
        String zonedecimalstring = txt.trim()
       
        if (zonedecimalstring.size() < 1) zonedecimalstring="0";
        
        say "fromZonedDecimal(String $zonedecimalstring)"
        
        
        // Set flag if right-most char of input string is in range of 'I'..'R' 
        // then input value is treated as a negative, 
        // so we need to make sure returned value is minus
        boolean minus = ( zonedecimalstring [ zonedecimalstring.size() - 1 ] in ran ) ? true : false;
        

        // here we rebuild input string taking fullzonedecimalstring digits to left of rightmost char, 
        // which we convert into a digit. So 123R is really a minus 1239 so we return -1239
        def fullzonedecimalstring = zonedecimalstring.substring(0,zonedecimalstring.size()-1) 
        
        def onechar = (minus) ?  cvt [ zonedecimalstring [ zonedecimalstring.size() - 1 ] ] : zonedecimalstring.substring( zonedecimalstring.size() - 1 );
        fullzonedecimalstring += onechar        

        say "... fullzonedecimalstring = <$fullzonedecimalstring>"
        
        try
        {
            // reverse sign of computed zonedecimal if input was negative
            bigdecimal = (minus) ? 0 - Integer.parseInt( fullzonedecimalstring ) : Integer.parseInt( fullzonedecimalstring ); 
        }
        catch (Exception ex)
        {
             throw new NumberFormatException("fromZonedDecimal($inp) found invalid zoned decimal format ")        
        } // end of catch
        
        say "... returned <$bigdecimal> as BigDecimal";

        // keep string version
        zonedecimalstringvalue = fullzonedecimalstring;

        // keep big decimal version
        decimalnumbervalue = bigdecimal;

        return bigdecimal;
    } // end of fromZonedDecimal



    // ======================================================
    // BigDecimal versions
    // Input: BigDecimal, Output:String padded on left with zeros up to maximum requiredsize declared, else fail
    public toZonedDecimal(BigDecimal bigdecimal, int requiredsize)
    {
        say "toZonedDecimal(BigDecimal $bigdecimal, int $requiredsize)"
        def zonedecimal = toZonedDecimal(bigdecimal);
        String paddedzonedecimalstring = addPadding(zonedecimal, requiredsize)
        zonedecimalstringvalue = paddedzonedecimalstring;
        decimalnumbervalue = bigdecimal;
        return paddedzonedecimalstring;
    } // end of toZonedDecimal


    // Input: BigDecimal, Output:String 
    public toZonedDecimal(BigDecimal bigdecimal)
    {
        decimalplaces = 0;
        String bigdecimalstring = bigdecimal.toString()
        say "toZonedDecimal(BigDecimal $bigdecimalstring)";
        bigdecimalstring = toZonedDecimal(bigdecimalstring);
        zonedecimalstringvalue = bigdecimalstring;
        return bigdecimalstring;
    } // end of toZonedDecimal



    // ======================================================
    // byte versions
    // Input: byte, Output:String 
    public toZonedDecimal(byte bytenumber)
    {
        decimalplaces = 0;
        String bytestring = bytenumber.toString()
        bytestring = toZonedDecimal(bytestring);
        zonedecimalstringvalue = bytestring;
        return bytestring;
    } // end of toZonedDecimal


    // Input: byte, integer length of Output:String 
    public toZonedDecimal(byte bytenumber, int requiredsize)
    {
        String bytestring = bytenumber.toString()
        String zonedecimal = toZonedDecimal(bytestring);
        bytestring = addPadding(zonedecimal, requiredsize)
        zonedecimalstringvalue = bytestring;
        return bytestring;
    } // end of toZonedDecimal
    




    // ======================================================
    // short versions
    // Input: short, Output:String 
    public toZonedDecimal(short shortnumber)
    {
        decimalplaces = 0;
        String shortstring = shortnumber.toString()
        say "toZonedDecimal(short $shortstring)";
        shortstring = toZonedDecimal(shortstring);
        zonedecimalstringvalue = shortstring;
        return shortstring;
    } // end of toZonedDecimal


    // Input: int, Output:String  padded on left with zeros up to maximum requiredsize declared, else fail
    public toZonedDecimal(short shortnumber, int requiredsize)
    {
        String zonedecimal = toZonedDecimal( shortnumber );
        String ans = addPadding(zonedecimal, requiredsize)
        zonedecimalstringvalue = ans;
        return ans;
    } // end of toZonedDecimal



    // ======================================================
    // int versions
    // Input: int, Output:String 
    public toZonedDecimal(int intnumber)
    {
        decimalplaces = 0;
        String intstring = intnumber.toString()
        say "toZonedDecimal(int $intstring)";
        intstring = toZonedDecimal(intstring);
        zonedecimalstringvalue = intstring;
        return intstring;
    } // end of toZonedDecimal


    // Input: int, Output:String  padded on left with zeros up to maximum requiredsize declared, else fail
    public toZonedDecimal(int intnumber, int requiredsize)
    {
        String zonedecimalstring = toZonedDecimal( intnumber );
        String paddedzonedecimalstring = addPadding(zonedecimalstring, requiredsize)
        zonedecimalstringvalue = paddedzonedecimalstring;
        return paddedzonedecimalstring;
    } // end of toZonedDecimal


    // ======================================================
    // long versions
    // Input: long, Output:String 
    public toZonedDecimal(long longnumber)
    {
        decimalplaces = 0;
        String longstring = longnumber.toString()
        say "toZonedDecimal(long $longstring)";
        longstring = toZonedDecimal(longstring);
        zonedecimalstringvalue = longstring;
        return longstring;
    } // end of toZonedDecimal


    // Input: long, Output:String  padded on left with zeros up to maximum requiredsize declared, else fail
    public toZonedDecimal(long longnumber, int requiredsize)
    {
        String zonedecimalstring = toZonedDecimal( longnumber );
        String paddedzonedecimalstring = addPadding(zonedecimalstring, requiredsize)
        zonedecimalstringvalue = paddedzonedecimalstring;
        return paddedzonedecimalstring;
    } // end of toZonedDecimal



    // ======================================================
    // float versions
    // Input: long, Output:String 
    public toZonedDecimal(float floatnumber)
    {
        decimalplaces = 0;
        String floatstring = floatnumber.toString()
        say "toZonedDecimal(float $floatstring)";
        String zonedecimalstring = toZonedDecimal(floatstring);
        zonedecimalstringvalue = zonedecimalstring;
        return zonedecimalstring;
    } // end of toZonedDecimal


    // Input: long, Output:String 
    public toZonedDecimal(float floatnumber, int requiredsize)
    {
        String floatstring = floatnumber.toString()
        say "toZonedDecimal(float $floatstring)";
        String zonedecimalstring = toZonedDecimal(floatstring);
        def paddedzonedecimalstring = addPadding(zonedecimalstring, requiredsize)
        zonedecimalstringvalue = paddedzonedecimalstring;
        return paddedzonedecimalstring;
    } // end of toZonedDecimal




    // ======================================================
    // String versions
    // Input: String, int is length of returned Output:String padded on left with zeros up to maximum requiredsize declared, else fail
    public toZonedDecimal(String stringnumber, int requiredsize)
    {
        String zonedecimalstring = toZonedDecimal( stringnumber );
        String paddedzonedecimalstring = addPadding(zonedecimalstring, requiredsize)
        zonedecimalstringvalue = paddedzonedecimalstring;
        return paddedzonedecimalstring;
    } // end of toZonedDecimal

    
    // turn number into a zoned decimal format where sign is over right-most digit
    // and may appear as a char. So 'I'..'R'' is really '0'..'9' with a minus sign
    // and the number is then negative
    // Input - String; Output - String;
    // valid parms would include 123  -123  12R 
    // invalid parms would include A123 123A 234Q -234R
    public toZonedDecimal(String stringnumberinput)
    {
        def number = 0;
        String xx;
        String numberstring;
        String x;
        String full;

        decimalplaces = 0;
        
        if ( stringnumberinput==null || stringnumberinput.size() < 1 )     
        {
                throw new IllegalStateException("toZonedDecimal() found no input. ")
        } // end of if


        String stringnumber = (stringnumberinput.startsWith("+")) ?  stringnumberinput.substring(1)  : stringnumberinput;
        say "... from $stringnumberinput we have stringnumber=$stringnumber"

        int j = stringnumber.indexOf(".");
        if (j>0)
        {
            def q1 = stringnumber.substring(0,j)
            def q2 = stringnumber.substring(j+1)
            int decimals = q2.size()
            decimalplaces = decimals;
            String q3 = q1+q2
            stringnumber = q3;
            say "... dot found in <$stringnumber> so j=$j  and q1=<$q1> and q2=<$q2> decimals=<$decimals> "
        } // end of if
        

        try{        
            say "\ntrying...$stringnumber"
            number = Integer.parseInt(stringnumber)
            numberstring = number.toString()
            say "converted...$number numberstring.size()=${numberstring.size()}"
            xx = numberstring.substring( 0, numberstring.size() - 1 )
            say "value is "+number+" and xx="+xx+" and numberstring="+numberstring
            
            // negative values
            if (number < 0)
            {
                number = 0 - number
                numberstring = number.toString()
                xx = numberstring.substring( 0, numberstring.size() - 1)
                x = numberstring [ numberstring.size() - 1 ]
                def x5 = cvt[x]
                full = xx+x5;
                say "number is "+number+" and xx="+xx+" and numberstring="+numberstring+" and x="+x+" which converts to "+x5+" giving "+full
            } // end of if
            else
            {
                full = numberstring
            } // end of else
        } // end of try
        
        // check to see if this was already a valid zoned decimal number formatted like 123I
        catch (NumberFormatException q) 
        {
            say "caught..."
            //q.printStackTrace()
            x = stringnumber [ stringnumber.size() - 1 ]

            // if rightmost char. in 'I'..'R' range, then that's ok for ZonedDecimals
            // next just check leading char.s for a valid numeric value, else throw a fit
            if (x in ran)
            { 
                say "x in ran:$x=" + cvt[x];
                def partialstringnumber = stringnumber.substring( 0, stringnumber.size() - 1 );
                say "partialstringnumber="+partialstringnumber 
                
                if (partialstringnumber.startsWith("-"))
                {
                    throw new NumberFormatException("toZonedDecimal($stringnumber) found invalid zoned decimal input of 2 minus signs ")
                }
                
                try{ number = Integer.parseInt(partialstringnumber); } 
                catch (NumberFormatException q3) 
                {
                    throw new NumberFormatException("toZonedDecimal() found invalid zoned decimal input of <$stringnumber> ")
                } // end of catch
                
                say "number="+number
                full = stringnumber
            } // end of if
            
            // here we arrive if rightmost char. of input stream is not in range of 'I'..'R' 
            else
            {
                throw new NumberFormatException("toZonedDecimal() found invalid zoned decimal input of <$stringnumber> ")
            } // end of else
            
        } // end of catch
        
        zonedecimalstringvalue = full;
        return full;
    } // end of toZonedDecimal
    

    
    // return zone decimal string from latest to... call
    public String getZoneDecimalString()
    {
       return zonedecimalstringvalue;
    } // end of method
    
    // return zone decimal value from latest to... call
    public BigDecimal getZoneDecimalNumber()
    {
       return decimalnumbervalue;
    } // end of method
    

    
    // =======================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        ZonedDecimal zd = new ZonedDecimal();

        BigDecimal bd = -123.4567;

        // BigDecimal bd = -123.4567; padded with zeros on left to nine places
        assert "bd=<-123.4567>00123456P" == "bd=<"+bd+">"+zd.toZonedDecimal(bd, 9);        
        assert "00123456P & 4 decimals" == zd.toZonedDecimal(bd, 9)+" & "+zd.decimalplaces+" decimals" ;
        
        
        assert "main: 123=123" == "main: 123="+zd.toZonedDecimal("123")
        assert "main: -123=12L" == "main: -123="+zd.toZonedDecimal("-123")
        assert "main: -1234567=123456P" == "main: -1234567="+zd.toZonedDecimal("-1234567")
        assert "main: -123459=<00000000000012345R>"=="main: -123459=<"+zd.toZonedDecimal("-123459", 18)+">"
        assert "main: +234R=234R" == "main: +234R="+zd.toZonedDecimal("+234R")
        
        long l = -123456789;
        assert "long l=<-123456789> 12345678R" == "long l=<"+l+"> "+zd.toZonedDecimal(l);
        
        
        float f = -123.4567;
        assert "float f=<-123.4567>123456P & 4 decimals" == "float f=<"+f+">"+zd.toZonedDecimal(f)+" & "+zd.decimalplaces+" decimals" ;
        int outsize = 12;
        assert "float f=<-123.4567>00000123456P & 4 decimals to create an output field of 12 bytes" == "float f=<"+f+">"+zd.toZonedDecimal(f,outsize)+" & "+zd.decimalplaces+" decimals to create an output field of $outsize bytes" ;
        
        int rr = -47;
        assert "rr=<-47>4P" == "rr=<"+rr+">"+zd.toZonedDecimal(rr);
        assert "rr=<-47>00004P" == "rr=<"+rr+">"+zd.toZonedDecimal(rr, 6);


        short sh = -16384;
        assert "sh=<-16384>1638M" == "sh=<"+sh+">"+zd.toZonedDecimal(sh);
        assert "sh=<-16384>01638M" == "sh=<"+sh+">"+zd.toZonedDecimal(sh, 6);



        byte by = -127;
        assert "by=<-127>12P" =="by=<"+by+">"+zd.toZonedDecimal(by);
        assert "by=<-127>012P" == "by=<"+by+">"+zd.toZonedDecimal(by,4);
        assert "---> by=<-127>012P = current:012P" == "---> by=<-127>012P = current:"+zd.getZoneDecimalString();


        def h = 0x4f;
        assert "h=<79>000079" ==  "h=<"+h+">"+zd.toZonedDecimal(h,6);
        assert "---> h=<79>000079 = current:000079" == "---> h=<79>000079 = current:"+zd.getZoneDecimalString();


        def bigd = zd.fromZonedDecimal("123R")
        assert "zd.fromZonedDecimal(\"123R\") =<-1239>" == "zd.fromZonedDecimal(\"123R\") =<${bigd}>"

        bigd = zd.fromZonedDecimal("1239")
        println "zd.fromZonedDecimal(\"1239\") =<${bigd}>\n"

        bigd = zd.fromZonedDecimal("I")
        println "zd.fromZonedDecimal(\"I\") =<${bigd}>\n"

        bigd = zd.fromZonedDecimal(" ")
        println "zd.fromZonedDecimal(\" \") =<${bigd}>\n"

        bigd = zd.fromZonedDecimal("")
        println "zd.fromZonedDecimal(\"\") =<${bigd}>\n"

        bigd = zd.fromZonedDecimal("123R") + zd.fromZonedDecimal("1J") + rr
        assert "... complex 123R + 1J -47 =<-1297>" == "... complex 123R + 1J -47 =<${bigd}>"
        assert bigd == -1297;

        println "======================\nEnd of Job ---\nJob Completion Without Failures\n==============================="
    }    // end of psvm
} // end of class