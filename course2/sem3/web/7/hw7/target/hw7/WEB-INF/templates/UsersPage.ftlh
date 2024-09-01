<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if user?? && user.admin>
                    <th>Admin</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user-login"></td>
                    <#if user?? && user.admin>
                        <td>
                            <button class="button"></button>
                        </td>
                    </#if>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");

            ajax({action: "findAll"},
                function (response) {
                    response["users"].forEach(function(user) {
                        $noData.hide();
                        const $tr = $($(".users template").prop("content")).clone();

                        $tr.find(".user-id").text(user["id"]);
                        $tr.find(".user-login").text(user["login"]);
                        $tr.find("tr").attr("data-userId", user["id"]);

                        const $button = $tr.find(".button");
                        $button.attr("data-id", user["id"]);
                        $button.attr("data-bool", user["admin"]);
                        if (user["admin"]) {
                            $button.text("disable");
                        } else {
                            $button.text("enable");
                        }

                        $(".users tbody").append($tr);
                    });
                    $(".users tr").click(function() {
                        ajax({
                                action: "findUser",
                                userId: $(this).attr("data-userId")
                            },
                            function (response) {
                                notify(response.user.creationTime);
                            });
                    });
                    ajaxButton($(".users .button"), "disable", "enable");
                }
            );
        })
    </script>
</@c.page>
