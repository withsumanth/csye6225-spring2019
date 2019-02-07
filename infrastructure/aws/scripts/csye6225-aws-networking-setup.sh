if [ -z "$1" ]; then
	echo "Please provide a stack name"
elif [ -z "$2" ]; then
	echo "Please provide Subnet 1 Availability Zone"
elif [ -z "$3" ]; then
	echo "Please provide Subnet 2 Availability Zone"
elif [ -z "$4" ]; then
	echo "Please provide Subnet 3 Availability Zone"
elif [ -z "$5" ]; then
	echo "Please provide CIDR block to create Subnet 1"
elif [ -z "$6" ]; then
	echo "Please provide CIDR block to create Subnet 2"
elif [ -z "$7" ]; then
	echo "Please provide CIDR block to create Subnet 3"
else
  #VPC
  region="us-east-1"
  vpcName="$1-csye6225-vpc"
  igName="$1-csye6225-InternetGateway"
  vpc_cidr="10.0.0.0/16"
	cidrIp="0.0.0.0/0"

  #Route table
  routeTableName="$1-csye6225-rt"

  #Subnets
  publicSubnet1Cidr=$5
  publicSubnet1Reg=$2
  publicSubnet1Name="publicSubnet1"
  publicSubnet2Cidr=$6
  publicSubnet2Reg=$3
  publicSubnet2Name="publicSubnet2"
  publicSubnet3Cidr=$7
  publicSubnet3Reg=$4
  publicSubnet3Name="publicSubnet3"

	#Security Group NAME
	securityGp="$1-web-security-group"

  #Creation of VPC
  VPC_ID=$(aws ec2 create-vpc \
    --cidr-block $vpc_cidr \
    --query 'Vpc.{VpcId:VpcId}' \
    --output text \
    --region $region 2>&1)
  VPC_CREATE_STATUS=$?

  #Check Status of vpc creation
  if [ $VPC_CREATE_STATUS -eq 0 ]; then
    echo " VPC ID '$VPC_ID' CREATED in '$region' region."
  else
  	echo "Error:VPC not created!!"
    echo " $VPC_ID "
  	exit $VPC_CREATE_STATUS
  fi

  #To provide a name tag to the VPC
  VPC_TAG=$(aws ec2 create-tags \
    --resources $VPC_ID \
    --tags "Key=Name,Value=$vpcName" \
    --region $region 2>&1)
  VPC_TAG_STATUS=$?
  if [ $VPC_TAG_STATUS -eq 0 ]; then
    echo "  VPC ID '$VPC_ID' NAMED as '$vpcName'."
  else
      echo "Error:VPC name not added!!"
      echo " $VPC_TAG "
      exit $VPC_TAG_STATUS
  fi

	# Create First  Subnet
  echo "CREATING THE 1 OUT OF 3 SUBNETS "
  SUBNET_FIRST_ID=$(aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block $publicSubnet1Cidr \
    --availability-zone $publicSubnet1Reg \
    --query 'Subnet.{SubnetId:SubnetId}' \
    --output text \
    --region $region)
	if [ $? -eq 0 ]; then
		echo "  Subnet ID '$SUBNET_FIRST_ID' CREATED in '$publicSubnet1Reg'" \
	    "Availability Zone."
	else
		echo "Creation of Subnet 1 failed"
		aws ec2 delete-vpc --vpc-id $VPC_ID
	  echo VPC deleted ...........
		exit 1
	fi

  # Add Name tag to Public Subnet
  aws ec2 create-tags \
    --resources $SUBNET_FIRST_ID \
    --tags "Key=Name,Value=$publicSubnet1Name" \
    --region $region
  echo "  Subnet ID '$SUBNET_FIRST_ID' NAMED as" \
    "'$publicSubnet1Name'."

  #create SECOND  Subnet
  echo "CREATING THE 2 OUT OF 3 SUBNETS "
  SUBNET_SECOND_ID=$(aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block $publicSubnet2Cidr \
    --availability-zone $publicSubnet2Reg \
    --query 'Subnet.{SubnetId:SubnetId}' \
    --output text \
    --region $region)
		if [ $? -eq 0 ]; then
			echo "  Subnet ID '$SUBNET_SECOND_ID' CREATED in '$publicSubnet2Reg'" \
		    "Availability Zone."
		else
			echo "Creation of Subnet 2 failed"
			for i in $(aws ec2 describe-subnets --filters Name=vpc-id,Values=$VPC_ID --output text --query "Subnets[].SubnetId")
		  do
		  	echo "Deleting Subnet: $i"
		  	aws ec2 delete-subnet --subnet-id $i
		  	echo "Successfully deleted Subnet: $i"
		  done
			aws ec2 delete-vpc --vpc-id $VPC_ID
			echo VPC deleted ...........
			exit 1
		fi

  # Add Name tag to the second Subnet
  aws ec2 create-tags \
    --resources $SUBNET_SECOND_ID \
    --tags "Key=Name,Value=$publicSubnet2Name" \
    --region $region
  echo "  Subnet ID '$SUBNET_SECOND_ID' NAMED as" \
    "'$publicSubnet2Name'."

  # Create third  Subnet
  echo "CREATING THE 3 OUT OF 3 SUBNETS "
  SUBNET_THIRD_ID=$(aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block $publicSubnet3Cidr \
    --availability-zone $publicSubnet3Reg \
    --query 'Subnet.{SubnetId:SubnetId}' \
    --output text \
    --region $region)
		if [ $? -eq 0 ]; then
			echo "  Subnet ID '$SUBNET_THIRD_ID' CREATED in '$publicSubnet3Reg'" \
		    "Availability Zone."
		else
			echo "Creation of Subnet 3 failed"
			for i in $(aws ec2 describe-subnets --filters Name=vpc-id,Values=$VPC_ID --output text --query "Subnets[].SubnetId")
		  do
		  	echo "Deleting Subnet: $i"
		  	aws ec2 delete-subnet --subnet-id $i
		  	echo "Successfully deleted Subnet: $i"
		  done
			aws ec2 delete-vpc --vpc-id $VPC_ID
			echo VPC deleted ...........
			exit 1
		fi

  # Add Name tag to Public Subnet
  aws ec2 create-tags \
    --resources $SUBNET_THIRD_ID \
    --tags "Key=Name,Value=$publicSubnet3Name" \
    --region $region
  echo "  Subnet ID '$SUBNET_THIRD_ID' NAMED as" \
    "'$publicSubnet3Name'."

  #Creating Internet Gateway
  echo "Creating Internet Gateway..."
  IGW_ID=$(aws ec2 create-internet-gateway \
    --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
    --output text \
    --region $region 2>&1)
  IGW_CREATE_STATUS=$?
  if [ $IGW_CREATE_STATUS -eq 0 ]; then
    echo "  Internet Gateway ID '$IGW_ID' CREATED."
  else
      echo "Error:Gateway not created"
      echo " $IGW_ID "
      exit $IGW_CREATE_STATUS
  fi

  #To provide a Name Tag to the internet gateway
  IGW_NAME_TAG=$(aws ec2 create-tags \
    --resources $IGW_ID \
    --tags "Key=Name,Value=$igName" 2>&1)
  IGW_NAME_TAG_STATUS=$?
  if [ $IGW_NAME_TAG_STATUS -eq 0 ]; then
    echo "  Internet gateway ID '$IGW_ID' NAMED as '$igName'."
  else
      echo "Error: INTERNET GATEWAY WAS NOT CREATED"
      echo " $IGW_NAME_TAG "
      exit $IGW_NAME_TAG_STATUS
  fi

  # Attach Internet gateway to your VPC
  IGW_ATTACH=$(aws ec2 attach-internet-gateway \
    --vpc-id $VPC_ID \
    --internet-gateway-id $IGW_ID \
    --region $region 2>&1)
  IGW_ATTACH_STATUS=$?
  if [ $IGW_ATTACH_STATUS -eq 0 ]; then
    echo "  Internet Gateway ID '$IGW_ID' ATTACHED to VPC ID '$VPC_ID'."
  else
      echo "Error:Gateway not attached to VPC: $?"
      echo " $IGW_ATTACH "
      exit $IGW_ATTACH_STATUS
  fi

  # Create Route Table
  echo "Creating Route Table..."
  ROUTE_TABLE_ID=$(aws ec2 create-route-table \
    --vpc-id $VPC_ID \
    --query 'RouteTable.{RouteTableId:RouteTableId}' \
    --output text \
    --region $region 2>&1)
    ROUTE_TABLE_CREATE_STATUS=$?
  if [ $ROUTE_TABLE_CREATE_STATUS -eq 0 ]; then
    echo "  Route Table ID '$ROUTE_TABLE_ID' CREATED."
  else
      echo "Error:Route table not created!!"
      echo " $ROUTE_TABLE_ID "
      exit $ROUTE_TABLE_CREATE_STATUS
  fi

  #ADDING A NAME TAG TO THE ROUTE TABLE
  ROUTE_TABLE_NAME_TAG=$(aws ec2 create-tags \
    --resources $ROUTE_TABLE_ID \
    --tags "Key=Name,Value=$routeTableName" 2>&1)
  ROUTE_TABLE_NAME_TAG_STATUS=$?
  if [ $ROUTE_TABLE_NAME_TAG_STATUS -eq 0 ]; then
    echo " '$routeTableName' NAME TAG WAS ADDED SUCCESSFULLY"
  else
      echo "Error:ROUTE_TABLE WAS NOT ADDED"
      echo " $ROUTE_TABLE_NAME_TAG "
      exit $ROUTE_TABLE_NAME_TAG_STATUS
  fi

  #ADDING A PUBLIC ROUTE TO THE INTERNET GATEWAY
  ROUTE=$(aws ec2 create-route \
    --route-table-id $ROUTE_TABLE_ID \
    --destination-cidr-block 0.0.0.0/0 \
    --gateway-id $IGW_ID \
    --region $region 2>&1)
  ROUTE_STATUS=$?
  if [ $ROUTE_STATUS -eq 0 ]; then
    echo "  ADDED ACCESS IP TO THE INTERNET GATEWAY SUCCESSFULLY"
  else
      echo "Error CREATING A ROUTE TO THE INTERNET GATEWAY"
      echo " $ROUTE "
      exit $ROUTE_STATUS
  fi

		RT_ASS1=$(aws ec2 associate-route-table --subnet-id $SUBNET_FIRST_ID --route-table-id $ROUTE_TABLE_ID)
		RT_ASS2=$(aws ec2 associate-route-table --subnet-id $SUBNET_SECOND_ID --route-table-id $ROUTE_TABLE_ID)
		RT_ASS3=$(aws ec2 associate-route-table --subnet-id $SUBNET_THIRD_ID --route-table-id $ROUTE_TABLE_ID)

		#Modification of default security group
		SG_ID=$(aws ec2 describe-security-groups --filters Name=vpc-id,Values=$VPC_ID --output text --query "SecurityGroups[].GroupId")
		echo "$SG_ID was created"

		#defining the rules
		SG_RULES=$(aws ec2 authorize-security-group-ingress --group-id $SG_ID --ip-permissions IpProtocol=tcp,FromPort=80,ToPort=80,IpRanges=[{CidrIp=$cidrIp}] IpProtocol=tcp,FromPort=22,ToPort=22,IpRanges=[{CidrIp=$cidrIp}])

		SG_REVOKE_RULE=$(aws ec2 revoke-security-group-ingress --group-id $SG_ID --protocol all --source-group $SG_ID)

		SG_DESCRIPTION=$(aws ec2 describe-security-groups --group-ids $SG_ID --output table)
		echo "Security group were added"

		#To check and report the status of a VPC creation
		SG_STATUS=$?
		if [ $SG_STATUS -eq 0 ]; then
		  echo "A SG with SG ID '$SG_ID' IS CREATED in '$region' region."
		else
			echo "Error:SG not created"
		  echo " $SG_ID "
			exit $SG_STATUS
		fi
		#ADDING A NAME TAG TO THE SECURITY GROUP
		SG_NAME_TAG=$(aws ec2 create-tags \
		  --resources $SG_ID \
		  --tags "Key=Name,Value=$securityGp" 2>&1)
fi
