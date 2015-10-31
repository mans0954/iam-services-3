log_location STDOUT
chef_server_url 'https://chef.openiam.com/organizations/openiam'
# validation_client_name "docker"
node_name 'docker'
ssl_verify_mode :verify_none
log_location '/opt/openiam/webapps/chef.client.log'
log_level :debug
