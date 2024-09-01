<#-- @ftlvariable name="articles" type="ru.itmo.wp.model.domain.Article[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <#if articles?size == 0>
        <div>You have no articles</div>
    <#else>
        <div class="myArticles datatable">
            <div class="caption">My Articles</div>
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Title</th>
                    <th>Hide/Show</th>
                </tr>
                </thead>
                <tbody>
                <#list articles as article>
                    <tr>
                        <td>${article.id}</td>
                        <td>${article.title}</td>
                        <td>
                            <button type="button"
                                    class="button"
                                    data-id="${article.id}"
                                    data-bool="${article.hidden?string}">
                                <#if article.hidden>Show<#else>Hide</#if>
                            </button>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </#if>

    <script>
        $(function () {
            ajaxButton($(".myArticles .button"), "Show", "Hide");
        });
    </script>
</@c.page>