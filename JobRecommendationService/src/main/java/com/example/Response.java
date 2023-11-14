package com.example;

// The DTO object used to send the status code and ChatGPT response back to the caller.
public class Response {

    private int statusCode;
    private String responseMessage = "";

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "Response{" +
        "statusCode=" + statusCode +
        ", responseMessage='" + responseMessage + '\'' +
        '}';
    }
}
