#Default attributes
#default["apache"]["dir"]          			= "/etc/apache2"
#default["apache"]["listen_ports"] 			= [ "80","443" ]
#default["apache"]["httpd_log_file"]			= "/var/log/apache2/access.log"

default["website"]["s3_url"]      			= "s3://cdymekbackup/MyRepMoney"
#default["website"]["source_zip"]  			= "website-test.zip"
#default["website"]["load_source_zip"]		= "Y"
#default["website"]["web_dir"]     			= "/var/www/html"
#default["website"]["wp_config_file_path"]   = "/var/www/html/blog"
#default["website"]["wp_config_file"]    	= "/var/www/html/blog/wp-config.php"


default["mysql"]["db_name"] 	  			= "MyRepMoney"
default["mysql"]["db_username"]   			= "root"
default["mysql"]["dp_password"]   			= "Shakeem"
default["mysql"]["db_host"]       			= "127.0.0.1"
default["mysql"]["sql_file"]				= "MyRepMoney_db.sql"
default["mysql"]["load_sql_file"]			= "Y"

default["server"]["backup_scripts"] 		= "/usr/scripts"
default["server"]["backup_scripts_db"] 		= "/usr/scripts/database-backup-script"
d#efault["server"]["backup_scripts_web"]		= "/usr/scripts/website-backup-script"
default["server"]["minute"]					= "00"
default["server"]["hour"]					= "6"
#default["server"]["cron_dir"]				= "/etc/cron.d"
#default["server"]["cron_dir_db"]			= "/etc/cron.d/database-script"
#default["server"]["cron_dir_website"]		= "/etc/cron.d/website-script"
default["server"]["do_upgrades"]			= "Y"

default["aws"]["log_group_name"]			= '/var/log/aws/opsworks/opsworks-agent.log'
default["aws"]["region"]					= "us-east-1"
default["aws"]["access_key"]	  			= "AKIAI2QOTOYNP3KE7NOQ"
default["aws"]["secret_key"]    			= "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"
default["aws"]["web_ip_address"]			= "000.000.000.000/00"
default["aws"]["hosted_zone"]				= "Z2WJ34K0USS5CR"
#default["aws"]["domain_list"]				= "test.cdymek.com,foo.cdymek.com"