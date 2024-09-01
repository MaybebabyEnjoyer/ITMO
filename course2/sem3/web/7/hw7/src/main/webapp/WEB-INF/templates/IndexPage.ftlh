<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles">
        <template>
            <article>
                <div class="title"></div>
                <div class="information"></div>
                <div class="body"></div>
            </article>
        </template>
        <div class="noData">No data</div>
    </div>

    <script>
        $(function () {
            const $noData = $(".articles .noData");
            $noData.text("Loading...");
            
            ajax({action: "findAll"},
            function (response) {
                if (response["articles"].length === 0) {
                    $noData.text("No articles");
                    return false;
                }
                response["articles"].forEach(function (article) {
                    $noData.hide();
                    const $art = $($(".articles template").prop("content")).clone();
                    $art.find(".title").text(article["title"]);
                    $art.find(".body").html("<p>" + article["text"].replaceAll("\n", "</p><p>") + "</p>");

                    const $info = $art.find(".information");
                    ajax({action: "findUser", userId: article["userId"]},
                        function (response) {
                            $info.text("By " + response.user.login);
                        });

                    $(document).ajaxStop(function () {
                        $(".articles").append($art);
                    })
                })
            });
        })
    </script>
</@c.page>
