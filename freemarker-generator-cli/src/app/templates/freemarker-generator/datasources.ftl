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
FreeMarker Generator DataSources
==============================================================================

<#if dataSources?has_content>
<#list dataSources?values as ds>
DataSource #${ds?counter}
------------------------------------------------------------------------------
name=${ds.name}
group=${ds.group}
contentType=${ds.contentType}
fileName=${ds.fileName}
baseName=${ds.baseName}
extension=${ds.extension}
relativeFilePath=${ds.relativeFilePath}
charset=${ds.charset}
mimeType=${ds.mimeType}
uri=charset=${ds.uri}
length=${ds.length} Bytes

</#list>
<#else>
No data sources found ...
</#if>
