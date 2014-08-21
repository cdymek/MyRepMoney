log "This is the default recipe for this cookbook."
log "This integrates the components of the prior recipes in to a single entity."

#Update the entire server
include_recipe 'core::do_upgrades'

#Install core software packages
include_recipe 'core::anacron'
include_recipe 'core::python'
include_recipe 'core::git'
include_recipe 'core::unzip'
#include_recipe 'core::apache2'
include_recipe 'core::mysql-server'
#include_recipe 'core::php'
include_recipe 'core::config_cloudwatch'
include_recipe 'core::install_cloudwatch'
include_recipe 'core::install_awscli'
s3cmd_setup  do
	access_key node["aws"]["access_key"]
	secret_key node["aws"]["secret_key"]
end

#Configure the server
#include_recipe 'MyRepMoney::configure_website'
include_recipe 'MyRepMoney::configure_database'
include_recipe 'MyRepMoney::configure_database_backup'
#include_recipe 'MyRepMoney::configure_website_backup'
#include_recipe 'MyRepMoney::update_route53'

log "Server configuration complete."