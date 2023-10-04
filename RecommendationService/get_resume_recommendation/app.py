import json
import boto3
import openai

def lambda_handler(event, context):
    resume_text = convert_s3_file_to_text(json.loads(event['body']).get('objectKey'))
    question = "Provide the below resume content, pretend yourself as an experienced interviewer, review and give some suggestion to the content. \n" + resume_text
    print("Question to Open AI:", question)
    res = get_response_from_open_ai(question)

    return {
        "statusCode": 200,
        "body": json.dumps(
            {
                "message": res,
            }
        ),
    }

def convert_s3_file_to_text(object_key):
    aws_session = boto3.Session(
        aws_access_key_id='xxx',
        aws_secret_access_key='xxx',
    )
    s3 = aws_session.client('s3')

    bucket_name = 't34'
    response = s3.get_object(Bucket=bucket_name, Key=object_key)
    data = response['Body'].read().decode('utf-8')
    return data
    
def get_response_from_open_ai(question):
    openai.api_key = 'xxx'

    response = openai.Completion.create(
        model = "text-davinci-003",
        prompt = question,
        temperature = 1,
        max_tokens = 100)
    
    return response['choices'][0]['text']