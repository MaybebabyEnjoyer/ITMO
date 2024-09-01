<#-- @ftlvariable name="user_id" type="java.lang.Number" -->
<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="u" type="ru.itmo.web.hw4.model.User" -->
<#import "./common.ftlh" as c>

<@c.cssLinks>
    <link rel="stylesheet" href="/css/user.css">
</@c.cssLinks>

<@c.page>
    <#if handle?? && handle?is_string>
        <#assign u = c.findBy(users, "handle", handle)!/>
    <#elseif user_id?? && user_id?is_number>
        <#assign u = c.findBy(users, "id", user_id)!/>
    <#else>
        <#assign u = "">
    </#if>

    <#if u?has_content>
        <div class="user-page">
            <a href="#" class="user-img"><img alt="User" title="User"
                                              src="/img/user-image.jpg"></a>
            <div class="username">
                <@c.userlink u/>
            </div>
            <div class="user-information">
                <ul>
                    <#assign fields = ["id", "handle", "name"]/>
                    <#list fields as field>
                        <li>${field}: ${u[field]}</li>
                    </#list>
                    <#assign count = c.postsCount(u)/>
                    <li>posts count: <@c.userPostslink u count/></li>
                </ul>
            </div>
        </div>
        <div class="links">
            <@userIter u=u step=-1 text="⟵"/>
            <@userIter u=u step=1 text="⟶"/>
        </div>
        <#assign index =0/>
        <#list users as user>
            <#if user == u>
                <#assign index = user_index + 1/>
            </#if>
        </#list>
    <#else>
        No such user
    </#if>
</@c.page>

<#macro userIter u step text>
    <#assign index = 0/>
    <#list users as user>
        <#if user == u>
            <#assign index = user_index + step/>
            <#if index == -1 || index == users?size>
                ${text}
            <#else>
                <@c.userlink users[index] text true/>
            </#if>
        </#if>
    </#list>
</#macro>
