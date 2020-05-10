package org.tulg.roundback.core;

public class StringTokenizer {
    private String originalString;
    private int currfield;
    private String[] fields;




    public StringTokenizer(String oString){
        this.originalString = oString;
        currfield = -1;
        fields = originalString.split(" ");
    }


    public String getOriginalString() {
        return this.originalString;
    }

    public int getCurrfield() {
        return this.currfield;
    }

    public void setCurrfield(int currfield) {
        this.currfield = currfield;
    }

    public String[] getFields() {
        return this.fields;
    }

    public boolean hasMoreTokens(){
        if(currfield + 1 < fields.length){
            return true;
        }
        return false;
    }

    public String nextToken(){
        currfield++;
        return fields[currfield];
    }

    public String currentToken(){
        if(currfield < 0){
            return null;
        }
        return fields[currfield];
    }

    public String fromCurrentToken(){
        if(currfield < 0){
            return null;
        }
        int length = 0;
        for (int i=0; i<currfield+1; i++){
            length = length + fields[i].length();
        }

        // something some somethings sometimes somtheother thing
        // 0         1    2          3         4           5     - fields
        //---------------------------------------------------------------
        // 0        11111111112222222222333333333344444444445555
        // 12345678901234567890123456789012345678901234567890123

        // add in the spaces
        length = length + currfield + 1;
        if(length<0){
            return null;
        }
        return originalString.substring(length);

    }
}