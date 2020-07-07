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
Has Content: ${DataSources?has_content?c}
Nr. of Documents: ${DataSources?size}

Use FTL Array-style Access
---------------------------------------------------------------------------
${DataSources[0].toString()}

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
<#list DataSources.findByGroup("default") as dataSource>
    ${dataSource}<#lt>
</#list>

Find DataSources By Wildcard
---------------------------------------------------------------------------
<#list DataSources.find("*.csv") as dataSource>
    ${dataSource}<#lt>
</#list>

Java Array-style access
---------------------------------------------------------------------------
${DataSources.get(0).toString()}

Invoke Arbitrary Methods On DataSources
---------------------------------------------------------------------------
empty       : ${DataSources.empty?c}
isEmpty()   : ${DataSources.isEmpty()?c}
size()      : ${DataSources.size()}
close()     : ${DataSources.close()}worx
