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
<#assign csvParser = createCsvParser(DataSources.get(0))>
<#assign csvPrinter = createCsvPrinter()>
<#--
    Print each record directly to the underyling writer without materializing the CSV in memory.
    FreeMarker and CSV output are out of sync but millions of records can processed without
    running out of memory.
-->
<#compress>
    <#list csvParser.iterator() as record>
        ${csvPrinter.printRecord(record)}
    </#list>
</#compress>

<#function createCsvParser dataSource>
    <#assign initialCvsInFormat = CSVTool.formats[CSV_IN_FORMAT!"DEFAULT"].withHeader()>
    <#assign csvInDelimiter = CSVTool.toDelimiter(CSV_IN_DELIMITER!initialCvsInFormat.getDelimiter())>
    <#assign cvsInFormat = initialCvsInFormat.withDelimiter(csvInDelimiter)>
    <#return CSVTool.parse(dataSource, cvsInFormat)>
</#function>

<#function createCsvPrinter>
    <#assign initialCvsOutFormat = CSVTool.formats[CSV_OUT_FORMAT!"DEFAULT"]>
    <#assign csvOutDelimiter = CSVTool.toDelimiter(CSV_OUT_DELIMITER!initialCvsOutFormat.getDelimiter())>
    <#assign cvsOutFormat = initialCvsOutFormat.withDelimiter(csvOutDelimiter)>
    <#return CSVTool.printer(cvsOutFormat, SystemTool.writer)>
</#function>