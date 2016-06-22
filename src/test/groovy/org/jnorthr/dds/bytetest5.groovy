import java.text.*

println "---- start of test -----"

Map<String,String> mp = System.getenv();
println "---- start of environmental variables -----"
mp.each{k,v -> 
def kv = k.padRight(35);
def vv = v.padRight(35);
println "$kv $vv" };

println "PWD=<${mp["PWD"]}>"
println "---- end of environmental variables -----\n"

// new Java 5.0 formatting facilities
String pattern = "There are {0, number, integer} days in a {1}."; 
String result = MessageFormat.format(pattern, new Integer(7), "week");
println result;

// java also use printf like C++
float a = 6;
float b = 2;
// had to escape the $ with \$ to make groovy happy
def fmt = "The result of %3\$.1f/%2\$.1f is %1\$.4f%n"
System.out.printf(fmt, a/b, b, a);

def initialSize = 64
def outStream = new ByteArrayOutputStream(initialSize)
def inStream = new ByteArrayInputStream()

byte c = 0xA1; // 1010 0001
char d = (char)c;
short h = c;
int j = 123;
String t = "0123"
outStream.write(c);
outStream.write((byte)d);
outStream.write(h);
outStream.write(j);
outStream.flush();
outStream.close();
//t.each{outStream.write((byte)it);}
//outStream.each{print " outStream:"+it.toHexString()}

//outStream.input();
//def xxx = outStream.input() 
println "-----> end of inStream"





println "----->"
def binary = ["0":"0000", '1':"0001", '2':"0010", '3':"0011", '4':"0100", '5':"0101", '6':"0110", '7':"0111" ]
char[] hex = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','?']
String x = "0123456789ABCDEF"

short[] no = [0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3c, 0x3D, 0x3E, 0x3F, 0x20  ]

binary.each{k,v -> println "k=<${k}> v=<${v}>  class=${k.class.name}"}

no.each{
int i = (it%16)+30
d = (char)it
c = (byte)it
String s = (String)it
println "no.each($it)  d=[${d}]  s=<${s}> i=<${i}>"
if (d <'9') {println "binary of d=${d} = "+binary[s]}
}



short zero = 0x32;
int upperC, lowerC=0;

d = zero
println "zero=${zero}  d=${d}"
x.each{
d = (char)it
c = d
println "it=${it}  d=${d}  c=${c}"
} // end of each





hex.each{
d = it
c = d
println "d=${d}  c=${c}"
}


d = (char)c;
println "c = ${c} and d now="+d

lowerC = c%16;
upperC = c / 16;

println"upper="+upperC
println "lower="+lowerC;


// special handling of bytes where 'sign' bit is set like '1010 0001'=0xA1
if (c<0){
    int k = 0 - c;
    int l = k>>4;
    int m = l+8;
    if (m>0&m<16)
    {
        upperC=m;
    }
    else
    {    upperC=16; } // unknown combination:set to hex(16)=?

    println "c<0="+c+"  k="+k+" l="+l+" m="+m+"   so upperC="+upperC

} // end of if

// sort out correct value for lower digit of a byte
if (lowerC<0)
{
   lowerC = 0 - lowerC; // reverse the sign
   println "lowerC<0 so now lowerC="+lowerC
} // end of if

if (lowerC < 0 | lowerC > 16)
      lowerC=16; // unknown combination:set to hex(16)=?

if (upperC < 0 || upperC > 16 || lowerC <0 || lowerC > 16)
{
    println("c="+c+" @ upperC="+upperC+" lowerC="+lowerC);
}

println ("upper="+hex[upperC])
println ("lower="+hex[lowerC]);
println "-----  end  -------------"
