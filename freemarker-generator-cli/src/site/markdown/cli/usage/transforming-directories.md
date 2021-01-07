## Transforming Directories

`Apache FreeMarker Generator` supports the transformation of directories

* Transform an input directory recursively into an output directory
* If a template has a ".ftl" extension this extension will be removed after processing
* Only a single directory is supported (for now)
* Currently no inclusion / exclusion patterns for templates are supported (for now)

The following sample files are used

* template/application.properties
* template/nginx/nginx.conf.ftl

```
tree examples/data/template/
examples/data/template/
|-- application.properties
`-- nginx
    `-- nginx.conf.ftl

# == application.properties ==================================================
server.name=${NGINX_HOSTNAME!"127.0.0.1"}
server.logs=${NGINX_LOGS!"/var/log/nginx"}
```

```
# == nginx-conf ==============================================================
server {
  listen ${NGINX_PORT!"80"};
  server_name ${NGINX_HOSTNAME!"127.0.0.1"};

  root ${NGINX_WEBROOT!"/usr/share/nginx/www"};
  index index.htm;
```

### Transform Template Directory To STDOUT

If no output directory is provided all output is written to `stdout`

```
freemarker-generator -t examples/data/template/
# == application.properties ==================================================
server.name=127.0.0.1
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 80;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Transform Template Directory To Output Directory

The transformed templates are written to an `out` directory

* `nginx.conf.ftl` was changed to `nginx.conf" during the transformation

```
freemarker-generator -t examples/data/template/ -o out; tree out
out
|-- application.properties
`-- nginx
    `-- nginx.conf

1 directory, 2 files
```

### Use Command Line Parameters

A user-supplied parameter `NGINX_HOSTNAME` is used to render the templates

```
freemarker-generator -t examples/data/template/ -P NGINX_HOSTNAME=localhost
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 80;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment Variables

All environment variables can be copied to the top-level data model by providing `-m env:///`

* `-m` or `-data-model` creates a data model
* `env:///` is an URI referencing all environment variables

```
export NGINX_PORT=8080
freemarker-generator -t examples/data/template/ -m env:///
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8080;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment File

Instead of environment variables an environment file (aka properties file) can be used

```
echo "NGINX_PORT=8080" > nginx.env
freemarker-generator -t examples/data/template/ -m nginx.env 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8080;
  server_name 127.0.0.1;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use JSON File

Another option is passing the information as JSON file

```
echo '{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}' > nginx.json
freemarker-generator -t examples/data/template/ -m nginx.json 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use YAML File

Yet another option is using a YAML file

```
echo -e "- NGINX_PORT": "\"8443\"\n- NGINX_HOSTNAME": "localhost" > nginx.yaml
freemarker-generator -t examples/data/template/ -m nginx.yaml 
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Use Environment Variable With JSON Payload

In the cloud it is common to pass JSON configuration as environment variable

* `env:///NGINX_CONF` selects the `NGINX_CONF` environment variable
* `#mimeType=application/json` defines that JSON content is parsed

```
export NGINX_CONF='{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}'
freemarker-generator -t examples/data/template/ -m env:///NGINX_CONF#mimeType=application/json
# == application.properties ==================================================
server.name=localhost
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8443;
  server_name localhost;

  root /usr/share/nginx/www;
  index index.htm;
}
```

### Overriding Values From The Command Line

For testing purpose it is useful to override certain settings

```
export NGINX_CONF='{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}'
freemarker-generator -t examples/data/template/ -PNGINX_HOSTNAME=www.mydomain.com -m env:///NGINX_CONF#mimeType=application/json
# == application.properties ==================================================
server.name=www.mydomain.com
server.logs=/var/log/nginx
# == nginx-conf ==============================================================
server {
  listen 8443;
  server_name www.mydomain.com;

  root /usr/share/nginx/www;
  index index.htm;
}
```

Please note that this only works for "top-level" variables, i.e. mimicking environment variables or property files. 