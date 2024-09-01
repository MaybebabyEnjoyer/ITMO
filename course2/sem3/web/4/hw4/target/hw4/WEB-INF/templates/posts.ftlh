<#-- @ftlvariable name="user_id" type="java.lang.Number" -->
<#-- @ftlvariable name="p" type="ru.itmo.web.hw4.model.Post[]" -->
<#import "./common.ftlh" as c>

<@c.cssLinks>
    <link rel="stylesheet" href="/css/blog.css">
</@c.cssLinks>

<@c.page>
    <#if user_id?? && user_id?is_number>
        <#assign p = c.listFindBy(posts, "userId", user_id)!/>
    <#else>
        <#assign p = ""/>
    </#if>

    <#if p?has_content>
        <@c.blogSeq p/>
    <#else>
        No such page
    </#if>
</@c.page>