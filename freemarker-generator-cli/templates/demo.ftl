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
<#assign date = FreeMarkerTool.objectConstructor("java.util.Date", 1000 * 3600 * 24)>
new java.utilDate(1000 * 3600 * 24): ${date?datetime}

3) Invoke a static method of an non-constructor class
---------------------------------------------------------------------------
Random UUID              : ${FreeMarkerTool.statics["java.util.UUID"].randomUUID()}
System.currentTimeMillis : ${FreeMarkerTool.statics["java.lang.System"].currentTimeMillis()}

4) Access an Enumeration
---------------------------------------------------------------------------
java.math.RoundingMode#UP: ${FreeMarkerTool.enums["java.math.RoundingMode"].UP}

5) Loop Over The Values Of An Enumeration
---------------------------------------------------------------------------
<#list FreeMarkerTool.enums["java.math.RoundingMode"]?values as roundingMode>
- java.math.RoundingMode.${roundingMode}<#lt>
</#list>

6) Display list of data sources
---------------------------------------------------------------------------
List all data sources:
<#list DataSources.list as dataSource>
- Document: name=${dataSource.name} uri=${dataSource.uri} length=${dataSource.length} encoding=${dataSource.encoding!""}
</#list>

7) SystemTool
---------------------------------------------------------------------------
Host name       : ${SystemTool.getHostName()}
Command line    : ${SystemTool.getCommandLineArgs()?join(", ")}
System property : ${SystemTool.getSystemProperty("user.name", "N.A.")}
Timestamp       : ${SystemTool.currentTimeMillis?c}
Environment var : ${SystemTool.envs["USER"]!"N.A."}

8) Access System Properties
---------------------------------------------------------------------------
app.dir      : ${SystemTool.systemProperties["app.dir"]!""}
app.home     : ${SystemTool.systemProperties["app.home"]!""}
app.pid      : ${SystemTool.systemProperties["app.pid"]!""}
basedir      : ${SystemTool.systemProperties["basedir"]!""}
java.version : ${SystemTool.systemProperties["java.version"]!""}
user.name    : ${SystemTool.systemProperties["user.name"]!""}
user.dir     : ${SystemTool.systemProperties["user.dir"]!""}
user.home    : ${SystemTool.systemProperties["user.home"]!""}

9) List Environment Variables
---------------------------------------------------------------------------
<#list SystemTool.envs as name,value>
- ${name} ==> ${value}<#lt>
</#list>

10) List System Properties
---------------------------------------------------------------------------
<#list SystemTool.systemProperties as name,value>
- ${name} ==> ${value}<#lt>
</#list>

11) Access DataSources
---------------------------------------------------------------------------
Get the number of documents:
- ${DataSources.size()}
<#if !DataSources.isEmpty()>
Get the first document
- ${DataSources.get(0)!"NA"}
</#if>
List all files containing "README" in the name
<#list DataSources.find("*README*") as dataSource>
- ${dataSource.name}
</#list>
List all files having "md" extension
<#list DataSources.find("*.md") as dataSource>
- ${dataSource.name}
</#list>
Get all documents
<#list DataSources.list as dataSource>
- ${dataSource.name} => ${dataSource.uri}
</#list>

12) FreeMarker CLI Tools
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
<#if key?ends_with("Tool")>
- ${key?right_pad(20)} : ${.data_model[key]}
</#if>
</#list>

13) Document Data Model
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
- ${key}<#lt>
</#list>

14) Create a UUID
---------------------------------------------------------------------------
UUIDTool Random UUID  : ${UUIDTool.randomUUID()}
UUIDTool Named UUID   : ${UUIDTool.namedUUID("value and salt")}

15) Printing Special Characters
---------------------------------------------------------------------------
German Special Characters: äöüßÄÖÜ

16) Locale-specific output
---------------------------------------------------------------------------
<#setting number_format=",##0.00">
<#assign smallNumber = 1.234>
<#assign largeNumber = 12345678.9>
Small Number :  ${smallNumber}
Large Number :  ${largeNumber}
Date         :  ${.now?date}
Time         :  ${.now?time}

17) Execute a program
---------------------------------------------------------------------------
> date
${ExecTool.execute("date")}