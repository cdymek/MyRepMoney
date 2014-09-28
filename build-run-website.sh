#!/bin/bash
# Copy the Java source code to the Git Repository
cd ~/Workspaces/MyEclipse\ Professional\ 2014/MyRepMoney_DataLoader/
rm -r ~/Workspaces/MyRepMoney/java
mkdir ~/Workspaces/MyRepMoney/java
cp -R src ~/Workspaces/MyRepMoney/java/src
mkdir ~/Workspaces/MyRepMoney/java/export
cp export/ant.xml ~/Workspaces/MyRepMoney/java/export
cp -R lib ~/Workspaces/MyRepMoney/java/lib

#Add files to git
cd ~/Workspaces/MyRepMoney
git commit -m "Updated files added"

# Upload Chef scripts
cd ~/Workspaces/MyRepMoney/cookbooks/MyRepMoney
berks package
mv cookbooks-*.tar.gz ~/Workspaces/MyRepMoney/chef.tar.gz
cd ~/Workspaces/MyRepMoney/
s3cmd put chef.tar.gz s3://cdymekbackup/MyRepMoney/chef.tar.gz

# Upload Java app
cd ~/Workspaces/MyEclipse\ Professional\ 2014/MyRepMoney_DataLoader/export
rm myrepmoney-app.zip
zip -ro myrepmoney-app.zip *
s3cmd put myrepmoney-app.zip s3://cdymekbackup/MyRepMoney/myrepmoney-app.zip

#Upload cloud_formation scripts
cd ~/Workspaces/MyRepMoney/cloudformation
s3cmd put vpc-1-public-2-private-subnets.json s3://cdymekbackup/MyRepMoney/vpc-1-public-2-private-subnets.json
s3cmd put vpc-2-public-subnets.json s3://cdymekbackup/MyRepMoney/vpc-2-public-subnets.json
s3cmd put MyRepMoney-app-stack-requires-vpc.json s3://cdymekbackup/MyRepMoney/MyRepMoney-app-stack-requires-vpc.json
s3cmd put MyRepMoney-full-stack-prvt-vpc-test.json s3://cdymekbackup/MyRepMoney/MyRepMoney-full-stack-prvt-vpc-test.json
s3cmd put MyRepMoney-full-stack-pub-vpc-test.json s3://cdymekbackup/MyRepMoney/MyRepMoney-full-stack-pub-vpc-test.json

#Run the VPC Setup scripts
#mkdir tmp
#aws cloudformation create-stack --stack-name VPC-Stack --template-url https://s3.amazonaws.com/cdymekbackup/cloudformation_scripts/VPC/MyRepMoney_empty_vpc_setup.json --capabilities CAPABILITY_IAM --parameters file://~/Workspaces/MyRepMoney/cloudformation/vpc-prod-params.json > ~/Workspaces/MyRepMoney/cloudformation/tmp/stackid.json

#NEED WAIT COMMAND
#aws cloudformation describe-stacks --stack-name VPC-Stack | grep '"StackStatus":'

#Run the website setup script
#https://s3.amazonaws.com/cdymekbackup/cloudformation_scripts/VPC/MyRepMoney_vpc_website_setup.json