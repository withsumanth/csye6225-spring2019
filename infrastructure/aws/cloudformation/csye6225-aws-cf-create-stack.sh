if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
  echo "Creating VPC Stack"
  stackName=$1
  echo $stackName-csye6225-vpc
  stackCreation=$(aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-networking.json --parameters ParameterKey=stackName,ParameterValue=$stackName)
  if [ $? -eq 0 ]; then
    stackCompletion=$(aws cloudformation wait stack-create-complete --stack-name $stackName)
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
