//submit do formulario de cadastro de promocoes para o controller
$("#form-add-categoria").submit(function (evt) {
    evt.preventDefault();

    var categoria = {};
    categoria.nome = $("#nome").val();
    console.log('promo >', categoria);

    $.ajax({
        method: "POST",
        url: "/categoria/save",
        data: categoria,
        beforeSend: function () {
            //remover os spans
            $("span").closest('.error-span').remove();

            //remover as bordas vermelhas
            $("#nome").removeClass("is-invalid");

            //imagem de loading
            $("#form-add-categoria").hide();
            $("#loader-box").addClass("pre-load").show();
            $("#loader-form").addClass("loader").show();
        },
        success: function () {
            $("#form-add-categoria").each(function() {
                this.reset();
            });
            $("#alert")
                .removeClass("alert alert-danger")
                .addClass("alert alert-success")
                .text("Categoria cadastrada com sucesso.");
        },
        statusCode: {
            422: function (xhr) {
                console.log('status error: ', xhr.status);
                var errors = $.parseJSON(xhr.responseText);
                $.each(errors, function (key, val) {
                    $("#" + key).addClass("is-invalid");
                    $("#error-" + key)
                        .addClass("invalid-feedback")
                        .append("<span class='error-span'>" + val + "</span>");
                })
            },
            400: function (xhr) {
                console.log('status error: ',  xhr.status);
                $("#nome").addClass("is-invalid");
                $("#error-nome").addClass("invalid-feedback").append("<span class='error-span'>Categoria já existe!</span>");
            }
        },
        error: function (xhr) {
            console.log("> error: ", xhr.responseText);
            $("#alert").addClass("alert alert-danger").text("Não foi possível salvar esta Categoria.");
        },
        complete: function () {
            $("#loader-form").fadeOut(800, function () {
                $("#form-add-categoria").fadeIn(250);
                $("#loader-box").removeClass("pre-load");
                $("#loader-form").removeClass("loader");
            });
        }
    })
});