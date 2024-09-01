<#-- @ftlvariable name="posts" type="ru.itmo.wp.domain.Post[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <#if post??>
        <@c.post post=post/>
        <#if user??>
            <div class="form">
                <div class="header">Write Comment</div>
                <div class="body">
                    <form method="post" action="">
                        <input type="hidden" name="action" value="createComment"/>
                        <div class="field">
                            <div class="name">
                                <label for="text">Comment</label>
                            </div>
                            <div class="value">
                                <textarea id="text" name="text"></textarea>
                            </div>
                            <@c.error "comment.text"/>
                        </div>
                        <div class="button-field">
                            <input type="submit" value="Publish">
                        </div>
                    </form>
                </div>
            </div>
        </#if>
        <#list post.comments as com>
            <@c.comment comment=com/>
        </#list>
    <#else>
        No such post.
    </#if>
</@c.page>
