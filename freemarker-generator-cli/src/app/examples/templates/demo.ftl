<#ftl output_format="plainText">
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
<#assign deductSensitiveInformation = (tools.system.systemProperties["freemarkerGenerator.examples.deductSensitiveInformation"]!'false') != 'false'>
1) FreeMarker Special Variables
---------------------------------------------------------------------------
FreeMarker version     : ${.version}
Template name          : ${.current_template_name}
Language               : ${.lang}
Locale                 : ${.locale}
Timestamp              : ${.now}
Output encoding        : ${.output_encoding!"not set"}
Output format          : ${.output_format}

2) Invoke a constructor of a Java class
---------------------------------------------------------------------------
<#assign date = tools.freemarker.objectConstructor("java.util.Date", 1000 * 3600 * 24)>
new java.utilDate(1000 * 3600 * 24): ${date?datetime}

3) Invoke a static method of an non-constructor class
---------------------------------------------------------------------------
Random UUID              : ${tools.freemarker.statics["java.util.UUID"].randomUUID()}
System.currentTimeMillis : ${tools.freemarker.statics["java.lang.System"].currentTimeMillis()}

4) Access an Enumeration
---------------------------------------------------------------------------
java.math.RoundingMode#UP: ${tools.freemarker.enums["java.math.RoundingMode"].UP}

5) Loop Over The Values Of An Enumeration
---------------------------------------------------------------------------
<#list tools.freemarker.enums["java.math.RoundingMode"]?values as roundingMode>
- java.math.RoundingMode.${roundingMode}<#lt>
</#list>

6) Display list of data sources
---------------------------------------------------------------------------
List all data sources:
<#list dataSources as dataSource>
- Document: name=${dataSource.name} uri=${dataSource.uri} length=${dataSource.length} charset=${dataSource.charset}
</#list>

7) SystemTool
---------------------------------------------------------------------------
Host name       : ${tools.system.getHostName()}
Command line    : ${tools.system.getCommandLineArgs()?join(", ")}
System property : ${tools.system.getSystemProperty("user.name", "N.A.")}
Timestamp       : ${tools.system.currentTimeMillis?c}
Environment     : ${tools.system.envs.USER!"N.A."}

8) Access System Properties
---------------------------------------------------------------------------
app.dir      : ${tools.system.systemProperties["app.dir"]!""}
app.home     : ${tools.system.systemProperties["app.home"]!""}
app.pid      : ${tools.system.systemProperties["app.pid"]!""}
basedir      : ${tools.system.systemProperties["basedir"]!""}
java.version : ${tools.system.systemProperties["java.version"]!""}
user.name    : ${tools.system.systemProperties["user.name"]!""}
user.dir     : ${tools.system.systemProperties["user.dir"]!""}
user.home    : ${tools.system.systemProperties["user.home"]!""}

9) List Environment Variables
---------------------------------------------------------------------------
<#list tools.system.envs as name,value>
<#if !deductSensitiveInformation
    || ['BASEDIR', 'USERNAME', 'CLASSPATH', 'TEMP', 'JAVA_HOME']?seq_contains(name?upper_case)
>
- ${name} ==> ${value}<#lt>
</#if>
</#list>
<#if deductSensitiveInformation>
[...]
Some items were deducted!
</#if>

10) List System Properties
---------------------------------------------------------------------------
<#list tools.system.systemProperties as name, value>
<#if !deductSensitiveInformation
    || ['app.dir', 'app.home', 'app.pid', 'basedir', 'java.version', 'user.home', 'user.dir', 'user.name', 'user.timezone'
        'file.separator', 'java.class.path', 'java.home']?seq_contains(name)
>
- ${name} ==> ${value}<#lt>
</#if>
</#list>
<#if deductSensitiveInformation>
[...]
Some items were deducted!
</#if>

11) Access DataSources
---------------------------------------------------------------------------
Get the number of data sources:
- ${dataSources?size}
<#if dataSources?has_content>
Get the first data source:
- ${dataSources[0].name!"No data sources provided"}
</#if>
Get all documents as map:
<#list dataSources as name, ds>
- ${name} => ${ds.mimeType}
</#list>
List all data sources containing "test" in the name
<#list dataSources?filter(ds -> ds.match("name", "*test*")) as ds>
- ${ds.name}
</#list>
List all data sources having "json" extension
<#list dataSources?filter(ds -> ds.match("extension", "json")) as ds>
- ${ds.name}
</#list>
List all data sources having "src/test/data/properties" in their file path
<#list dataSources?filter(ds -> ds.match("filePath", "*/src/test/data/properties")) as ds>
- ${ds.name}
</#list>

12) Document Data Model
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
- ${key}<#lt>
</#list>

13) FreeMarker Generator Tools
---------------------------------------------------------------------------
<#list .data_model.tools?keys?sort as key>
- ${key?right_pad(20)} : ${.data_model.tools[key]}
</#list>

14) Create a UUID
---------------------------------------------------------------------------
UUIDTool Random UUID  : ${tools.uuid.randomUUID()}
UUIDTool Named UUID   : ${tools.uuid.namedUUID("value and salt")}

15) Printing Special Characters
---------------------------------------------------------------------------
Windows 1252 Characters: €¥§ÆÇæ®
German Special Characters: äöüßÄÖÜ
Cyrillic Characters: Кириллица
Chinese Characters: 你好吗

16) Locale-specific output
---------------------------------------------------------------------------
<#setting number_format=",##0.00">
<#assign smallNumber = 1.234>
<#assign largeNumber = 12345678.9>
Small Number :  ${smallNumber}
Large Number :  ${largeNumber}
Date         :  ${.now?date}
Time         :  ${.now?time}
