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

<#---
    Detemine the CSV format for reading a CSV files using user-supplied
    parameters from the data model.

    * CSV_SOURCE_FORMAT - see https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html
    * CSV_SOURCE_DELIMITER - symbolic name of delimiter, e.g. "COLON" or "SEMICOLON"
    * CSV_SOURCE_WITH_HEADER - whether the first rows are headers
-->
<#function sourceFormat>
    <#assign format = CSVTool.formats[CSV_SOURCE_FORMAT!"DEFAULT"]>
    <#assign delimiter = CSVTool.toDelimiter(CSV_SOURCE_DELIMITER!format.getDelimiter())>
    <#assign withHeader = CSV_SOURCE_WITH_HEADER!"true">
    <#assign format = format.withDelimiter(delimiter)>
    <#if withHeader?boolean>
        <#assign format = format.withFirstRecordAsHeader()>
    </#if>
    <#return format>
</#function>

<#---
    Detemine the CSV format for printing a CSV files using user-supplied
    parameters from the data model.

    * CSV_TARGET_FORMAT - see https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html
    * CSV_TARGET_DELIMITER - symbolic name of delimiter, e.g. "COLON" or "SEMICOLON"
    * CSV_TARGET_WITH_HEADER - whether the first rows are headers
-->
<#function targetFormat>
    <#assign format = CSVTool.formats[CSV_TARGET_FORMAT!"DEFAULT"]>
    <#assign delimiter = CSVTool.toDelimiter(CSV_TARGET_DELIMITER!format.getDelimiter())>
    <#assign withHeader = CSV_TARGET_WITH_HEADER!"true">
    <#assign format = format.withDelimiter(delimiter)>
    <#if withHeader?boolean>
        <#assign format = format.withFirstRecordAsHeader()>
    </#if>
    <#return format>
</#function>
