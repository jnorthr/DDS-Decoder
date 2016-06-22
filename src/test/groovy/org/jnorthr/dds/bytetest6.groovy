def m = [:]
m << ["0":"0000", "1":"0001","2":"0010","3":"0011","4":"0100","5":"0101", "6":"0110","7":"0111",]
println "m="+m["0"]
def i = 0x30;
int j = i << 4;
short b = (short)j;
println "b="+b
char ch = (byte)i
println "i="+i+" and j="+j+"  ch=<"+ch+">";
def r = 0x20..0x39;
r.each{ch = (byte)it; println "decimal=$it and ch = <"+ch+">";
        println "m="+m[it]    
}