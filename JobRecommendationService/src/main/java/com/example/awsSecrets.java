/*
This class is to retrieve the API Key stored in AWS Secrets Manager. 

Do include "Layers" in the Lambda so it can access the AWS Secrets Manager. 

Also need to grant the AWS Lambda Role under Configuration -> Permissions 
Once you're at IAM, give the SecretsManagerReadWrite to the Lambda 
*/

package com.example;

// Used to Retrieve the API Key from AWS Secrets Manager
import software.amazon.lambda.powertools.parameters.SecretsProvider;
import software.amazon.lambda.powertools.parameters.ParamManager;

// Imports to help with parsing the JSON into a String for usage
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class awsSecrets  {
    
    public String getSecret() throws JsonProcessingException {

        // Method 1
        // Code to retrieve secrets from AWS Secrets Manager.
        /*
        // Get an instance of the Secrets Provider
        SecretsProvider secretsProvider = ParamManager.getSecretsProvider();

        // Retrieve a single secret
        String value = secretsProvider.get("cgptkey");
        
        TypeReference<HashMap<String, String>>typeRef = new TypeReference<HashMap<String, String>>(){}; 
        
        ObjectMapper mapper = new ObjectMapper();

        // Here will throw the JsonProcessingException
        Map<String, String> map = mapper.readValue(value, typeRef);

        return map.get("chatgptapikey");
        
        // Debug
        //System.out.println(map);
        //System.out.println(map.get("chatgptapikey"));

        */

        // Method 2
        // Alternate method to retrieve API key stored in environment variable
        String apikey = System.getenv("chatgptapikey");
        // System.out.println(apikey);
        return apikey;



    }
    
}

/* 
Links used:
// Method 1
// Main code used for the script
https://aws.amazon.com/blogs/compute/securely-retrieving-secrets-with-aws-lambda/
https://docs.powertools.aws.dev/lambda/java/utilities/parameters/

// Serailization
https://www.baeldung.com/jackson-map
https://fasterxml.github.io/jackson-core/javadoc/2.7/com/fasterxml/jackson/core/JsonProcessingException.html
https://www.geeksforgeeks.org/map-get-method-in-java-with-examples/

// Debugging errors
https://stackoverflow.com/questions/7421612/slf4j-failed-to-load-class-org-slf4j-impl-staticloggerbinder
https://bobbyhadz.com/blog/aws-grant-lambda-access-to-secrets-manager
https://docs.aws.amazon.com/secretsmanager/latest/userguide/retrieving-secrets_lambda.html

// Method 2
https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html
*/