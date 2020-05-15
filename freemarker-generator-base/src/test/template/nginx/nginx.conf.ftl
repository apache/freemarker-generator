server {
  listen 80;
  server_name ${NGINX_HOSTNAME};

  root ${NGINX_WEBROOT};
  index index.htm;
}
