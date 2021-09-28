<#ftl output_format="plainText">
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
Support FreeMarker Directives
==============================================================================
has_content: ${dataSources?has_content?c}
size: ${dataSources?size}

Use FTL Array-style Access
==============================================================================
${dataSources?values[0].name}
${dataSources?values?first.name}

Get Document Names As Keys
==============================================================================
<#list dataSources?keys as name>
    ${name}<#lt>
</#list>

Iterate Over Names & DataSources
==============================================================================
<#list dataSources as name, dataSource>
    ${name} => ${dataSource.uri}<#lt>
</#list>

<#if dataSources?has_content>
    <#list dataSources?values as dataSource>
        <@writeDataSource dataSource/>
    </#list>
<#else>
    No data sources found ...
</#if>

<#macro writeDataSource dataSource>

${dataSource.name}
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
<#assign dataSource=dataSources?values?first>
Name                : ${dataSource.name}
Group               : ${dataSource.group}
Nr of lines         : ${dataSource.lines?size}
ContentType         : ${dataSource.contentType}
MimeType            : ${dataSource.mimeType}
Charset             : ${dataSource.charset}
Extension           : ${dataSource.extension}
Nr of chars         : ${dataSource.text?length}
Nr of bytes         : ${dataSource.bytes?size}
File name           : ${dataSource.fileName}
URI schema          : ${dataSource.uri.scheme}
Relative File Path  : ${dataSource.relativeFilePath}

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
<#list dataSource.metadata as name, value>
${name?right_pad(15)} : ${value}
</#list>

Iterating Over Properties Of A Datasource
---------------------------------------------------------------------------
<#list dataSource.properties as name, value>
${name?right_pad(15)} : ${value}
</#list>
</#macro>
