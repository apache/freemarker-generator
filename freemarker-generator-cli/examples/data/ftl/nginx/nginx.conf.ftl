<#assign env = PropertiesTool.parse(dataSources.get(0))>

server {
  listen 80;
  server_name ${env.NGINX_HOSTNAME};

  root ${env.NGINX_WEBROOT};
  index index.htm;
}
