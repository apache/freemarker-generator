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
<#-- Setup Utah-Parser and parse all records to determine the headers -->
<#assign conf =  tools.utahparser.getConfig(dataSources[0])>
<#assign parser = tools.utahparser.getParser(conf, dataSources[1])>
<#assign records = parser.toList()>
<#assign headers = tools.utahparser.getHeaders(records)>
<#-- Setup CSVPrinter  -->
<#assign defaultCsvformat = tools.csv.formats[CSV_TARGET_FORMAT!"DEFAULT"]>
<#assign csvDelimiter = tools.csv.toDelimiter(CSV_TARGET_DELIMITER!defaultCsvformat.getDelimiter())>
<#assign cvsFormat = defaultCsvformat.withHeader(headers).withDelimiter(csvDelimiter)>
<#assign csvPrinter = tools.csv.printer(cvsFormat)>
<#-- Print records as CSV  -->
<#compress>
    <#list records as record>
        ${csvPrinter.printRecord(record, headers)}
    </#list>
</#compress>