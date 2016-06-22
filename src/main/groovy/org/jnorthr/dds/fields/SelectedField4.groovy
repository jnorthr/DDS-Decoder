// used when DDS has 'S'elect or 'O'mit clause 
@groovy.transform.Canonical
class SelectedField {
    // used to count byte length per field 
    long startingbyte = 0
    long bytesize = 0

    String name
    long size       = 0     // in underlying measures digits or bytes
    int decimals   = -1
    String type    = " "    // s=zoned p=packed b=binary a=alpha blank=alpha d=date z=datetime t=time
    char usage     = ' '    // i/o

    // VALUES declaration
    boolean hasValues   = false
    String values  = ""


    boolean hasSelect   = false
    boolean hasOmit     = false
    String select = ""
    String omit = ""

} // end of class