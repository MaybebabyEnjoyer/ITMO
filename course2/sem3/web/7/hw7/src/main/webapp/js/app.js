window.notify = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "info"
    });
}

window.ajax = function (data, successFunc, $error = null) {
    $.ajax({
        type: "POST",
        dataType: "json",
        data,
        success: function (response) {
            successFunc(response);
            if (response.error) {
                $error.text(response.error)
            }
            if (response.redirect) {
                location.href = response.redirect
            }
        }
    })
}

window.ajaxButton = function (button, ifTrue, ifFalse) {
    button.click(function () {
        const $button = $(this);
        ajax(
            {
                action: "change",
                id: $(this).attr("data-id"),
                boolValue: $(this).attr("data-bool")
            },
            function (response) {
                $button.attr("data-bool", response.bool);
                if (response.bool) {
                    $button.text(ifTrue);
                } else {
                    $button.text(ifFalse);
                }
            }
        );
    });
}
