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

if node["website"]["load_source_zip"] == ("Y")

	#Generate backup script
	log "Implementing web backup script"
	template "#{node["server"]["backup_scripts_web"]}" do
		source "website-backup-script.erb"
		mode 0755
		variables(
			:web_content_dir => node["website"]["web_dir"],
			:s3_source_url => node["website"]["s3_url"],
			:web_source_filename => node["website"]["source_zip"]
		)
	end

	#Execute backup script
	execute "#{node["server"]["backup_scripts_web"]}" do
	  user "root"
	  cwd node["server"]["backup_scripts"]
	  command node["server"]["backup_scripts_web"]
	end

	#Create cron job file
	#<%= @minute %> <%= @hour %> * * * <%= @script_name %>
	execute "#{ENV['HOME']}/crontab.source" do
		user "root"
		cwd "#{ENV['HOME']}"
		command "echo \"" << node["server"]["minute"] << " " << node["server"]["hour"] << " * * * " << node["server"]["backup_scripts_web"] << "\" >> crontab.source"
	end
	log "Backup web script created"
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
