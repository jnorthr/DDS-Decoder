import java.io.InputStream; 
import java.io.IOException;

/*
Process Building
The new java.lang.ProcessBuilder class makes it possible to build operating system 
processes and manage a collection of process attributes (the command, an environment, 
a working directory, and whether or not the error stream is redirected). 

One of that class's constructors and the public ProcessBuilder command (String ... command) 
method use the new variable arguments syntax to simplify the specification of a 
command for execution and its arguments. For example, to execute Windows' notepad program, 
and have that program load the autoexec.bat batch file from the root directory 
(on the current drive), specify the code fragment below:
*/
Process p = new ProcessBuilder ("groovyc", "-v").start ();
InputStream stream = p.getErrorStream();
try
{
    while (true) 
    { 
        int c = stream.read(); 
        if (c == -1) 
        {
            stream.close(); 
            break;
        } 
        System.out.print((char)c);
    } 
} 
catch (IOException ex) 
{
    ex.printStackTrace();
}