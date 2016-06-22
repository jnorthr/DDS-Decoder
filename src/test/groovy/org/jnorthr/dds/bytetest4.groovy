import java.io.*;

final int BUFFER = 2048;
def fn = "./data/byte.txt"
RandomAccessFile input;
boolean eof = false;

// landing zone for each input byte
short c;
byte temp = 0;

// counts number of char.s read from input for manual EOF detection
int readCount = 0;

char[] hex = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','?'];

try
{
   input = new RandomAccessFile(fn,"r");        
   c = readIt(input);

   while(!eof)
   {
       c = readIt(input);
   }    
} 
catch(Exception e) 
{
         e.printStackTrace();
}

input.close();


/**
 * method to take the next unsigned byte from the input stream and detect EOF as the
 * RandomAccessFile has no methods for EOF detection
 * @return
 * @throws java.io.IOException
 */
short readIt(input) throws IOException
{
    def readCount = 0;
    try
    {
       if ( readCount++ < input.length())
       {
           int c = input.readUnsignedByte();
           short t = (short)c;
           return t;
       } // end of if
       else
       {
           eof = true;
           return (short)-1;
       } // end of else

    } // end of try

    catch (IOException ex)
    {
        throw new IOException("unknown problem on input");
    }
} // end of readIt()





println "= end of job ========="