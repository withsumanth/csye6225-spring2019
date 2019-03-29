STACK_NAME=$1
KEY_NAME=$2
if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
  if [ -z "$2" ]; then
  	echo "Please provide a Aws keyPair name"
  else
		echo "Creating application stack"
		domain=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
		bname=${domain::-1}
		BucketName="${bname}.csye6225.com"
		echo $BucketName
		AMI_ID=$(aws ec2 describe-images --filters "Name=tag:Base_AMI_Name,Values=ami-9887c6e7" --query 'reverse(sort_by(Images,&CreationDate)[].ImageId)[0]' --output text)
		stackCreation=$(aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application-stack.json --parameters ParameterKey=stackName,ParameterValue=$STACK_NAME ParameterKey=keyPair,ParameterValue=$KEY_NAME ParameterKey=amiId,ParameterValue=$AMI_ID ParameterKey=s3Bucket,ParameterValue=$BucketName ParameterKey=domainName,ParameterValue=$bname)
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
fi
