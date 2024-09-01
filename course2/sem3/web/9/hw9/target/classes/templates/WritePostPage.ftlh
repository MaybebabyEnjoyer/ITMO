<#-- @ftlvariable name="post" type="ru.itmo.wp.domain.Post" -->

<#import "/spring.ftl" as spring>
<#import "commons.ftlh" as c>

<@c.page>
    <div class="form">
        <div class="header">Write Post</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="writePost"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Title</label>
                    </div>
                    <div class="value">
                        <input autofocus id="title" name="title" value="${post.title!}"/>
                    </div>
                    <@c.error "post.title"/>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Text</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text">${post.text!}</textarea>
                    </div>
                    <@c.error "post.text"/>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="names">Tags</label>
                    </div>
                    <div class="value">
                        <textarea id="names" name="names">${tagNames.names!}</textarea>
                    </div>
                    <@c.error "tagNames.names"/>
                </div>
                <div class="button-field">
                    <input type="submit" value="Write">
                </div>
            </form>
        </div>
    </div>
</@c.page>
