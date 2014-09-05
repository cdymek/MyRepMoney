#Check to see which type of server this is
#jcs = control server
#dataloader = data loader server
log "Configuring application of type " << node["server"]["type"]
server_dir = ""
app_package = ""
if node["server"]["type"] == ("DataLoader")
	server_dir = node["dataloader"]["app_dir"]
	app_package = node["dataloader"]["app-package"]
end
elsif node["server"]["type"] == ("JCS")
	server_dir = node["jcs"]["app_dir"]
	app_package = node["jcs"]["app-package"]
else
	log "Undefined server type provided"
end

if server_dir != ("")

	#Create the directory where the app will be installed
	directory server_dir do
  		owner 'root'
  		group 'root'
  		mode 0755
  		recursive true
  		action :create
	end
	log "Directory " << server_dir << " created"

	#Set up the script to download the java app & extract the contents
	app_setup_script = server_dir << "/app-setup-script"
	template app_setup_script do
		source "app-setup-script.erb"
		mode 0755
		variables(
			:web_content_dir => node["website"]["web_dir"],
			:s3_source_url => node["website"]["s3_url"],
			:web_source_filename => node["website"]["source_zip"]
		)
	end
	log "App Setup Script " << app_setup_script " execution complete"

	#Set up the configuration files (aws.credentials, log4j.xml, config.properties)
	aws_cred_file = server_dir << "/AwsCredentials.properties"
	template aws_cred_file do
		source "AwsCredentials.properties.erb"
		mode 0644
		variables(
			:aws_access_key => node["aws"]["access_key"],
			:aws_secret_key => node["aws"]["secret_key"]
		)
	end

	config_file = server_dir << "/config.properties"
	template config_file do
		source "config.properties.erb"
		mode 0644
		variables(
			:dataloader_localdir => node["dataloader"]["localdir"],
			:dataloader_workingdir => node["dataloader"]["workingdir"],
			:ataloader_threadcount => node["dataloader"]["threadcount"],
			:dataloader_sleeptime => node["dataloader"]["sleeptime"],
			:dataloader_monitor_dir => node["dataloader"]["monitor_dir"],
			:jcs_sleeptime => node["jcs"]["sleeptime"],
			:jcs_monitor_dir => node["jcs"]["monitor_dir"],
			:db_name => node["mysql"]["db_name"],
			:db_username => node["mysql"]["db_username"],
			:db_password => node["mysql"]["db_password"],
			:db_host => node["mysql"]["db_host"],
			:db_port => node["mysql"]["db_port"],
			:aws_sqs_queue => node["aws"]["sqs_name"],
			:aws_region => node["aws"]["region"],
			:aws_sqs_url => node["aws"]["sqs_url"]
		)
	end	

	log4j_file = server_dir << "/log4j2-test.xml"
	template config_file do
		source "log4j2-test.xml.erb"
		mode 0644
		variables(
			:java_log_dir => node["java"]["log_dir"]
		)
	end	
	log "Properties files created"

	#Configure the app to execute


end 
