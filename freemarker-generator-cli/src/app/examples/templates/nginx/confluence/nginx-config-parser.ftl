<#compress>
    || FILE || SERVER || ACCESSLOG || SPLUNK ||
    <#list dataSources as dataSource>
        <#assign fileName = dataSource.fileName>
        <#assign serverName = "N.A">
        <#-- Transform to a single line to avoid matching OS-specific line endings -->
        <#assign text = dataSource.getText()?replace("\r", "")?replace("\n", " ")>
        <#assign accessLog = getAccessLog(text)>
        <#assign serverName = getServerName(text)>
        | ${fileName} | ${serverName} | ${accessLog} | [${splunkSearchUrl(accessLog)}] |
    </#list>
</#compress>
<#--------------------------------------------------------------------------->
<#function splunkSearchUrl accessLog>
    <#return "https://splunk.p.santanderconsumer.at/en-US/app/scbdevteam/search?q=search%20source%3D%22${accessLog?url}%22">
</#function>
<#--------------------------------------------------------------------------->
<#function getAccessLog text>
    <#assign matches = text?matches(r".*access_log\s*([\w\.\-\/\\]*);.*")>
    <#if matches>
        <#return matches?groups[1]?trim>
    <#else>
        <#return "N.A.">
    </#if>
</#function>
<#--------------------------------------------------------------------------->
<#function getServerName text>
    <#assign matches = text?matches(r".*server_name\s*([\w\.\-\\]*);.*")>
    <#if matches>
        <#return matches?groups[1]?trim>
    <#else>
        <#return "N.A.">
    </#if>
</#function>