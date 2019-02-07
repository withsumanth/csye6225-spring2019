#!/bin/sh
if [ -z "$1" ]; then
	echo " Please provide stack name "
	exit 1
else
  VPC_NAME="$1"-csye6225-vpc
  echo $VPC_NAME

  VPC_ID=$(aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${VPC_NAME}" --query 'Vpcs[*].{id:VpcId}' --output text)

  IG_GATEWAY=$(aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=${VPC_ID}" --output text  | grep  igw| awk '{print $2}')

  RT_ID=$(aws ec2 describe-route-tables --query "RouteTables[?VpcId=='$VPC_ID'].RouteTableId" --output text)
  echo $RT_ID

  echo RT_ID IS $RT_ID

  echo VPC ID IS $VPC_ID

  echo Internate gateway Id is $IG_GATEWAY

	if [ -z "$VPC_ID" ]; then
		echo "VPC does not exist"
	else
		for i in $(aws ec2 describe-subnets --filters Name=vpc-id,Values=$VPC_ID --output text --query "Subnets[].SubnetId")
	  do
	  	echo "Deleting Subnet: $i"
	  	aws ec2 delete-subnet --subnet-id $i
	  	echo "Successfully deleted Subnet: $i"
	  done
	  echo "SUCCESSFUL DELETED ALL SUBNETS"

	  # IDENTIFYING THE CUSTOM ROUTE TABLE
	  CUSTOM_ROUTE_TABLE_ID=$(aws ec2 describe-route-tables --query "RouteTables[?VpcId=='$VPC_ID']|[?Associations[?Main!=true]].RouteTableId" --output text)

	  echo "id = $CUSTOM_ROUTE_TABLE_ID"

	  #DELETING THE ROUTE TABLE
	  for i in $RT_ID
	  do
	  	echo "BEGINNING TO DELETE THE ROUTE TABLE WITH ID: $CUSTOM_ROUTE_TABLE_ID"
	  	if [ $i != $CUSTOM_ROUTE_TABLE_ID ]; then
	  		aws ec2 delete-route-table --route-table-id $i
	  		echo $i
	  	fi
	  	echo "DELETED THE $CUSTOM_ROUTE_TABLE_ID"
	  done
	  echo "SUCCESSFUL DELETION OF ROUTE TABLE"

	  echo detaching gateway
	  aws ec2 detach-internet-gateway --internet-gateway-id $IG_GATEWAY --vpc-id=$VPC_ID
	  echo gateway detached ...........

	  echo deleting gateway
	  aws ec2 delete-internet-gateway --internet-gateway-id $IG_GATEWAY
	  echo gateway deleted...........

	  echo deleting vpc
	  aws ec2 delete-vpc --vpc-id $VPC_ID
	  echo VPC deleted ...........
	fi
fi
