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
<#-- Setup Utah-Parser -->
<#assign conf =  tools.utahparser.getConfig(dataSources[0])>
<#assign parser = tools.utahparser.getParser(conf, dataSources[1])>
<#assign csvPrinter = "">
<#-- Print records as CSV  -->
<#compress>
    <#list parser.iterator() as record>
        <#if !csvPrinter?has_content>
            <#-- Lazy creation of a CSVFormat for writing the records -->
            <#assign defaultCsvformat = tools.csv.formats[CSV_TARGET_FORMAT!"DEFAULT"]>
            <#assign delimiter = tools.csv.toDelimiter(CSV_TARGET_DELIMITER!defaultCsvformat.getDelimiter())>
            <#assign csvHeaders = tools.utahparser.getHeaders(record)>
            <#assign cvsFormat = defaultCsvformat.withHeader(csvHeaders).withDelimiter(delimiter)>
            <#assign csvPrinter = tools.csv.printer(cvsFormat)>
        </#if>
        ${csvPrinter.printRecord(record, csvHeaders)}
    </#list>
</#compress>
