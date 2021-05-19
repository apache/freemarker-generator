## Parsing With Grok

### Unleashing The Power Of Grok

Think of `Grok` as modular regular expressions with a pre-defined functionality to parse access logs or any other data where you can't comprehend the regular expression any longer, one very simple example is `QUOTEDSTRING`

```
QUOTEDSTRING (?>(?<!\\)(?>"(?>\\.|[^\\"]+)+"|""|(?>'(?>\\.|[^\\']+)+')|''|(?>`(?>\\.|[^\\`]+)+`)|``))
```

And with `Grok` the `QUOTEDSTRING` is just a building block for an even more complex regular expression such as `COMBINEDAPACHELOG`

> bin/freemarker-generator -t examples/templates/accesslog/combined-access.ftl examples/data/accesslog/combined-access.log

which gives you the following output

```
TIMESTAMP;VERB;REQUEST;HTTPVERSION
19/Jun/2005:06:44:17 +0200;GET;/wximages/wxwidgets02-small.png;1.1
19/Jun/2005:06:46:05 +0200;GET;/wximages/wxwidgets02-small.png;1.1
19/Jun/2005:06:47:37 +0200;GET;/wximages/wxwidgets02-small.png;1.1
19/Jun/2005:06:48:40 +0200;GET;/wiki.pl?WxWidgets_Bounties;1.1
19/Jun/2005:06:50:49 +0200;GET;/wiki.pl?WxWidgets_Compared_To_Other_Toolkits;1.1
19/Jun/2005:06:50:49 +0200;GET;/wxwiki.css;1.1
19/Jun/2005:06:50:49 +0200;GET;/wximages/wxwidgets02-small.png;1.1
19/Jun/2005:06:50:50 +0200;GET;/favicon.ico;1.1
19/Jun/2005:06:52:36 +0200;GET;/wximages/wxwidgets02-small.png;1.1
19/Jun/2005:06:53:14 +0200;GET;/;1.0
```

using the following FreeMarker template

```text
<#ftl output_format="plainText" strip_whitespace=true>
<#assign grok = tools.grok.compile("%{COMBINEDAPACHELOG}")>
<#assign dataSource = dataSources?values[0]>
<#assign lines = dataSource.getLineIterator()>

<#compress>
    TIMESTAMP;VERB;REQUEST;HTTPVERSION
    <#list lines as line>
        <#assign parts = grok.match(line)>
        <#assign timestamp = parts["timestamp"]>
        <#assign verb = parts["verb"]>
        <#assign request = parts["request"]>
        <#assign httpversion = parts["httpversion"]>
        ${timestamp};${verb};${request};${httpversion}
    </#list>
</#compress>
```

While this looks small and tidy there are some nifty features

* `tools.grok.compile("%{COMBINEDAPACHELOG}")` builds the `Grok` instance to parse access logs in `Combined Format`
* The data source is streamed line by line and not loaded into memory in one piece
* This also works for using `stdin` so are able to parse GB of access log or other files

### Parse Server Log File And Generate CSV

A more practical example consists of parsing dozens of server logs files to determine response time of message processing, e.g.

```
2021-05-18 20:00:32,140 INFO  [aa.bb.ccc] (Thread-99) message response handled in: 62 ms; message counter: 2048; total message counter: 7094
```

In technical terms the FTL 

* Defines custom Grok pattern definitions
* Compiles the Grok expression `MY_SERVERLOG`   
* Reads through all data sources passed on the command line
* Use Grok to match lines and extract the timestamp and response times
* Skip all execution times less than 5 ms because these are `pings` we are not interested in  
* Creates a simple CSV file

```
<#ftl output_format="plainText" strip_whitespace=true>
<#--
    Define custom grok pattern as map to match something like using "MY_SERVERLOG"
    2019-05-17 20:00:32,140 INFO  [xx.yyy.zzzz] (Thread-99) message response handled in: 62 ms; message counter: 2048; total message counter: 7094
-->
<#assign patternDefinitions = {
"MY_DATE": "%{YEAR}-%{MONTHNUM}-%{MONTHDAY}",
"MY_TIMESTAMP": "%{MY_DATE:date} %{TIME:time},%{INT:millis}",
"MY_MODULE": "\\[%{NOTSPACE}\\]",
"MY_THREAD": "\\(%{NOTSPACE}\\)",
"MY_SERVERLOG": "%{MY_TIMESTAMP} %{LOGLEVEL}%{SPACE:UNWANTED}%{MY_MODULE} %{MY_THREAD} message response handled in: %{INT:response_time} ms; %{GREEDYDATA:UNWANTED}"
}>

<#-- Instantiante the grok tool -->
<#assign grok = tools.grok.compile("%{MY_SERVERLOG}", patternDefinitions)>

<#-- Iterate over all data sources and convert matching lines to CSV output -->
<#compress>
    TIMESTAMP;MILLIS
    <#if dataSources?has_content>
        <#list dataSources?values as dataSource>
            <#list dataSource.getLineIterator() as line>
                <#assign parts = grok.match(line)>
                <#if parts?has_content>
                    <#-- Skip all response times less than 5 ms because these are boring pings -->
                    <#if parts.response_time?number gt 5>
                        ${parts.date}T${parts.time}.${parts.millis}+02:00;${parts.response_time}
                    </#if>
                </#if>
            </#list>
        </#list>
    </#if>
</#compress>
```
Executing the FTL yields the following output

```
> bin/freemarker-generator -t examples/templates/logs/csv/serverlog-to-csv.ftl examples/data/logs; echo
TIMESTAMP;MILLIS
2021-05-18T20:00:32.140;62
2021-05-18T21:00:32.140;162
```

