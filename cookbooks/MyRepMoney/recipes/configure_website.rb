#Attributes required:
#s3 bucket to download website from
#target folder to install website to

if node["website"]["load_source_zip"] == ("Y")
	log "Loading website from archive file..."
	template "#{ENV['HOME']}/website-setup-script" do
		source "website-setup-script.erb"
		mode 0755
		variables(
			:web_content_dir => node["website"]["web_dir"],
			:s3_source_url => node["website"]["s3_url"],
			:web_source_filename => node["website"]["source_zip"]
		)
	end

	execute "website-setup-script" do
	  user "root"
	  cwd "#{ENV['HOME']}"
	  command "./website-setup-script"
	end

	template node["website"]["wp_config_file"] do
		source "wp-config.php.erb"
		mode 0644
		variables(
			:db_name_var => node["mysql"]["db_name"],
			:db_username_var => node["mysql"]["db_username"],
			:db_password_var => node["mysql"]["db_password"],
			:db_host_var => node["mysql"]["db_host"]
		)
		only_if {::File.directory?(node["website"]["wp_config_file_path"])}
	end

	log "Website archive file downloaded and loaded."
end


 log "Restarting apache..."
 service 'apache2' do
    action :restart
 end
 log "Apache restarted."
 