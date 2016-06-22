// another example 

// this example is nice cos you can join both error and output streams into a single result
// to see result together: builder.redirectErrorStream(true);
// then process.getInputStream(); rather than getErrorStream();
import java.io.InputStream; 
import java.io.IOException; 
import java.util.Map;
public class ProcessBuilderTest2 
{     
    public static void main(String[] args) 
    {
        Map<String,String> mp = System.getenv();
        println "---- start of environmental variables -----"
        mp.each{k,v -> 
        def kv = k.padRight(35);
        def vv = v.padRight(35);
        println "$kv $vv" };
        
        println "PWD=<${mp["PWD"]}>"
            
        try 
        { 
            ProcessBuilder builder = new ProcessBuilder("groovy", "test51.groovy"); 
            builder.redirectErrorStream(true);
            Map<String, String> env = builder.environment();
            env.put("SAMPLE", "sample");
            Process process = builder.start();
            InputStream stream = process.getInputStream(); // getErrorStream();
            while (true) 
            {
                int c = stream.read(); 
                if (c == -1) 
                {
                    stream.close(); 
                    break;
                } 
                System.out.print((char)c);
            } // end of while
            
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        } // end of catch
        println "=== end of job ==="
    } // end of main
} // end of class