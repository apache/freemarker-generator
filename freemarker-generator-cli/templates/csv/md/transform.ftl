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
<#import "/templates/lib/commons-csv.ftl" as csv />
<#assign dataSource = DataSources.get(0)>
<#assign csvParser = CSVTool.parse(dataSource, csv.sourceFormat())>
<#assign headers = (csvParser.getHeaderMap()!{})?keys>
<#assign records = csvParser.records>
<#--------------------------------------------------------------------------->
<#compress>
    <@writeHeaders headers/>
    <@writeColums records/>
</#compress>
<#--------------------------------------------------------------------------->
<#macro writeHeaders headers>
    <#if headers?has_content>
        | ${headers?join(" | ", "")} |
        <#list headers as header>| --------</#list>|
    </#if>
</#macro>
<#--------------------------------------------------------------------------->
<#macro writeColums columns>
    <#if columns?has_content>
        <#list columns as column>
            | ${column.iterator()?join(" | ", "")} |
        </#list>
    </#if>
</#macro>
