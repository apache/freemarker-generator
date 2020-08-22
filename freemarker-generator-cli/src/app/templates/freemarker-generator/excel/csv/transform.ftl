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
<#-- Parse the first data source & sheet of the Excel document -->
<#import "/freemarker-generator/lib/commons-csv.ftl" as csv />
<#assign workbook = tools.excel.parse(dataSources?values[0])>
<#assign sheet = tools.excel.getSheets(workbook)[0]>
<#assign records = tools.excel.toTable(sheet)>
<#-- Setup CSVPrinter  -->
<#assign csvPrinter = tools.csv.printer(csv.targetFormat())>
<#-- Print each line of the Excel as CSV record -->
<#compress>
    <#list records as record>
        ${csvPrinter.printRecord(record)}
    </#list>
</#compress>
<#--------------------------------------------------------------------------->
<#function csvOutFormat>
    <#assign format = tools.csv.formats[CSV_TARGET_FORMAT!"DEFAULT"]>
    <#assign delimiter = tools.csv.toDelimiter(CSV_TARGET_DELIMITER!format.getDelimiter())>
    <#assign withHeader = CSV_TARGET_WITH_HEADER!"false">
    <#assign format = format.withDelimiter(delimiter)>
    <#if withHeader?boolean>
        <#assign format = format.withHeader>
    </#if>
    <#return format>
</#function>

