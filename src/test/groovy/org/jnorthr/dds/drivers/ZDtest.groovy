package org.jnorthr.dds.drivers
import spock.lang.*
// ======================================
class ZDtest extends spock.lang.Specification 
{
	@Shared ZonedDecimal zd  = new ZonedDecimal();
	
	def "Convert BigDecimal to ZonedDecimal"(){
        when:
        	BigDecimal bd = -123.4567;
        then:
            "bd=<-123.4567>00123456P" == "bd=<"+bd+">"+zd.toZonedDecimal(bd, 9);
    }    

/*                
        
        
	def "Convert BigDecimal to ZonedDecimal w/4 decimal places"(){
        assert "00123456P & 4 decimals" == zd.toZonedDecimal(bd, 9)+" & "+zd.decimalplaces+" decimals" ;
    }    
        
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
*/

} // end of class