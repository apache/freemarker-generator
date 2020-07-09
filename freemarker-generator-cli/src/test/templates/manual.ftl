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
${dataSources[0].toString()}

Use FTL Map-style access
---------------------------------------------------------------------------
${DataSources["github-users.json"].toString()}
${DataSources["github-users.json"].name}

Get Document Names As Keys
---------------------------------------------------------------------------
<#list DataSources?keys as name>
    ${name}<#lt>
</#list>

Iterate Over Names & DataSources
---------------------------------------------------------------------------
<#list DataSources as name, dataSource>
    ${name} => ${dataSource}<#lt>
</#list>

Find DataSources By Group
---------------------------------------------------------------------------
<#list dataSources.findByGroup("default") as dataSource>
    ${dataSource}<#lt>
</#list>

Find DataSources By Wildcard
---------------------------------------------------------------------------
<#list dataSources.find("*.csv") as dataSource>
    ${dataSource}<#lt>
</#list>

Java Array-style access
---------------------------------------------------------------------------
${dataSources?values[0].toString()}

Invoke Arbitrary Methods On DataSources
---------------------------------------------------------------------------
empty       : ${dataSources.empty?c}
isEmpty()   : ${dataSources.isEmpty()?c}
size()      : ${dataSources.size()}
close()     : ${dataSources.close()}worx
