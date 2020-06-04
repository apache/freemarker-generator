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
<#assign map = GsonTool.parse(DataSources.get(0))>
<#assign basePath = map.basePath!"/">
<#assign paths = map.paths!{}>

<#compress>
    ENDPOINT;METHOD;CONSUMES;PRODUCES;PARAMETERS;SUMMARY;DESCRIPTION
    <#list paths as endpoint,metadata>
        <#assign url = basePath + endpoint>
        <#assign names = metadata?keys?sort>
        <#list names as name>
            <#assign method = paths[endpoint][name]>
            <#assign summary = sanitize(method["summary"]!"")>
            <#assign description = sanitize(method["description"]!"")>
            <#assign consumes = join(method["consumes"]![])>
            <#assign produces = join(method["produces"]![])>
            <#assign parameters = method["parameters"]>
            ${url};${name?upper_case};${consumes};${produces};${parameters?size};${summary};${description}
        </#list>
    </#list>
</#compress>
${'\n'}

<#function sanitize str>
    <#return (((str?replace(";", ","))?replace("(\\n)+", "",'r')))?truncate(250)>
</#function>

<#function join list>
    <#if list?has_content>
        <#return list?join(", ")>
    <#else>
        <#return "">
    </#if>
</#function>
