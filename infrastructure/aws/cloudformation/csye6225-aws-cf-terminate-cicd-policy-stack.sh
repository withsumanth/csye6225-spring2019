if [ -z "$1" ]; then
	echo "Please provide a stack name"
else
  stackname=$1
		terminateOutput=$(aws cloudformation delete-stack --stack-name $stackname)
		if [ $? -eq 0 ]; then
			echo "Deletion of policy stack In Progress"
	    aws cloudformation wait stack-delete-complete --stack-name $stackname
	    echo $terminateOutput
	    echo "Deletion of stack successful"
	  else
	  echo "Deletion failed"
	  echo $terminateOutput
	fi
fi
