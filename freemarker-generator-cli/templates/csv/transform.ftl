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
<#-- Print each line without materializing the CSV in memory -->
<#compress>
    <#list csvParser.iterator() as record>
        ${csvPrinter.printRecord(record)}
    </#list>
</#compress>

<#function createCsvParser dataSource>
    <#assign initialCvsInFormat = CSVTool.formats[SystemTool.getParameter("csv.in.format", "DEFAULT")]>
    <#assign csvInDelimiter = CSVTool.toDelimiter(SystemTool.getParameter("csv.in.delimiter", initialCvsInFormat.getDelimiter()))>
    <#assign cvsInFormat = initialCvsInFormat.withDelimiter(csvInDelimiter)>
    <#return CSVTool.parse(dataSource, cvsInFormat)>
</#function>

<#function createCsvPrinter>
    <#assign initialCvsOutFormat = CSVTool.formats[SystemTool.getParameter("csv.out.format", "DEFAULT")]>
    <#assign csvOutDelimiter = CSVTool.toDelimiter(SystemTool.getParameter("csv.out.delimiter", initialCvsOutFormat.getDelimiter()))>
    <#assign cvsOutFormat = initialCvsOutFormat.withDelimiter(csvOutDelimiter)>
    <#return CSVTool.printer(cvsOutFormat, SystemTool.writer)>
</#function>