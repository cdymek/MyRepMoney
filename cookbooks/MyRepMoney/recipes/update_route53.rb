#Script to update Route53 with a list of DNS entries
#Uses:
#update-route53.erb - shell script template to execute the update
#route53_update.json-erb - json template that is used as input to the aws cli route53 command
#Attributes required:
#node["aws"]["hosted_zone"] - provides the hosted zone that will contain the new or modified ip address entries
#node["aws"]["domain_list"] - the comma separated list of domain names


#First we need to get the public ip address using ec2 metadata
log "Beginning process to update DNS for " << node["aws"]["domain_list"]

public_ipv4_cmd=Mixlib::ShellOut.new("ec2metadata | grep 'public-ipv4:' | cut -d ' ' -f 2")
public_ipv4_cmd.run_command
puts public_ipv4_cmd.stdout
public_ipv4 = public_ipv4_cmd.stdout
puts "error messages" + public_ipv4_cmd.stderr
public_ipv4_cmd.error!
public_ipv4 = public_ipv4.strip

#public_ipv4 = "192.168.0.152"
log "Retrieved public ip " << public_ipv4



domain_list = node["aws"]["domain_list"].split(",")

#then for each domain name provided, we need to do the following
domain_list.each do |domain|
	#Populate the json template
	template "#{ENV['HOME']}/route_53_update.json" do
		user "root"
		source "route53_update.json.erb"
		mode 0644
		variables(
			:domain_name => domain,
			:public_ipv4 => public_ipv4
		)
	end

	#Execute the update command
	execute "update-route53" do
		user "root"
		cwd "#{ENV['HOME']}"
		command "aws route53 change-resource-record-sets --hosted-zone-id " << node["aws"]["hosted_zone"] << " --change-batch file://#{ENV['HOME']}/route_53_update.json"
	end
	log "Executed commaind: aws route53 change-resource-record-sets --hosted-zone-id " << node["aws"]["hosted_zone"] << " --change-batch file://#{ENV['HOME']}/route_53_update.json"
end
 log "DNS updated."
 