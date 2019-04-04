STACK_NAME=$1
if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
		echo "Creating serverless stack"
		domain=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
		bname=${domain::-1}
		echo $bname
		stackCreation=$(aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-serverless-stack.json --parameters ParameterKey=domainName,ParameterValue=$bname)
		if [ $? -eq 0 ]; then
			stackCompletion=$(aws cloudformation wait stack-create-complete --stack-name $STACK_NAME)
			if [ $? -eq 0 ]; then
				echo "Stack creation successful"
				fnUpdate=$(aws lambda update-function-configuration --function-name PasswordResetLambda --handler ResetPasswordEmail::handleRequest --runtime java8)

			else
				echo "Error in creating CloudFormation"
			fi
		else
			echo "Error in creating CloudFormation"
			echo $stackCreation
		fi
  
fi
