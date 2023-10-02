import json
import boto3

def lambda_handler(event, context):

    resumeText = convert_s3_file_to_text(event['obectKey'])
    print(resumeText)

    return {
        "statusCode": 200,
        "body": json.dumps(
            {
                "message": "hello world",
                # "location": ip.text.replace("\n", "")
            }
        ),
    }

def convert_s3_file_to_text(obect_key):
    s3 = boto3.client('s3')
    bucket_name = 't34/documents'
    object_key = 'your-file-name'
    response = s3.get_object(Bucket=bucket_name, Key=object_key)
    data = response['Body'].read().decode('utf-8')
    print(data)