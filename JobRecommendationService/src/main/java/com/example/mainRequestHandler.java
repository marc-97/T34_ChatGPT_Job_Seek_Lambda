/*
This class is the main handler/driver for the Lambda requests.

1. It uses a Data Transfer Object (DTO), "requestJson", for AWS to perform mapping of 
the incoming data from frontend.

2. Next it extracts out the relevant portion of the data with the string query to be sent
to ChatGPT. 

3. It then sends this data to ChatGPT for processing and awaits the result.

4. After receiving the reply from ChatGPT, it will construct a DTO, "Response", with the 
reply within.

5. Return the "Response" DTO back to the caller.   
*/

package com.example;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.HashMap;
// import static java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger; // For Debug
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class mainRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context){
    // 1. "requestJson" is used as the mapping object that AWS Lambda tries to map the request to.

    // Debug Code to output to AWS Logger
    // LambdaLogger logger = context.getLogger();
    // logger.log("Function '" + context.getFunctionName() + "' called");
    // logger.log(event.toString());

    // 2. Extract query to be sent to ChatGPT 
    String requestString = event.getBody(); // Extract Query from request
    
    // Parse the input into a JSON Object for usage
    JSONParser parser = new JSONParser(); 
    JSONObject requestJsonObject = new JSONObject();
    try {
      requestJsonObject = (JSONObject) parser.parse(requestString);
    } catch (ParseException e) {
      LambdaLogger logger = context.getLogger();
      logger.log(e.toString());
    }
    String requestMessage = "";
    if (requestJsonObject != null) {
      if (requestJsonObject.get("text") != null) {
          requestMessage = requestJsonObject.get("text").toString();
      }
    }
    String chatgptQuery = requestMessage;

    // 3. Send Data to ChatGPT for processing and get Result
    // String chatgptOutput = ChatGPTLambda.chatGPT(chatgptQuery); // Static call to the ChatGPT Class

    // Instantiation of Variable
    String chatgptOutput = ""; 
    int httpSuccessCode = 200;
    int httpFailCode = 500;
    int responseCode = 0;

    try {
      // Populate variable with data
      chatgptOutput = ChatGPTLambda.chatGPT(chatgptQuery); // Static call to the ChatGPT Class
      responseCode = httpSuccessCode;
    } catch (SocketTimeoutException e){
        String errorMessage = "Socket Timeout Exception!";
        chatgptOutput = errorMessage;
        responseCode = httpFailCode;
    } catch (JsonProcessingException e) {
      String errorMessage = "Json Processing Exception!";
      chatgptOutput = errorMessage;
      responseCode = httpFailCode;
    } catch (IOException e) {
      String errorMessage = "IOException!";
      chatgptOutput = errorMessage;
      responseCode = httpFailCode;
    } catch (Exception e) {
      String errorMessage = e.toString();
      chatgptOutput = errorMessage;
      responseCode = httpFailCode;
    }

    // 4. Crafting the "Response" DTO
    //Response temp = new Response();
    //temp.setStatusCode(responseCode);
    //temp.setResponseMessage(chatgptOutput);

    // Crafting Response to be sent back
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
    // Set Headers for response
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Access-Control-Allow-Origin", "*");
    response.setHeaders(headers);

    
    response.setBody(chatgptOutput);
    response.setStatusCode(responseCode);
    //logger.info("response - body: {} status: {}", responseEvent.getBody(), responseEvent.getStatusCode());    
    
    // 5. Return DTO to caller
    return response; 
  } 
}

/* 
Links used:
https://docs.aws.amazon.com/lambda/latest/dg/java-context.html
https://www.youtube.com/watch?v=kyWllXOGMWQ&ab_channel=DanVega

New Links used:
https://www.youtube.com/watch?v=Fp-9s-0x0jU&ab_channel=Easy2Excel
https://dzone.com/articles/calling-lambda-function-through-aws-api-gateway
https://javadoc.io/doc/com.amazonaws/aws-lambda-java-events/3.10.0/com/amazonaws/services/lambda/runtime/events/APIGatewayProxyResponseEvent.html#setHeaders-java.util.Map-
*/