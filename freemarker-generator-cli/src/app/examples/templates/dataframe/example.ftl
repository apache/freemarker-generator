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
<#assign dataSource = dataSources[0]>
<#assign csvParser = tools.csv.parse(dataSource, tools.csv.formats.DATAFRAME)>
<#assign users = tools.dataframe.fromCSVParser(csvParser)>

Original Data
=============================================================================
${tools.dataframe.print(users)}

Select By Age
=============================================================================
${tools.dataframe.print(users.select("(age > 40)"))}

Select By Name & Country
=============================================================================
<#assign country = "Germany">
${tools.dataframe.print(users
.select("(name == 'Schmitt' || name == 'Meier') && country == '${country}'")
.sort("name", tools.dataframe.sortOrder.ASCENDING))}

Head of Users
=============================================================================
${tools.dataframe.print(users.head(2))}

Count Column Values
=============================================================================
${tools.dataframe.print(users.getColumn("country").transform(tools.dataframe.transformer.COUNT))}

Group By Age & Country
=============================================================================
${tools.dataframe.print(users.groupBy("country", "age").sort("country"))}
