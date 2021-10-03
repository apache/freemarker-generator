<#ftl output_format="plainText" strip_whitespace=true>
<#--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->

<#--
    Define custom grok pattern as map to match something like using "MY_SERVERLOG"
    2019-05-17 20:00:32,140 INFO  [xx.yyy.zzzz] (Thread-99) message response handled in: 62 ms; message counter: 2048; total message counter: 7094
-->
<#assign patternDefinitions = {
"MY_DATE": "%{YEAR}-%{MONTHNUM}-%{MONTHDAY}",
"MY_TIMESTAMP": "%{MY_DATE:date} %{TIME:time},%{INT:millis}",
"MY_MODULE": "\\[%{NOTSPACE}\\]",
"MY_THREAD": "\\(%{NOTSPACE}\\)",
"MY_SERVERLOG": "^%{MY_TIMESTAMP} %{LOGLEVEL}%{SPACE:UNWANTED}%{MY_MODULE} %{MY_THREAD} message response handled in: %{INT:response_time} ms; %{GREEDYDATA:UNWANTED}$"
}>

<#-- Instantiante the grok tool -->
<#assign grok = tools.grok.create("%{MY_SERVERLOG}", patternDefinitions)>

<#-- Iterate over all data sources and convert matching lines to CSV output -->
<#compress>
    TIMESTAMP;MILLIS
    <#if dataSources?has_content>
        <#list dataSources as dataSource>
            <#list dataSource.getLineIterator() as line>
                <#assign parts = grok.match(line).capture()>
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