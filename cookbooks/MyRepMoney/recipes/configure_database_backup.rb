log "Implementing backup scripts"
directory node["server"]["backup_scripts"] do
  owner 'root'
  group 'root'
  mode 0755
  recursive true
  action :create
end

#Archive the current root crontab
script "#{ENV['HOME']}/crontab.source" do
	interpreter "bash"
	user "root"
	cwd "#{ENV['HOME']}"
	code <<-EOH
	crontab -l > crontab.source
	if [ "$?" -ne "0" ]; then
		touch crontab.source
	fi
	EOH
end

if node["mysql"]["load_sql_file"] == ("Y")

	log "Implementing database backup script"

	#Generate backup script
	template "#{node["server"]["backup_scripts_db"]}" do
		source "database-backup-script.erb"
		mode 0755
		variables(
			:web_content_dir => node["website"]["web_dir"],
			:s3_source_url => node["website"]["s3_url"],
			:web_source_filename => node["website"]["source_zip"],
			:db_name_var => node["mysql"]["db_name"],
			:db_username_var => node["mysql"]["db_username"],
			:db_password_var => node["mysql"]["db_password"],
			:db_host_var => node["mysql"]["db_host"],
			:mysql_source_filename => node["mysql"]["sql_file"]		
		)
	end

	#Execute backup script
	execute "#{node["server"]["backup_scripts_db"]}" do
	  user "root"
	  cwd node["server"]["backup_scripts"]
	  command node["server"]["backup_scripts_db"]
	end
	
	#Create cron job file
	#<%= @minute %> <%= @hour %> * * * <%= @script_name %>
	execute "#{ENV['HOME']}/crontab.source" do
		user "root"
		cwd "#{ENV['HOME']}"
		command "echo \"" << node["server"]["minute"] << " " << node["server"]["hour"] << " * * * " << node["server"]["backup_scripts_db"] << "\" >> crontab.source"
	end

	log "Database backup script implemented"
end

#Replace the current root crontab
execute "#{ENV['HOME']}/crontab.source" do
	user "root"
	cwd "#{ENV['HOME']}"
	command "crontab crontab.source"
end

#Restart the crontab
execute "/etc/init.d/cron restart" do
  user "root"
  cwd "/etc/init.d"
  command "/etc/init.d/cron restart"
end
log "Cron restarted with backup jobs configured."
