@groovy.transform.Canonical
class Keys {

    def keyarray = []

    // key related values
    int keyno = 0
    boolean inSelect = false
    boolean inOmit = false
    String statement ="";

    // default constructor
    public Keys()
    {
        super();
    } // end of constructor
    

    // adjust Key object passed in as a parameter
    public add(Key k)
    {
        k.isKey = false;
        k.keyno = 0;
        k.andor = false
        k.andand = false       
        k.hasSelect = false
        k.hasOmit = false

        switch(k.usage)
        {

            case 'K' :  k.isKey = true;
                        keyno+=1;
                        k.keyno = keyno;
                        inSelect = false;
                        inOmit = false; 
                        break;

            case 'S' :  k.hasSelect=true;
                        inSelect = true;
                        inOmit = false; 
                        break;

            case 'O' :  k.hasOmit=true;
                        inSelect = false;
                        inOmit = true;
                        break;

            case ' ' :  if (inSelect && k.select.size() > 0) 
                        {
                            k.usage = 'S'
                            k.hasSelect = true
                            k.andor = true   // mark more than one select as OR's = R   
                            inOmit = false; 
                        } // end of if

                        if (inOmit && k.omit.size() > 0) 
                        {
                            k.usage = 'O'
                            k.hasOmit = true
                            k.andand = true  // mark more than one omit as AND's = A   
                            inSelect = false;
                        } // end of if
                        break;
        } // end of switch

        keyarray << k;
    } // end of add


    // construct sql-like selection statement
    String buildSelect()
    {
        def statement="";
        keyarray.each
        { k -> 
            if (k.hasSelect)
            statement+= "\n"+k.toString()+"\n" 
        } // end of each 

        return statement;    
    }  // end of buildSelect  


    String toString()
    {
        def reply = "keyno=$keyno \n"
        keyarray.each{k -> reply+= "\n"+k.toString()+"\n" }
        return reply
    } // end of toString()



    // ========================================    
    // main method to test
    public static void main(String[] args)
    {
        println "======================\nStart of Job ---"
        def keys = new Keys()

        Key s1 = new Key([isKey:true, keyno: 15, usage:'K', name: 'ST', text:'This is a state', alias:'State Abbrev.']) 
        keys.add(s1)

        s1 = new Key([usage:'S', name: 'ST', text:'This is a state key', alias:'State Abbrev. Key', select:'COMP(EQ ’NY’)']) 
        keys.add(s1)

        s1 = new Key([usage:' ', name: 'ST', select:'COMP(EQ ’OK’)']) 
        keys.add(s1)


        s1 = new Key([usage:'O', name: 'FIELDA', text:'Omit this state key', alias:'Ignore State Abbrev. Key', omit:'COMP(GE 78)']) 
        keys.add(s1)

        s1 = new Key([usage:' ', name: 'FIELDA', omit:'COMP(NE 16)']) 
        keys.add(s1)

        println keys
        println "----------------"
        //println keys.buildSelect()
        println "======================\nEnd of Job ---"

    }    // end of psvm            
}    // end of Field