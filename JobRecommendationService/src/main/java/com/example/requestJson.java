package com.example;

// "requestJson" is used as the mapping object that AWS Lambda tries to map the request to.
// Its also a known as a DTO

public class requestJson {
    private String text;

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}

/*  
Links:
https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html
https://openjdk.org/jeps/395
*/

