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
FreeMarker Generator Information
------------------------------------------------------------------------------
FreeMarker version     : ${.version}
Template name          : ${.current_template_name}
Language               : ${.lang}
Locale                 : ${.locale}
Timestamp              : ${.now}
Output encoding        : ${.output_encoding}
Output format          : ${.output_format}

FreeMarker Generator Template Loader Directories
------------------------------------------------------------------------------
<#list tools.system.getTemplateDirectories() as directory>
[#${directory?counter}] ${directory}
</#list>

FreeMarker Generator Data Model
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
- ${key}<#lt>
</#list>

FreeMarker Generator DataSources
------------------------------------------------------------------------------
<#if dataSources?has_content>
    <#list dataSources?values as ds>
        [#${ds?counter}]: name=${ds.name}, group=${ds.group}, fileName=${ds.fileName}, mimeType=${ds.mimeType}, charset=${ds.charset}, length=${ds.length} Bytes
        URI : ${ds.uri}
    </#list>
<#else>
    No data sources found ...
</#if>

FreeMarker Generator Parameters
------------------------------------------------------------------------------
<#if tools.system.parameters?has_content>
    <#list tools.system.parameters as key,value>
        <#if value?is_hash>
            - ${key} ==> { <#list value as name,value>${name}=${value} </#list>}
        <#else>
            - ${key} ==> ${value}
        </#if>
    </#list>
<#else>
    No parameters found ...
</#if>

FreeMarker Generator Tools
------------------------------------------------------------------------------
<#list tools?keys?sort as name>
    - ${name?right_pad(20)} : ${tools[name]}
</#list>
