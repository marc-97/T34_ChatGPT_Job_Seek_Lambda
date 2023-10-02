import json
import boto3

def lambda_handler(event, context):

    # resume_text = convert_s3_file_to_text(event.objectKey)
    


    return {
        "statusCode": 200,
        "body": json.dumps(
            {
                # "message": resume_text,
                "message": event,
                # "location": ip.text.replace("\n", "")
            }
        ),
    }

def convert_s3_file_to_text(object_key):
    s3 = boto3.client('s3')
    bucket_name = 't34/documents'
    response = s3.get_object(Bucket=bucket_name, Key=object_key)
    data = response['Body'].read().decode('utf-8')
    print(data)