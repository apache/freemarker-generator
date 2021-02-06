<#ftl output_format="plainText" >
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
---------------------------------------------------------------------------
Has Content: ${dataSources?has_content?c}
Nr. of Documents: ${dataSources?size}

Use FTL Array-style Access
---------------------------------------------------------------------------
${dataSources?values[0].toString()}
${dataSources?values?first.toString()}

Get Document Names As Keys
---------------------------------------------------------------------------
<#list dataSources?keys as name>
    ${name}<#lt>
</#list>

Iterate Over Names & DataSources
---------------------------------------------------------------------------
<#list dataSources as name, dataSource>
    ${name} => ${dataSource.uri}<#lt>
</#list>

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
<#if dataSources?has_content>
<#assign dataSource=dataSources?values?first>
Name            : ${dataSource.name}
Nr of lines     : ${dataSource.lines?size}
Content Type    : ${dataSource.contentType}
Charset         : ${dataSource.charset}
Extension       : ${dataSource.extension}
Nr of chars     : ${dataSource.text?length}
Nr of bytes     : ${dataSource.bytes?size}
File name       : ${dataSource.metadata["filename"]}

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
<#list dataSource.metadata as name, value>
${name?right_pad(15)} : ${value}
</#list>
</#if>
