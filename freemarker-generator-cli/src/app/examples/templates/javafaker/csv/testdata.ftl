<#ftl output_format="plainText" strip_whitespace=true>
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
<#import "/freemarker-generator/lib/commons-csv.ftl" as CSV />
<#assign NR_OF_RECORDS = tools.system.getString("NR_OF_RECORDS","10")>
<#assign DAYS = tools.javafaker.timeUnits["DAYS"]>
<#assign faker = tools.javafaker.faker>
<#assign csvTargetFormat = CSV.targetFormat()>
<#assign csvPrinter = tools.csv.printer(csvTargetFormat)>
<#assign csvHeaders = ['ID','CUSTOMER_ID','FIRST_NAME','LAST_NAME','EMAIL','IBAN','CREATED_AT']>
<#compress>
    <#if csvTargetFormat.getSkipHeaderRecord()>
        ${csvPrinter.printRecord(csvHeaders)}<#t>
    </#if>
    <#list 1..NR_OF_RECORDS?number as i>
        <#assign id = tools.uuid.randomUUID()>
        <#assign customerId = faker.bothify("?#######")>
        <#assign firstName = faker.name().firstName()>
        <#assign lastName = faker.name().lastName()>
        <#assign email = firstName + "." + lastName + "@gmail.com">
        <#assign iban = faker.finance().iban("AT")>
        <#assign createAt = faker.date().past(3650, DAYS)>
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