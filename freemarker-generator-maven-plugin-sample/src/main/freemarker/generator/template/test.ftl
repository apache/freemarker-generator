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
<#assign model=session.currentProject.model>
<#assign properties=session.currentProject.properties>
<#assign dependencies=session.currentProject.dependencies>
Accessing Generator Data
-----------------------------------------------------------------------------
This is a test freemarker template. Test json data: '${testVar}'

Maven Project Properties
-----------------------------------------------------------------------------
<#list properties as key,value>
- ${key?right_pad(38)} ==> ${value}<#lt>
</#list>

Maven Project Model
-----------------------------------------------------------------------------
- ArtifactId                             ==> ${model.artifactId}
- GroupId                                ==> ${model.groupId}
- Version                                ==> ${model.version}
- Name                                   ==> ${model.name}
- Description                            ==> ${model.description!""}

Maven Project Dependencies
-----------------------------------------------------------------------------
<#list dependencies as dependency>
- ${dependency.groupId}:${dependency.artifactId}:${dependency.version}:${dependency.optional?string('yes', 'no')}
</#list>