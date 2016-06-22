@groovy.transform.Canonical
class Field {
    boolean isComment   = false
    boolean isRecord    = false
    boolean isField     = false
    boolean hasReference       = false

    char metadataUsage    = ' '    // R,K,J,S,O ??
    String name
    long size       = 0     // in underlying measures digits or bytes
    int decimals   = -1
    String type    = " "    // s=zoned p=packed b=binary a=alpha blank=alpha d=date z=datetime t=time
    char usage     = ' '    // i/o
    String alias    = ""
    String text    = ""

    String datfmt   = ""    // Date Format . . . . . . . . . . . . . . . :  *ISO
    String timfmt   = ""    // Time Format . . . . . . . . . . . . . . . :  *ISO
    String ccsid    = ""    // Coded Character Set Identifier


    // column headings go here
    String colhdg1 = " "
    String colhdg2 = " "
    String colhdg3 = " "

    // editing formats for numeric and date/time fields
    String edtwrd  = ""
    String edtcde  = ""

    // VALUES declaration
    boolean hasValues   = false
    String values  = ""

    // not really a metadata value, just used to hold DDS comments
    String comment = ""


    // key related values
    boolean isKey       = false
    boolean hasSelect   = false
    boolean hasOmit     = false
    boolean descend = false

    int keyno = 0
    String select = ""
    String omit = ""

    // used to count byte length per field 
    long startingbyte = 0
    long bytesize = 0

    String toString()
    {
        def ic = (isComment) ? "yes" : "no "
        def ir = (isRecord) ? "yes" : "no "
        def ifi = (isField) ? "yes" : "no "
        def hr = (hasReference)  ? "yes" : "no "
        def va = (hasValues) ? "Values="+values : "*no values"
        "isComment isRecord isField hasReference Meta Name        Size Decimals Type Use Edit Wd  Edit Cd Alias -------------------       Text\n  $ic        $ir     $ifi       $hr        $metadataUsage  ${name?.padRight(10)}   ${size?.toString().padRight(3)}    ${decimals?.toString().padRight(3)}    $type $usage ${edtwrd?.padRight(10)} ${edtcde} '${alias?.padRight(30)}' '${text?.padRight(30)}' '${colhdg1?.padRight(30)}' '${colhdg2?.padRight(30)}' '${colhdg3?.padRight(30)}' '$comment' \n$va\n"
    }
}    // end of Field