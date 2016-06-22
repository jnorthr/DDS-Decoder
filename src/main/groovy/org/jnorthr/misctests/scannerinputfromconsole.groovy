import java.util.*

// read an int from command line input 
// clearly better than the old way 
Scanner sc = new Scanner(System.in); 
int i = sc.nextInt();
// read some ints and strings with a 'fish' delimiter 
String input = "1 fish 2 fish red fish blue fish"; 
Scanner s = new Scanner(input).useDelimiter("\\s*fish\\s*"); 
System.out.println(s.nextInt()); 
System.out.println(s.nextInt()); 
System.out.println(s.next());
System.out.println(s.next()); 
s.close();
// more processbuilder