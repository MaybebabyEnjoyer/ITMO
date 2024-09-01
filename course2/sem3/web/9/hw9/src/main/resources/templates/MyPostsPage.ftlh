<#import "commons.ftlh" as c>

<@c.page>
<#list user.posts as post>
    <@c.post post=post/>
</#list>
</@c.page>
