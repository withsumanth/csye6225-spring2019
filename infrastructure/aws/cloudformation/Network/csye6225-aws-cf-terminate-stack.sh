if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
  stackname=$1
	VPC_NAME="$stackname"-csye6225-vpc
	echo " Vpc Name = $VPC_NAME"
	VPC_ID=$(aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${VPC_NAME}" --query 'Vpcs[*].{id:VpcId}' --output text)
	if [ -z "$VPC_ID" ]; then
		echo "VPC does not exist"
	else
		terminateOutput=$(aws cloudformation delete-stack --stack-name $stackname)
		if [ $? -eq 0 ]; then
			echo "Deletion in Progress"
	    aws cloudformation wait stack-delete-complete --stack-name $stackname
	    echo $terminateOutput
	    echo "Deletion of stack successful"
	  else
	  echo "Deletion failed"
	  echo $terminateOutput
	  fi
	fi
fi
