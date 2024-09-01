<#-- @ftlvariable name="link" type="java.lang.String" -->
<#-- @ftlvariable name="currentMenuItem" type="java.lang.String" -->
<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <@cssLinks/>
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro cssLinks>
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/style.css">
    <#nested/>
    <link rel="icon" href="/favicon.ico">
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user=user nameOnly=true/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href": "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li><a <#if link?? && link == item.href>class="current-item" </#if>
                           href="${item.href}">${item.name}</a></li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#macro sidebar>
    <aside>
        <#list posts as post>
            <section>
                <div class="header">Post ${post.id}</div>
                <div class="body"><@trimText post/></div>
                <div class="footer"><@postlink post/></div>
            </section>
        </#list>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2021 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#macro trimText post needTrim=true>
<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
    <#assign text = post.text/>
    <#if needTrim && text?length gt 350>
        ${text?substring(0, 350)}...
    <#else>
        ${text}
    </#if>
</#macro>

<#macro blog post needTrim=true>
<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
    <article>
        <div class="title">${post.title}</div>
        <#assign user = findBy(users, "id", post.userId).name!"No such user"/>
        <div class="information">By ${user}</div>
        <div class="body">
            <@trimText post needTrim/>
        </div>
    </article>
</#macro>

<#macro blogSeq posts>
<#-- @ftlvariable name="posts" type="ru.itmo.web.hw4.model.Post[]" -->
    <#list posts?reverse as post>
        <@blog post/>
    </#list>
</#macro>

<#macro userlink user text=user.name nameOnly=false>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <a href="/user?handle=${user.handle}"
       <#if !nameOnly>style="color: ${user.color}" class="user-handle"</#if>>${text}
    </a>
</#macro>

<#macro postlink post>
<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
    <a href="/post?post_id=${post.id}">View all</a>
</#macro>

<#macro userPostslink user text>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <a href="/posts?user_id=${user.id}">${text}</a>
</#macro>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>

<#function listFindBy items key value>
    <#assign result = []/>
    <#list items as item>
        <#if item[key] == value>
            <#assign result = result + [ item ]/>
        </#if>
    </#list>
    <#return result/>
</#function>

<#function postsCount user>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <#assign count = 0/>
    <#list posts as post>
        <#if post.userId == user.id>
            <#assign count++/>
        </#if>
    </#list>
    <#return count/>
</#function>