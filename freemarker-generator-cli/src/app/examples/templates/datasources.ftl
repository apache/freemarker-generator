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
Support Of FreeMarker Directives
==============================================================================
dataSources?has_content: ${dataSources?has_content?c}
dataSources?size: ${dataSources?size}

Iterate Over DataSources Using Array-style Access
==============================================================================
<#if dataSources?has_content>
<#list 0..dataSources?size-1 as i>
- dataSource[${i}] ==> ${dataSources[i].name}
</#list>
<#else>
No data sources provided ...
</#if>

Iterate Over DataSources Using Sequence
==============================================================================
<#list dataSources as dataSource>
- dataSource[${dataSource?index}] => ${dataSource.name}
<#else>
No data sources provided ...
</#list>

Iterate Over DataSources Using Key & Values
==============================================================================
<#list dataSources as name, dataSource>
- dataSource["${name}"] => ${dataSource.name}<#lt>
<#else>
No data sources provided ...
</#list>

Iterate Over DataSources Using Values
==============================================================================
<#list dataSources?values as dataSource>
- dataSource[${dataSource?index}] => ${dataSource.name}
<#else>
No data sources provided ...
</#list>

Iterate Over DataSources Using Hash Map Keys
==============================================================================
<#list dataSources?keys as key>
- dataSource["${key}"] => ${dataSources[key].name}
<#else>
No data sources provided ...
</#list>

Iterate Over DataSources Using Lambda Expression
==============================================================================
<#list dataSources?filter(ds -> ds.match("group", "default")) as dataSource>
- Group "default" => ${dataSource.name}
<#else>
No data sources provided ...
</#list>

Iterate Over DataSources Using Wildcard Search
==============================================================================
<#list dataSources?api.find("*") as dataSource>
- ${dataSource.name}
<#else>
No data sources provided ...
</#list>

Access Underlying DataSources API
==============================================================================
DataSources.getNames(): ${dataSources?api.names?size}
DataSources.getGroups(): ${dataSources?api.getGroups()?size}
DataSources.find("*"): ${dataSources?api.find("*")?size}
DataSources.find("uri", "*.md"): ${dataSources?api.find("uri", "*.md")?size}
DataSources.find("extension", "md"): ${dataSources?api.find("extension", "md")?size}

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