Step 1 : Steps to deploy the required infrastructure using AWS Cloudformation:

--Run the script using command sh ./csye6225-aws-cf-create-stack.sh <stack_name>
--The logs for successful creation of resources is displayed if the infrastructure is deployed. Else, the error messages are displayed 

Steps to teardown the infrastructure deployed using AWS Cloudformation: 
--Run the script using command sh ./csye6225-aws-cf-terminate-stack.sh <stack_name>
--The logs for successful deletion of resources is displayed if the infrastructure is torn down. Else, the error messages are displayed.

Step 2 : Steps to create and configure required application resources using AWS CloudFormation

--Run Step 1 
--Run create application stack script using sh ./csye6225-aws-cf-create-application-stack.sh <stack_name> <key_name>

Step to teardown the application CloudFormation stack

--Run terminate application stack usinf sh ./csye6225-aws-cf-terminate-application-stack.sh <stack_name>
