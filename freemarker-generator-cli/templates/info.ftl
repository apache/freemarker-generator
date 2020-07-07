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
FreeMarker CLI Information
------------------------------------------------------------------------------
FreeMarker version     : ${.version}
Template name          : ${.current_template_name}
Language               : ${.lang}
Locale                 : ${.locale}
Timestamp              : ${.now}
Output encoding        : ${.output_encoding}
Output format          : ${.output_format}

FreeMarker CLI Template Loader Directories
------------------------------------------------------------------------------
<#list tools.system.getTemplateDirectories() as directory>
[#${directory?counter}] ${directory}
</#list>

FreeMarker CLI Data Model
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
- ${key}<#lt>
</#list>

FreeMarker CLI Tools
------------------------------------------------------------------------------
<#list .data_model.tools?keys?sort as key>
- ${key?right_pad(20)} : ${.data_model.tools[key]}
</#list>

<#if dataSources.list?has_content>
FreeMarker CLI DataSources
------------------------------------------------------------------------------
<#list dataSources.list as dataSource>
[#${dataSource?counter}], name=${dataSource.name}, group=${dataSource.group}, mimeType=${dataSource.mimetype}, charset=${dataSource.charset}, length=${dataSource.length} Bytes
URI : ${dataSource.uri}
</#list>
</#if>

<#if tools.system.parameters?has_content>
FreeMarker CLI Parameters
------------------------------------------------------------------------------
<#list tools.system.parameters as key,value>
<#if value?is_hash>
- ${key} ==> { <#list value as name,value>${name}=${value} </#list>}
<#else>
- ${key} ==> ${value}
</#if>
</#list>
</#if>
