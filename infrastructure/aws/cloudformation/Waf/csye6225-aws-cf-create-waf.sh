if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
  echo "Creating WAF Stack"
  stackName=$1
  stackCreation=$(aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-waf.yml)
  if [ $? -eq 0 ]; then
    stackCompletion=$(aws cloudformation wait stack-create-complete --stack-name $stackName)
		if [ $? -eq 0 ]; then
			echo "Stack creation successful"
		else
			echo "Error in creating WAF"
		fi
  else
    echo "Error in creating WAF"
    echo $stackCreation
  fi
fi
