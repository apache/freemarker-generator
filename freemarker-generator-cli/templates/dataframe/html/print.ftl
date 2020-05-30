<#ftl output_format="HTML" >
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
<#assign dataSource = DataSources.get(0)>
<#assign name = dataSource.name>
<#assign date = .now?iso_utc>
<#assign dataFrame = DataFrameTool.parse(dataSource, csvReader())>
<#--------------------------------------------------------------------------->
<!DOCTYPE html>
<html>
<head>
    <title>${name}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<div class="container-fluid">
    <h1>DataFrame Test <small>${name}, ${date}</small></h1>
    <table class="table table-striped">
        <@writeHeader dataFrame/>
        <@writeRows dataFrame/>
    </table>
</div>
</body>
</html>

<#--------------------------------------------------------------------------->
<#function csvReader>
    <#return DataFrameTool.csvReaderBuilder.containsHeader(true).withSeparator(CSV_IN_DELIMITER!';').build()>
</#function>

<#--------------------------------------------------------------------------->
<#macro writeHeader dataFrame>
    <tr>
        <#list dataFrame.columns as column>
            <th>${column.name}</th>
        </#list>
    </tr>
</#macro>

<#--------------------------------------------------------------------------->
<#macro writeRows dataFrame>
    <#list dataFrame.iterator() as row>
        <tr>
            <#list 0..row.size()-1 as idx>
                <td>${row.getString(idx)}</td>
            </#list>
        </tr>
    </#list>
</#macro>