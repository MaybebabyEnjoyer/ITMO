<#-- @ftlvariable name="bodyItems" type="ru.itmo.web.hw4.model.User[]" -->
<#import "./common.ftlh" as c>

<@c.cssLinks>
    <link rel="stylesheet" href="/css/table.css">
</@c.cssLinks>

<@c.page>
    <div class="datatable">
        <div class="caption">Users</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Handle</th>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <#list users as user>
                <tr>
                    <td>${user.id}</td>
                    <td><@c.userlink user/></td>
                    <td>${user.name}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>