## Generating Test Data

The generation of test data is supported by [Java Faker](https://github.com/DiUS/java-faker).

Let's assume that you need to populate a user table based on a CSV file with random data

```
freemarker-generator -DNR_OF_RECORDS=10 -t examples/templates/javafaker/csv/testdata.ftl; echo
```  

will generate

```
ID,CUSTOMER_ID,FIRST_NAME,LAST_NAME,EMAIL,IBAN,CREATED_AT
ec48407f-b74b-3255-aa23-c478b97fe6a0,k1627756,Finnja,Huebel,finnja.huebel@server.invalid,DE34649925979537623502,2019-02-02T06:19:32+01:00
4386bbd1-f410-310f-9961-3e8b2bdfb158,k2697194,Catharina,Ruckdeschel,catharina.ruckdeschel@server.invalid,DE20946378663346781874,2016-01-08T21:31:30+01:00
5225a437-a981-33f8-be17-4520d1656357,i2477693,Efe,Grün,efe.grün@server.invalid,DE72530958450307406958,2014-01-27T19:33:54+01:00
f3d7cbd6-b7ba-3709-97e3-a0f8c66f3a61,h1789606,Adriano,Walz,adriano.walz@server.invalid,DE91943313948716057559,2019-01-06T18:57:24+01:00
3b43d134-8342-322d-8814-2efb8d2525af,x9067951,Denny,Kleininger,denny.kleininger@server.invalid,DE60996500459835447795,2014-10-30T19:07:59+01:00
0f145471-293f-32c4-8dd2-9be95540e5f6,k9415677,Silas,Bönisch,silas.bönisch@server.invalid,DE53857572315572131803,2014-07-15T15:33:06+02:00
ec0a21b7-106b-3983-8b19-03bf91f5fa33,a0190000,Ceyda,Schäffel,ceyda.schäffel@server.invalid,DE83184389823488369676,2020-07-28T21:16:33+02:00
9c0d0feb-dd08-3cbd-875e-ea9f072d1a96,w7084669,Wilhelm,Burmeister,wilhelm.burmeister@server.invalid,DE89066581983817613534,2017-09-26T04:01:56+02:00
76f770a9-4008-317e-800f-6c203dd363d8,f7683995,Madlen,Knobel,madlen.knobel@server.invalid,DE82012476109671707669,2018-07-08T05:52:25+02:00
6ecf87f6-6a46-3f06-9482-ab2ffe696c41,y8814536,Jessica,Koubaa,jessica.koubaa@server.invalid,DE79396597066674926625,2019-10-12T23:55:02+02:00
4a72d322-55db-35ee-b47e-c484d29b5760,v1849194,Jesper,Hildenbrand,jesper.hildenbrand@server.invalid,DE60616233632021309556,2015-03-09T01:37:18+01:00
```

using the following FreeMarker template

```
<#-- Get a localized JavaFaker instance -->
<#assign faker = tools.javafaker.getFaker("de_DE")>
<#assign nrOfRecords = tools.system.getString("NR_OF_RECORDS","100")>
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
```

Some thoughts along the line

* [Java Faker](https://github.com/DiUS/java-faker) does not create coherent test data, e.g. each invocation of "name" creates a new random name - hence we create the email address ourselves
* The created IBAN does not use a valid bank code but structure and checksum is correct (albeit slow)
* The "createdAt" generates a creation date from the last 10 years to have some proper distribution
* See [A Guide to JavaFaker](https://www.baeldung.com/java-faker) for a quick overview
* A CSV Printer is used to properly escape the generated fields (in case they contain a CSV separator)