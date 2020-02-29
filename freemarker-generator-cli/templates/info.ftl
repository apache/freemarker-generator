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
FreeMarker CLI Information
---------------------------------------------------------------------------
FreeMarker version     : ${.version}
Template name          : ${.current_template_name}
Language               : ${.lang}
Locale                 : ${.locale}
Timestamp              : ${.now}
Output encoding        : ${.output_encoding}
Output format          : ${.output_format}

FreeMarker CLI Template Directories
---------------------------------------------------------------------------
<#list SystemTool.getTemplateDirectories() as directory>
[${directory?counter}] ${directory}
</#list>

FreeMarker CLI Tools
---------------------------------------------------------------------------
<#list .data_model?keys?sort as key>
<#if key?ends_with("Tool")>
- ${key?right_pad(20)} : ${.data_model[key]}
</#if>
</#list>

FreeMarker CLI Datasources
---------------------------------------------------------------------------
<#list Datasources.list as datasource>
[${datasource?counter}] ${datasource.name}, ${datasource.location}, ${datasource.length} Bytes
</#list>

User Supplied Parameters
---------------------------------------------------------------------------
<#list SystemTool.parameters as name,value>
- ${name} ==> ${value}
</#list>

User Supplied System Properties
---------------------------------------------------------------------------
<#list SystemTool.userSystemProperties as name,value>
- ${name} ==> ${value}
</#list>

SystemTool
---------------------------------------------------------------------------
Command line         : ${SystemTool.getCommandLineArgs()?join(", ")}
Host Name            : ${SystemTool.getHostName()}
Java Home            : ${SystemTool.getEnv("JAVA_HOME", "N.A.")}
User Name            : ${SystemTool.getSystemProperty("user.name", "N.A.")}
Timestamp            : ${SystemTool.currentTimeMillis}
Writer               : ${SystemTool.writer.class.name}
