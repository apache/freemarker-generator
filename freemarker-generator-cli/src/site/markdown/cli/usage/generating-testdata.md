## Generating Test Data

The generation of test data is supported by [Java Faker](https://github.com/DiUS/java-faker).

Let's assume that you need to populate a user table based on a CSV file

```
freemarker-generator -t examples/templates/javafaker/csv/testdata.ftl
```  

will generate

```
ID,CUSTOMER_ID,FIRST_NAME,LAST_NAME,EMAIL,IBAN,CREATED_AT
7e522b5c-1c7f-4195-8585-2268cc7f152a,l1671977,Eddy,Schuster,eddy.schuster@gmail.com,AT101071425404684529,2019-05-18T17:28:30+02:00
701db208-923b-4100-a02b-4d574b0b703d,e5229102,Mark,Rogahn,mark.rogahn@gmail.com,AT491398501778297253,2014-06-16T21:48:24+02:00
d5e99c0f-0117-42f9-83a2-33052656e9d6,g0941131,Randall,Cronin,randall.cronin@gmail.com,AT788431029877229377,2016-01-08T05:12:26+01:00
e2b44ea6-4033-45c9-b9c3-bc9c8f6bc3fe,o4440179,Arthur,Reichel,arthur.reichel@gmail.com,AT767530622367679157,2013-04-07T12:56:13+02:00
d0dd2806-fe11-487c-9d9b-22dd3ec1abc4,i5706274,Elroy,Sawayn,elroy.sawayn@gmail.com,AT459308883343277427,2017-02-06T01:53:34+01:00
d36d6851-e961-4f07-9fcc-f7a0702fa6b6,s5645599,Jerald,Nitzsche,jerald.nitzsche@gmail.com,AT366350952237029663,2015-06-14T21:43:32+02:00
6b9999cf-5aed-4e12-bc6e-e88a7973154d,n4220945,Grant,Bayer,grant.bayer@gmail.com,AT275651850312588466,2020-07-18T15:07:07+02:00
5fd8286e-cc2f-4fe9-bf56-2245d1bf0c34,t0311536,Edison,Hoppe,edison.hoppe@gmail.com,AT847505633045057210,2017-04-23T23:26:42+02:00
e0638c76-f130-4daa-9c62-40304101c8ab,b7209087,Nicky,Cole,nicky.cole@gmail.com,AT127017906823575933,2014-08-30T20:38:11+02:00
c35e2b24-9b9b-4fb0-b25b-72af93301da5,a0087873,Raleigh,Leffler,raleigh.leffler@gmail.com,AT140117255798582963,2016-04-04T12:12:29+02:00
```

using the following FreeMarker template

```
<#import "/freemarker-generator/lib/commons-csv.ftl" as csv />
<#assign timeUnits = tools.javafaker.timeUnits>
<#assign faker = tools.javafaker.faker>
<#assign csvTargetFormat = csv.targetFormat()>
<#assign csvPrinter = tools.csv.printer(csvTargetFormat)>
<#assign csvHeaders = ['ID','CUSTOMER_ID','FIRST_NAME','LAST_NAME','EMAIL','IBAN','CREATED_AT']>
<#compress>
    <#if csvTargetFormat.getSkipHeaderRecord()>
        ${csvPrinter.printRecord(csvHeaders)}<#t>
    </#if>
    <#list 1..10 as i>
        <#assign id = tools.uuid.randomUUID()>
        <#assign customerId = faker.bothify("?#######")>
        <#assign firstName = faker.name().firstName()>
        <#assign lastName = faker.name().lastName()>
        <#assign email = firstName + "." + lastName + "@gmail.com">
        <#assign iban = faker.finance().iban("AT")>
        <#assign createAt = faker.date().past(3650, timeUnits["DAYS"])>

        ${csvPrinter.printRecord(
        id,
        customerId,
        firstName,
        lastName,
        email?lower_case
        iban,
        createAt?datetime?iso_local
        )}<#t>
    </#list>
</#compress>
```
