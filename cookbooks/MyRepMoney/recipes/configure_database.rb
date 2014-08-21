#Attributes required:
#s3 bucket to download website from
#target folder to install website to

if node["mysql"]["load_sql_file"] == ("Y")
	log "Loading database from database backup file..."
	domain_name = node["aws"]["domain_list"].split(",").first
	template "#{ENV['HOME']}/database-setup-script" do
		source "database-setup-script.erb"
		mode 0755
		variables(
			:s3_source_url => node["website"]["s3_url"],
			:db_name_var => node["mysql"]["db_name"],
			:db_username_var => node["mysql"]["db_username"],
			:db_password_var => node["mysql"]["db_password"],
			:db_host_var => node["mysql"]["db_host"],
			:mysql_source_filename => node["mysql"]["sql_file"],
			:domain_name => domain_name
		)
	end

	execute "database-setup-script" do
	  user "root"
	  cwd "#{ENV['HOME']}"
	  command "./database-setup-script"
	end
	log "Database backup file loaded."
end

 