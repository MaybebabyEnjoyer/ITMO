<#-- @ftlvariable name="userCount" type="java.lang.Number" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#-- @ftlvariable name="message" type="java.lang.String" -->
<#macro page>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Codeforces</title>
    <link rel="stylesheet" type="text/css" href="css/normalize.css">
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <link rel="stylesheet" type="text/css" href="css/form.css">
    <link rel="stylesheet" type="text/css" href="css/datatable.css">
</head>
<body>
<header>
    <a href="/index"><img src="img/logo.png" alt="Codeforces" title="Codeforces"/></a>
    <div class="languages">
        <a href="#"><img src="img/gb.png" alt="In English" title="In English"/></a>
        <a href="#"><img src="img/ru.png" alt="In Russian" title="In Russian"/></a>
    </div>
    <div class="enter-or-register-box">
        <#if user??>
            ${user.login}
            |
            <a href="/logout">Logout</a>
        <#else>
            <a href="/enter">Enter</a>
            |
            <a href="/register">Register</a>
        </#if>
    </div>
    <nav>
        <ul>
            <li><a href="/index">Home</a></li>
            <li><a href="/users">Users</a></li>
            <li><a href="/talks">Talks</a></li>
        </ul>
    </nav>
</header>
<#if message??>
    <div class="message">${message}</div>
</#if>
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
    <a href="/index">Codeforces</a> &copy; 2022 by Mike Mirzayanov
    <div>${userCount} users</div>
</footer>
</body>
</html>
</#macro>
