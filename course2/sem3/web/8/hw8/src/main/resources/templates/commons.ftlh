<#import "/spring.ftl" as spring>

<#macro page>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" type="text/css" href="<@spring.url '/css/normalize.css'/>">
        <link rel="stylesheet" type="text/css" href="<@spring.url '/css/style.css'/>">
        <link rel="stylesheet" type="text/css" href="<@spring.url '/css/form.css'/>">
        <link rel="stylesheet" type="text/css" href="<@spring.url '/css/datatable.css'/>">
        <script src="<@spring.url '/js/jquery-3.6.1.min.js'/>"></script>
        <script src="<@spring.url '/js/notify.min.js'/>"></script>
        <script src="<@spring.url '/js/app.js'/>"></script>
    </head>
    <body>
    <header>
        <a href="/"><img src="<@spring.url '/img/logo.png'/>" alt="Codeforces" title="Codeforces"/></a>
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
                <li><a href="<@spring.url '/notice'/>">Notice</a></li>
            </ul>
        </nav>
    </header>
    <div class="middle">
        <aside>
            <#list notices as notice>
                <section>
                    <div class="header">Pay attention</div>
                    <div class="body">
                        <#list notice.content?split("\n") as n>
                            <p>${n}</p>
                        </#list>
                    </div>
                    <div class="footer">
                        <a href="#">View all</a>
                    </div>
                </section>
            </#list>
        </aside>
        <main>
            <#nested/>
        </main>
    </div>
    <footer>
        <a href="/">Ejudge</a> &copy; 2077 by Mike Mirzayanov
    </footer>
    <script>
        $(function () {
            <#if message??>
                notify("${message?js_string}");
            </#if>
        })
    </script>
    </body>
    </html>
</#macro>

<#macro error field>
    <@spring.bind field/>
    <#if spring.status.errorMessage??>
        <div class="error">${spring.status.errorMessage}</div>
    </#if>
</#macro>