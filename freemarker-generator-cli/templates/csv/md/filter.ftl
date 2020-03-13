<#ftl output_format="plainText" strip_text="true">
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
<#assign parser = parser(dataSource)>
<#assign headers = parser.getHeaderNames()>
<#assign column = SystemTool.getParameter("column")>
<#assign values = SystemTool.getParameter("values")?split(",")>

<#compress>
    <@writePageHeader dataSource/>
    <#-- Process each line without materializing the whole file in memory -->
    <#list parser.iterator() as record>
        <#if filter(record)>
            <@writeCsvRecord headers record/>
        </#if>
    </#list>
</#compress>

<#function parser dataSource>
    <#assign format = CSVTool.formats[SystemTool.getParameter("format", "DEFAULT")]>
    <#assign delimiter = CSVTool.toDelimiter(SystemTool.getParameter("delimiter", format.getDelimiter()))>
    <#return CSVTool.parse(dataSource, format.withFirstRecordAsHeader().withDelimiter(delimiter))>
</#function>

<#function filter record>
    <#return values?seq_contains(record.get(column))>
</#function>

<#macro writePageHeader dataSource>
    # ${dataSource.name}
</#macro>

<#macro writeCsvRecord headers record>
    ## Line ${record.getRecordNumber()}
    | Column    | Value                       |
    | --------- | --------------------------- |
    <#list headers as header>
    | ${header} | ${record.get(header)}       |
    </#list>
</#macro>
