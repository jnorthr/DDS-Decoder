DDS-Decoder
===========

// AS400 Data Description Specification Decoder
// Written in Groovy

// TO DO List
1. Finish zoned, packed,binary number alignments
2. DDS for output file
3. determine order of processing input: dds or data file 1st, .txt file suffix if needed
4. hash totals for numerics and record counts
5. implement select/omit for inputs
6. how to read/write byte streams
7. figure out dates/times etc.

//======================================
TestddsEachLine.groovy - looks like logic to decode DDSTest
Field.groovy - holds one field's attributes
SelectedField.groovy - when DDS uses 'S'elect or 'O'mit clause
Test.groovy - a test harness to load a DDS source and query it's content
ZonedDecimal.groovy - mimic as/400 style numners stored in hex 0102030d fmt
ProcessDDS.groovy - ??
PhysicalFile.groovy - more like chkobj for files
Key.groovy - holds one key of the DDS source
Keys.groovy - holds an array of key/select/omit declarations
DSPFFD.groovy - reads output spoolfile of DSPFFD command and makes an internal dds equivalent
DDSSupport.groovy - utility methods for the DDS class to log / echo & load field ref files
DDS.groovy - the 'do it all' module
