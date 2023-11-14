package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.anyString;


@ExtendWith(MockitoExtension.class)
public class SimpleHandlerTest {
    
    private SimpleHandler simpleHandler;

    @Mock
    Context context;

    @Mock
    LambdaLogger logger;

    @BeforeEach
    public void setup() {
        when(context.getLogger()).thenReturn(logger);

        doAnswer(call -> {
            System.out.println((String)call.getArgument(0));
            return null;
        }).when(logger).log(anyString());

        simpleHandler = new SimpleHandler();

    }

    @Test
    void shouldReturnUppercaseOfInput(){
        //SimpleHandler sut = new SimpleHandler();
        // String expectedOutput = "HELLO WORLD!";

        when(context.getFunctionName()).thenReturn("handleRequest");
        Assertions.assertEquals("HELLO WORLD!", simpleHandler.handleRequest("hello world!", context));

    }

}

/*
Links used:
https://stackoverflow.com/questions/7322705/finding-import-static-statements-for-mockito-constructs




 */
