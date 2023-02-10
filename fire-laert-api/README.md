# Getting started with the project
This project is developed on top of Java with Quarkus and build using SAM(Serverless Application Model). Purpose of this project is create two AWS Lambda function which consume kafka topic and store data in AWS ElastiCache

# SAM Template

# template.yaml
this is the yaml file conatining all the lambda functions, roles, events configuration.

# KafkaConsumerLambdaFunction
This is a serverless function build on top of Java with quarkus. There are some envrironment variables added in the lambda function(REDIS_SERVER, KAFKA_BOOTSTRAP_URI) and value of these envrironment variables coming from AWS Parameter store. Purpose of these function is to consume kafka toppic and store in AWS ElastiCache Redis Cluster

# MSKEvent
This is AWS MSK Event attached to the KafkaConsumerLambdaFunction. This event has some properties like MSK Server URL as stream, topic name etc. When Kafka producer start producing data , thsi event will trigger and consumer that data and pass the data to KafkaConsumerLambdaFunction and then that function will store the data to ElastiCache

# KakfaConsumerLambdaExecutionRole
This execution role attached to both of the Lambda function. This execution role has serveral aws managed policies which basically grant permission to access elasticache, msk cluster etc

# CacheConsumerLambdaFunction
Purpose of this function is to search data based on on some condition from elasticache . Below is one example
"expression": "$.[?(@.ARR_TRPN_STN_CD == 'MCO' && @.DPRT_TRPN_STN_CD == 'ATL' && @.FLT_ORIG_LDT == '06-DEC-22' && @.MKD_FLT_NB == '2553')]". It's JSOn exression used to search data from ElastiCache

# Build ad Deploy
mvn clean install ->  run with all the test cases and create a zip file name function.zip
sam deploy  --guided --capabilities CAPABILITY_AUTO_EXPAND CAPABILITY_NAMED_IAM CAPABILITY_IAM ->  create a SAM deployment on top of AWS Cloudformation and deploy those lambda function