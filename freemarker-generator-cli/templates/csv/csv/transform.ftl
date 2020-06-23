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
<#assign csvParser = CSVTool.parse(dataSource, csvInFormat())>
<#assign csvPrinter = CSVTool.printer(csvOutFormat())>
<#list csvParser.iterator() as record>
    ${csvPrinter.printRecord(record)}<#t>
</#list>

<#function csvInFormat>
    <#assign format = CSVTool.formats[CSV_IN_FORMAT!"DEFAULT"]>
    <#assign delimiter = CSVTool.toDelimiter(CSV_IN_DELIMITER!format.getDelimiter())>
    <#assign withHeader = CSV_IN_WITH_HEADER!"false">
    <#assign format = format.withDelimiter(delimiter)>
    <#if withHeader?boolean>
        <#assign format = format.withHeader()>
    </#if>
    <#return format>
</#function>

<#function csvOutFormat>
    <#assign format = CSVTool.formats[CSV_OUT_FORMAT!"DEFAULT"]>
    <#assign delimiter = CSVTool.toDelimiter(CSV_OUT_DELIMITER!format.getDelimiter())>
    <#assign withHeader = CSV_OUT_WITH_HEADER!"false">
    <#assign format = format.withDelimiter(delimiter)>
    <#if withHeader?boolean>
        <#assign format = format.withHeader>
    </#if>
    <#return format>
</#function>
