#!/bin/bash
#Add files to git
cd ~/Workspaces/MyRepMoney
git commit -m "Updated files added"

# Upload Chef scripts
cd ~/Workspaces/MyRepMoney/cookbooks/MyRepMoney
berks package
mv cookbooks-*.tar.gz ~/Workspaces/MyRepMoney/chef.tar.gz
cd ~/Workspaces/MyRepMoney/
s3cmd put chef.tar.gz s3://cdymekbackup/MyRepMoney/chef.tar.gz

#Upload cloud_formation scripts
cd ~/Workspaces/MyRepMoney/cloudformation
s3cmd put vpc_website_setup_ec2_only.json s3://cdymekbackup/MyRepMoney/vpc_website_setup_ec2_only.json
s3cmd put empty_vpc_setup.json s3://cdymekbackup/MyRepMoney/Mempty_vpc_setup.json

#Run the VPC Setup scripts
#mkdir tmp
#aws cloudformation create-stack --stack-name VPC-Stack --template-url https://s3.amazonaws.com/cdymekbackup/cloudformation_scripts/VPC/MyRepMoney_empty_vpc_setup.json --capabilities CAPABILITY_IAM --parameters file://~/Workspaces/MyRepMoney/cloudformation/vpc-prod-params.json > ~/Workspaces/MyRepMoney/cloudformation/tmp/stackid.json

#NEED WAIT COMMAND
#aws cloudformation describe-stacks --stack-name VPC-Stack | grep '"StackStatus":'

#Run the website setup script
#https://s3.amazonaws.com/cdymekbackup/cloudformation_scripts/VPC/MyRepMoney_vpc_website_setup.json