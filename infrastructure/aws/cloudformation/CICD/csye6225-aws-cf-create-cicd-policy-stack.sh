#!/bin/bash

STACK_NAME=$1
if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
	echo "Creating policies stack"
	ACC_ID=$(aws sts get-caller-identity --output text --query 'Account')
	domain=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
	BucketName=${domain::-1}
	echo $BucketName
	stackCreation=$(aws cloudformation create-stack \
		--stack-name $STACK_NAME  \
		--template-body file://csye6225-aws-cf-create-cicd-policy-stack.json \
		--capabilities CAPABILITY_NAMED_IAM \
		--parameters  ParameterKey="s3bucket",ParameterValue=$BucketName ParameterKey="accid",ParameterValue=$ACC_ID \
		--disable-rollback)
		if [ $? -eq 0 ]; then
			stackCompletion=$(aws cloudformation wait stack-create-complete --stack-name $STACK_NAME)
			if [ $? -eq 0 ]; then
				echo "Stack creation successful"
			else
				echo "Error in creating CloudFormation"
			fi
		else
			echo "Error in creating CloudFormation"
			echo $stackCreation
		fi
fi
