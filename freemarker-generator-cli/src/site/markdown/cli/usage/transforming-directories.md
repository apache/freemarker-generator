## Transforming Directories

FreeMarker CLI supports the transformation of directories

* Transform an input directory recursively into an output directory
* If a template has a ".ftl" extension this extension will be removed after processing
* Only a single directory is support
* Currently no inclusion / exclusion pattern for templates are supported

### Transform Template Directory To STDOUT

```
bin/freemarker-cli -t site/template/
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 80;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Transform Template Directory To Output Directory

```
bin/freemarker-cli -t site/template/ -o out; ls -l out
total 8
-rw-r--r--  1 sgoeschl  staff  128 May 30 20:02 application.properties
drwxr-xr-x  3 sgoeschl  staff   96 May 30 20:02 nginx
```

### Use Command Line Parameters

```
bin/freemarker-cli -t site/template/ -P NGINX_HOSTNAME=localhost
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 80;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment Variables

```
export NGINX_PORT=8080
bin/freemarker-cli -t site/template/ -m env:///
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 8080;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment File

```
echo "NGINX_PORT=8080" > nginx.env
bin/freemarker-cli -t site/template/ -m nginx.env 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 8080;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use JSON File

```
echo '{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}' > nginx.json
bin/freemarker-cli -t site/template/ -m nginx.json 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use YAML File

```
echo -e "- NGINX_PORT": "\"8443\"\n- NGINX_HOSTNAME": "localhost" > nginx.yaml
bin/freemarker-cli -t site/template/ -m nginx.yaml 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment Variable With JSON Payload

```
export NGINX_CONF='{"NGINX_PORT":"8443","NGINX_HOSTNAME":"somehost"}'
echo $NGINX_CONF
{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}
bin/freemarker-cli -t site/template/ -m env:///NGINX_CONF#mimetype=application/json
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf =============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```