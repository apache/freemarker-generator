## Generating Test Data

The generation of test data is supported by [Java Faker](https://github.com/DiUS/java-faker).

Let's assume that you need to populate a user table based on a CSV file with random data

```
freemarker-generator -DNR_OF_RECORDS=10 -t src/app/examples/templates/javafaker/csv/testdata.ftl
```  

will generate

```
ID,CUSTOMER_ID,FIRST_NAME,LAST_NAME,EMAIL,IBAN,CREATED_AT
5c3dbf2b-2957-41fe-8566-e16c91d6bba7,e6044780,Audrey,Ryan,audrey.ryan@gmail.com,DE69137185182464804325,2016-07-25T03:06:54+02:00
34671167-92f8-46e3-874b-d488960eb320,z8394366,Herb,Lehner,herb.lehner@gmail.com,DE32993443443552974345,2019-10-11T23:27:32+02:00
479855f6-cc98-4c46-99e4-b1d38d35a7b2,x8937857,Kirby,Wilkinson,kirby.wilkinson@gmail.com,DE12566901129220359287,2020-04-12T04:49:00+02:00
1a3c51b9-d168-4c0a-84ae-05e79cd181b1,t3486957,Charmaine,Bergstrom,charmaine.bergstrom@gmail.com,DE98964063811726229158,2015-07-24T23:19:19+02:00
43b9f7ad-1aec-44ff-b3c1-de7688b5a729,z9190225,Sterling,Glover,sterling.glover@gmail.com,DE47207633748672977993,2019-11-04T04:45:06+01:00
34ce2c9f-e5bb-44f4-a71f-40b0dfa8d0bf,a4406167,George,Marquardt,george.marquardt@gmail.com,DE79342449317255392445,2016-05-15T16:33:05+02:00
1f9bbc16-8b17-4947-ab50-4abf6aa4cc46,s6438445,Arnoldo,Herzog,arnoldo.herzog@gmail.com,DE20421444995381411375,2013-10-12T07:01:01+02:00
30e3f7a2-7fe8-4ebf-b46b-4f59ab62ba45,o9507275,Nickie,Predovic,nickie.predovic@gmail.com,DE06666930299990216198,2019-08-01T10:51:51+02:00
f703e93e-7bc3-42c9-a7f5-f1db84d32fd1,z8385157,Clinton,Murphy,clinton.murphy@gmail.com,DE27305002168865903990,2018-04-01T19:03:55+02:00
7f6a8d29-2dfc-4467-b366-25b46aa5bc32,x5244747,Johnson,Blanda,johnson.blanda@gmail.com,DE83757301199253406795,2012-06-23T18:04:38+02:00
```

using the following FreeMarker template

```
<#assign faker = tools.javafaker.faker>
<#assign nrOfRecords = tools.system.getString("NR_OF_RECORDS","10")>
<#assign days = tools.javafaker.timeUnits["DAYS"]>
<#assign csvTargetFormat = tools.csv.formats["DEFAULT"].withFirstRecordAsHeader()>
<#assign csvPrinter = tools.csv.printer(csvTargetFormat)>
<#assign csvHeaders = ['ID','CUSTOMER_ID','FIRST_NAME','LAST_NAME','EMAIL','IBAN','CREATED_AT']>
<#compress>
    <#if csvTargetFormat.getSkipHeaderRecord()>
        ${csvPrinter.printRecord(csvHeaders)}<#t>
    </#if>
    <#list 1..nrOfRecords?number as i>
        <#assign id = tools.uuid.randomUUID()>
        <#assign customerId = faker.bothify("?#######")>
        <#assign firstName = faker.name().firstName()>
        <#assign lastName = faker.name().lastName()>
        <#assign email = firstName + "." + lastName + "@gmail.com">
        <#assign iban = faker.finance().iban("DE")>

        <#assign createAt = faker.date().past(3650, days)>
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
* The created IBAN does not use a valid bank code but structure and checksum is correct
* The "createdAt" generates a creation date from the last 10 years to have some proper distribution
* See [A Guide to JavaFaker](https://www.baeldung.com/java-faker) for a quick overview