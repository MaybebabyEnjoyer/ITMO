<#-- @ftlvariable name="post" type="ru.itmo.wp.domain.Post" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.domain.User" -->
<#-- @ftlvariable name="message" type="java.lang.String" -->
<#import "/spring.ftl" as spring/>
<#macro page>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Codeforces</title>
    <link rel="icon" type="image/png" href="<@spring.url '/favicon.png'/>" />
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/normalize.css'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/style.css'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/form.css'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/datatable.css'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/article.css'/>">
    <script src="<@spring.url '/js/jquery-3.4.1.min.js'/>"></script>
    <script src="<@spring.url '/js/notify.js'/>"></script>
</head>
<body>
<header>
    <a href="<@spring.url '/'/>"><img src="<@spring.url '/img/logo.png'/>" alt="Codeforces" title="Codeforces"/></a>
    <div class="languages">
        <a href="#"><img src="<@spring.url '/img/gb.png'/>" alt="In English" title="In English"/></a>
        <a href="#"><img src="<@spring.url '/img/ru.png'/>" alt="In Russian" title="In Russian"/></a>
    </div>
    <div class="enter-or-register-box">
        <#if user??>
            ${user.login}
            |
            <a href="<@spring.url '/logout'/>">Logout</a>
        <#else>
            <a href="<@spring.url '/enter'/>">Enter</a>
            |
            <a href="<@spring.url '/register'/>">Register</a>
        </#if>
    </div>
    <nav>
        <ul>
            <li><a href="<@spring.url '/'/>">Home</a></li>
            <li><a href="<@spring.url '/users/all'/>">Users</a></li>
            <#if user??>
                <li><a href="<@spring.url '/writePost'/>">Write Post</a></li>
                <li><a href="<@spring.url '/myPosts'/>">Posts</a></li>
            </#if>
        </ul>
    </nav>
</header>
<div class="middle">
    <aside>
        <section>
            <div class="header">
                Pay attention
            </div>
            <div class="body">
                Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cupiditate ducimus enim facere impedit nobis,
                nulla placeat quam suscipit unde voluptatibus.
            </div>
            <div class="footer">
                <a href="#">View all</a>
            </div>
        </section>
    </aside>
    <main>
        <#nested/>
    </main>
</div>
<footer>
    <a href="<@spring.url '/'/>">Codehorses</a> &copy; 2099 by Mike Mirzayanov
</footer>
</body>
<script>
    <#--noinspection JSUnresolvedFunction-->
    $(function () {
        <#if message??>
            $.notify("${message?js_string}", {className: "success", position: "bottom right"});
        </#if>
    })
</script>
</html>
</#macro>

<#macro error field>
    <@spring.bind field/>
    <#if spring.status.errorMessage??>
        <div class="error">${spring.status.errorMessage}</div>
    </#if>
</#macro>

<#macro post post>
<#-- @ftlvariable name="post" type="ru.itmo.wp.domain.Post" -->
    <article>
        <div class="title"><a href="<@spring.url '/post/${post.id}'/>">${post.title}</a></div>
        <div class="information">By ${post.user.login}, ${post.creationTime}</div>
        <div class="tags">
            <#list post.tags as tag>
                ${tag.name}
            </#list>
        </div>
        <div class="body">${post.text}</div>
        <ul class="attachment">
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 1)</a></li>
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 2)</a></li>
        </ul>
        <div class="footer">
            <div class="left">
                <img src="<@spring.url '/img/voteup.png'/>" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="<@spring.url '/img/votedown.png'/>" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="<@spring.url '/img/date_16x16.png'/>" title="Publish Time" alt="Publish Time"/>
                ${post.creationTime}
                <img src="<@spring.url '/img/comments_16x16.png'/>" title="Comments" alt="Comments"/>
                <a href="#">68</a>
            </div>
        </div>
    </article>
</#macro>

<#macro comment comment>
<#-- @ftlvariable name="comment" type="ru.itmo.wp.domain.Comment" -->
    <article>
        <div class="information">By ${comment.user.login}, ${comment.creationTime}</div>
        <div class="body">${comment.text}</div>
    </article>
</#macro>