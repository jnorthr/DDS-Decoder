// uses Java 5.0 var args to construct a string from several bits
// also discover a second Tiger innovation: java.lang.StringBuilder rather than StringBuffer
static String concat (String ... strings) 
{
    StringBuilder sb = new StringBuilder ();
    for (int i = 0; i < strings.length; i++)
    sb.append (strings [i]+" ");
    return sb.toString ();
}

static int sum (int ... numbers) 
{
    int total = 0;
    for (int i = 0; i < numbers.length; i++)
    total += numbers [i];
    return total;
}

println "The sum of 16 + 49 + 231 is "+sum(16, 49, 231)
println concat("Hi","there","kids");