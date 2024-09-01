<#-- @ftlvariable name="post_id" type="java.lang.Number" -->
<#-- @ftlvariable name="p" type="ru.itmo.web.hw4.model.Post" -->
<#import "./common.ftlh" as c>

<@c.cssLinks>
    <link rel="stylesheet" href="/css/blog.css">
</@c.cssLinks>

<@c.page>
    <#if post_id?? && post_id?is_number>
        <#assign p = c.findBy(posts, "id", post_id)!/>
    <#else>
        <#assign p = ""/>
    </#if>

    <#if p?has_content>
        <@c.blog p false/>
    <#else>
        No such post
    </#if>
</@c.page>