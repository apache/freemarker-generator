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
Support FreeMarker Directives
==============================================================================
dataSources?has_content: ${dataSources?has_content?c}
dataSources?size: ${dataSources?size}

Use FTL Array-style Access
==============================================================================
<#if dataSources?has_content>
dataSources[0]: ${dataSources[0].name}
<#else>
No data sources provided ...
</#if>

Iterate Over DataSources as List
==============================================================================
<#list dataSources as dataSource>
- dataSource[${dataSource?index}] => ${dataSource.name}<#lt>
</#list>

Iterate Over DataSources as Map
==============================================================================
<#list dataSources as name, dataSource>
- dataSource["${name}"] => ${dataSource.name}<#lt>
</#list>

Iterate Over DataSources as Values
==============================================================================
<#list dataSources?values as dataSource>
- dataSource[${dataSource?index}] => ${dataSource.name}<#lt>
</#list>

Get Document Names As Keys
==============================================================================
<#list dataSources?keys as name>
- ${name}<#lt>
</#list>

Access Underlying DataSources API
==============================================================================
DataSources.getNames(): ${dataSources?api.names?size}
DataSources.getGroups(): ${dataSources?api.getGroups()?size}
DataSources.find(): ${dataSources?api.find("*")?size}

Iterate Over DataSources Using Wildcard Search
==============================================================================
<#if dataSources?has_content>
<#list dataSources?api.find("*") as dataSource>
- ${dataSource.name}
</#list>
<#else>
No data sources provided ...
</#if>

<#if dataSources?has_content>
<#list dataSources as dataSource>
[#${dataSource?counter}] - ${dataSource.name}
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
<#assign dataSource=dataSources?first>
Name                : ${dataSource.name}
Nr of lines         : ${dataSource.lines?size}
Content Type        : ${dataSource.contentType}
Charset             : ${dataSource.charset}
Extension           : ${dataSource.extension}
Nr of chars         : ${dataSource.text?length}
Nr of bytes         : ${dataSource.bytes?size}

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
<#list dataSource.metadata as name, value>
${name?right_pad(19)} : ${value}
</#list>

</#list>
</#if>