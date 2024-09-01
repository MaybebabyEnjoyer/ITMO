<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <div>${user.id}</div>
        <div>${user.login}</div>
        <div>${user.creationTime}</div>
    <#else>
        No such user
    </#if>
</@c.page>