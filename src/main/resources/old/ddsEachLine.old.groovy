// as400 DDS decoder
new File("/Volumes/Data/dev/groovy/DDS/dds.txt").eachLine{
ln->
line=ln+="                                                                  "
print "<"
print line[0..4]
print ":"
print line[5]
print ":"
print line[6]
print ":"
print line[7..15]
print ":"
print line[16]
print ":"
print line[17]    // R,K,J,S,O ??
print ":"
print line[18..23]    // field name
print ":"
print line[24..29]    // spare
print ":"
print line[30..33]    // field size
print ":"
print line[34]    // field type P,S,B,F,A,H,L,Z,T or J,E,O,G  for double byte char. sets
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
}