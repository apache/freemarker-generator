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
<#assign dataSource = DataSources.get(0)>
<#assign name = dataSource.name>
<#assign workbook = ExcelTool.parse(dataSource)>
<#assign date = .now?iso_utc>
<#--------------------------------------------------------------------------->
<@writeSheets workbook/>

<#--------------------------------------------------------------------------->
<#-- writeSheets                                                           -->
<#--------------------------------------------------------------------------->
<#macro writeSheets workbook>
    <#assign sheets = ExcelTool.getSheets(workbook)>
    <#list sheets as sheet>
        <#assign table = ExcelTool.toTable(sheet)>
        <#assign df = DataFrameTool.fromRows(table, true)>
        ${DataFrameTool.print(df)}<#t>
    </#list>
</#macro>