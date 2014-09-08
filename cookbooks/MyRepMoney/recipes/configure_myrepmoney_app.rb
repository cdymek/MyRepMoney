#Check to see which type of server this is
#jcs = control server
#dataloader = data loader server
log "Configuring application of type #{node["myrepmoney"]["server_type"]}"
server_dir = ""
if node["myrepmoney"]["server_type"] == ("dataloader")
	server_dir = node["dataloader"]["app_dir"],
	monitor_dir = node["dataloader"]["monitor_dir"]
elsif node["myrepmoney"]["server_type"] == ("jcs")
	server_dir = node["jcs"]["app_dir"],
	monitor_dir = node["jcs"]["monitor_dir"]
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
	log "Directory #{server_dir} created"


	#Set up the configuration files (aws.credentials, log4j.xml, config.properties)
	template "#{server_dir}/AwsCredentials.properties" do
		source "AwsCredentials.properties.erb"
		mode 0644
		variables(
			:aws_access_key => node["aws"]["access_key"],
			:aws_secret_key => node["aws"]["secret_key"]
		)
	end

	template "#{server_dir}/config.properties" do
		source "config.properties.erb"
		mode 0644
		variables(
			:server_type => node["myrepmoney"]["server_type"],
			:dataloader_localdir => node["dataloader"]["localdir"],
			:dataloader_workingdir => node["dataloader"]["workingdir"],
			:dataloader_threadcount => node["dataloader"]["threadcount"],
			:dataloader_sleeptime => node["dataloader"]["sleeptime"],
			:dataloader_monitor_dir => node["dataloader"]["monitor_dir"],
			:jcs_sleeptime => node["jcs"]["sleeptime"],
			:jcs_monitor_dir => node["jcs"]["monitor_dir"],
			:db_name => node["mysql"]["db_name"],
			:db_username => node["mysql"]["db_username"],
			:db_password => node["mysql"]["db_password"],
			:db_host => node["mysql"]["db_host"],
			:db_port => node["mysql"]["db_port"],
			:aws_sqs_queue => node["aws"]["sqs_queue"],
			:aws_region => node["aws"]["region"],
			:aws_sqs_url => node["aws"]["sqs_url"]
		)
	end	

	template "#{server_dir}/log4j2-test.xml" do
		source "log4j2-test.xml.erb"
		mode 0644
		variables(
			:java_log_dir => node["java"]["log_dir"],
			:server_type => node["server"]["type"]
		)
	end	
	log "Properties files created"

	#Set up the script to download the java app & extract the contents
	template "#{server_dir}/app-setup-script" do
		source "app-setup-script.erb"
		mode 0755
		variables(
			:server_dir => server_dir,
			:s3_source_url => node["website"]["s3_url"],
			:app_package => node["myrepmoney"]["app_package"],
			:jar_file => node["myrepmoney"]["jar_file"]
		)
	end

	#Execute setup script
	execute "app-setup-script" do
	  user "root"
	  cwd  "#{server_dir}"
	  command "#{server_dir}/app-setup-script"
	end
	log "App Setup Script #{server_dir}/app-setup-script execution complete"

	#Configure the app to execute
	template "#{server_dir}/java-app.sh" do
		source "java-app.sh.erb"
		mode 0755
		variables(
			:server_dir => server_dir,
			:monitor_dir => monitor_dir,
			:jar_file => node["myrepmoney"]["jar_file"]
		)
	end

end 
