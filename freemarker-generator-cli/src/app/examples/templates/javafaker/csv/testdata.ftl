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
<#-- Get a localized JavaFaker instance -->
<#assign faker = tools.javafaker.getFaker("de_DE")>
<#assign nrOfRecords = tools.system.getProperty("NR_OF_RECORDS","100")>
<#assign days = tools.javafaker.timeUnits["DAYS"]>
<#assign csvTargetFormat = tools.csv.formats["DEFAULT"].withFirstRecordAsHeader()>
<#assign csvPrinter = tools.csv.printer(csvTargetFormat)>
<#assign csvHeaders = ['ID','CUSTOMER_ID','FIRST_NAME','LAST_NAME','EMAIL','IBAN','CREATED_AT']>
<#compress>
    <#if csvTargetFormat.getSkipHeaderRecord()>
        ${csvPrinter.printRecord(csvHeaders)}<#t>
    </#if>
    <#list 0..nrOfRecords?number as i>
        <#-- Generate a reproducable id to allow re-importing of test data -->
        <#assign id = tools.uuid.namedUUID("trxid-" + i?string)>
        <#assign customerId = faker.bothify("?#######")>
        <#assign firstName = faker.name().firstName()>
        <#assign lastName = faker.name().lastName()>
        <#assign email = firstName + "." + lastName + "@server.invalid">
        <#-- JavaFakers IBAN generation is really slow -->
        <#assign iban = faker.finance().iban("DE")>
        <#-- Distribute the creation date up to 10 years in the past -->
        <#assign createAt = faker.date().past(3650, days)>
        <#-- Use a CSV Printer to properly escape the output -->
        ${csvPrinter.printRecord(
            id,
            customerId,
            firstName,
            lastName,
            email?lower_case
            iban,
            createAt?datetime?iso_local)}
    </#list>
</#compress>
