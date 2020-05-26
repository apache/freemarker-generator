# == nginx-conf =============================================================
server {
  listen ${NGINX_PORT!"80"};
  server_name ${NGINX_HOSTNAME!"localhost"};

  root ${NGINX_WEBROOT!"/usr/share/nginx/www"};
  index index.htm;
}
