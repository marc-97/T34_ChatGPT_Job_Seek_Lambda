package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ChatGPTLambda {
    
  public static String chatGPT(String prompt) throws SocketTimeoutException, IOException, JsonProcessingException {
		String url = "https://api.openai.com/v1/chat/completions";
		String model = "gpt-3.5-turbo";

    // Get API Key from AWS secrets Manager to avoid security issue on GitHub
    // Throws the JsonProcessingException
    String myApiKey = new awsSecrets().getSecret();

    // Set the Connection Timeout.
    // Do note that this is in Miliseconds so:
    // 100000ms == 100 Seconds 
    int TIMEOUT = 300000; // 300 Seconds
    
		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
      connection.setConnectTimeout(TIMEOUT);

      // Construct Request Header
      connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + myApiKey);
			
			// Construct the request body
			String setModel = "\"model\": \"" + model + "\"";

      String setRole = "\"role\": \"user\"";
      String setContent = "\"content\": \"" + prompt + "\"";

      String setMessageContent = "{" + setRole + ", " + setContent + "}";
      String setMessage = "\"messages\": [" + setMessageContent + "]";
      
      //String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
			String body = "{"+ setModel +", "+ setMessage +"}";
            
      connection.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(body);
			writer.flush();
			writer.close();

			// Response from ChatGPT
      // Throws SocketTimeoutException and IOException
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			StringBuffer response = new StringBuffer();

			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			br.close();

			// calls the method to extract the message.
			return extractMessageFromJSONResponse(response.toString());

		} catch (SocketTimeoutException e) {
      throw e; //rethrow here as want to handle the exception in main calling class
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
			throw e; //rethrow here as want to handle the exception in main calling class
		} catch (Exception e) {
      throw e;
    }
	}

  public static String extractMessageFromJSONResponse(String response) {
    int start = response.indexOf("content")+ 11;
    int end = response.indexOf("\"", start);
    return response.substring(start, end);
  }

  // Debug Code
  /* 
  public static void main(String[] args) {
    // System.out.println(chatGPT("Hello how are you?"));

    try {
      System.out.println(chatGPT("Suggest one job. I am an English speaker who is fluent in Spanish and German. I enjoy helping people, seeing new places, and solving problems."));
    } catch (SocketTimeoutException e){
        System.out.println("Socket Timeout Exception!");
    } catch (IOException e) {
        System.out.println("IOException!");
    }

   }
  */

}

/* 
Links used for code:
https://rollbar.com/blog/how-to-use-chatgpt-api-with-java/
https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Serverless-AWS-Lambda-example-in-Java
https://platform.openai.com/docs/api-reference/chat/create

Links used for Connection Timeout Exception handling:
https://www.tabnine.com/code/java/methods/java.net.HttpURLConnection/setConnectTimeout
https://stackoverflow.com/questions/6829801/httpurlconnection-setconnecttimeout-has-no-effect
https://blog.airbrake.io/blog/java-exception-handling/sockettimeoutexception


Integrate Java onto AWS: 
https://www.youtube.com/watch?v=MaHxZEBRcT4

// Sample structure of API Call to Chat GPT
curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Hello!"
      }
    ],
    "stream": true
  }'

*/