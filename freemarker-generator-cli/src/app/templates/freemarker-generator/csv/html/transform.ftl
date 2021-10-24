<#ftl output_format="HTML" strip_whitespace=true>
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
<#import "/freemarker-generator/lib/commons-csv.ftl" as csv />
<#assign dataSource = dataSources[0]>
<#assign csvParser = tools.csv.parse(dataSource, csv.sourceFormat())>
<#assign csvHeaders = csvParser.getHeaderNames()>
<#--------------------------------------------------------------------------->
<!DOCTYPE html>
<html>
<head>
    <title>${dataSource.name}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<table class="table table-striped">
    <@writeHeaders csvParser.getHeaderNames()/>
    <#list csvParser.iterator() as record>
        <@writeColumns record/>
    </#list>
</table>
</body>
</html>
<#--------------------------------------------------------------------------->
<#macro writeHeaders headers>
    <#if headers?has_content>
        <tr>
            <#list headers as header>
                <th>${header}</th>
            </#list>
        </tr>
    </#if>
</#macro>
<#--------------------------------------------------------------------------->
<#macro writeColumns record>
    <#if record?has_content>
        <tr>
            <#list record.iterator() as field>
                <td>${field}</td>
            </#list>
        </tr>
    </#if>
</#macro>