<#import "commons.ftlh" as c>

<@c.page>
    <div class="article form-box">
        <div class="header">New article</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="register"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Enter title</label>
                    </div>
                    <div class="value">
                        <input id="title" name="title"/>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Enter your text</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text" rows="5"></textarea>
                    </div>
                </div>
                <div class="error"></div>
                <div class="button-field">
                    <input type="submit" value="Create">
                </div>
            </form>
        </div>
    </div>

    <script>
        $(function (){
            $(".article form").submit(function () {
                const title = $(this).find("input[name='title']").val();
                const text = $(this).find("textarea[name='text']").val();
                const $error = $(this).find(".error");

                ajax({
                    action: "create",
                    title,
                    text
                },
                    function (response){},
                    $error
                );

                return false;
            })
        })
    </script>
</@c.page>
