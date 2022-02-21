$(document).ready(function () {
    var table = $("#table-categoria-server").DataTable({
        processing: true,
        serverSide: true,
        responsive: true,
        lengthMenu: [10, 50],
        ajax: {
            url: "/categoria/datatables/server",
            data: "data"
        },
        columns: [
            {data: 'id'},
            {data: 'nome'},
            {data: 'numeroPromocoes'},

        ],
        dom: 'Blfrtip',
        buttons: [
            {
                text: 'Editar',
                attr: {
                    id: 'btn-editar-cat',
                    type: 'button',
                    class: 'btn btn-primary'
                },
                enabled: false
            },
            {
                text: 'Remover',
                attr: {
                    id: 'btn-remover-cat',
                    type: 'button',
                    class: 'btn btn-danger me-3'
                },
                enabled: false
            }
        ],
    });

    table.buttons().disable();

    $("#table-categoria-server thead").on("click", "tr", function () {
        table.buttons().disable();
    });

    $("#table-categoria-server").on("click", "tr", function () {
        if($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            table.buttons().disable();
        } else {
            $('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            table.buttons().enable();
        }
    });

    //abre o modal editar
    $("#btn-editar-cat").on("click", function () {
        if (isSelectedRow()) {
            $("#alert").removeClass("alert alert-success");
            var id = getCategoriaId();
            console.log(id);
            $.ajax({
                method: "GET",
                url: "/categoria/edit/" + id,
                beforeSend: function () {
                    $("span").closest('.error-span').remove();
                    $(".is-invalid").removeClass("is-invalid");
                    $("#modal-form-categoria").modal('show');
                },
                success: function (data) {
                    $("#edt_id").val(data.id);
                    $("#edt_nome").val(data.nome);
                    $("#edt_numeroPromocoes").val(data.numeroPromocoes);
                    $("#alert").removeClass("alert alert-success").text("");
                },
                error: function (xhr) {
                    alert("Ops, ocorreu um erro, tente novamente mais tarde. > " + xhr.status + " : " + xhr.responseText);
                },
            });
        }
    });

    $("#btn-edit-modal-categoria").on("click", function () {
        var categoria = {};
        categoria.id = $("#edt_id").val();
        categoria.nome = $("#edt_nome").val();
        categoria.numeroPromocoes = $("#edt_numeroPromocoes").val();

        $.ajax({
            method: "POST",
            url: "/categoria/edit",
            data: categoria,
            beforeSend: function () {
                $("span").closest('.error-span').remove();
                $(".is-invalid").removeClass("is-invalid");

                //loader > esconde o conteúdo e mostra o loader
                $("#tabela-categoria").hide();
                $("#loader-box").addClass("pre-load").show();
                $("#loader-form").addClass("loader").show();
            },
            success: function () {
                $("#modal-form-categoria").modal('hide');
                table.ajax.reload();
                $("#alert")
                    .removeClass("alert alert-danger")
                    .addClass("alert alert-success")
                    .text("Categoria editada com sucesso.")
                desabilitarBotoes();
            },
            statusCode: {
                422: function (xhr) {
                    console.log('status error:', xhr.status);
                    var errors = $.parseJSON(xhr.responseText);
                    $.each(errors, function (key, val) {
                        $("#edt_" + key).addClass("is-invalid");
                        $("#error-" + key)
                            .addClass("invalid-feedback")
                            .append("<span class='error-span'>" + val + "</span>");
                    })
                }
            },
            complete: function (){
                //mostra o conteúdo denovo e esconde o loader
                $("#loader-form").fadeOut(800, function () {
                    $("#tabela-categoria").fadeIn(250);
                    $("#loader-box").removeClass("pre-load");
                    $("#loader-form").removeClass("loader");
                });
            }
        });

    });

    $("#btn-remover-cat").on("click", function () {
        if (isSelectedRow()) {
            $("span").closest('.error-span-cat').remove();
            $("#modal-delete-categoria").modal('show');
            $("#alert").removeClass("alert alert-success").text("");
        }
    });

    $("#btn-del-modal-categoria").on("click", function () {
        var id = getCategoriaId();
        $.ajax({
            method: "GET",
            url: "/categoria/delete/" + id,
            beforeSend: function () {
                $("span").closest('.error-span-cat').remove();

                //loader > esconde o conteúdo e mostra o loader
                $("#tabela-categoria").hide();
                $("#loader-box").addClass("pre-load").show();
                $("#loader-form").addClass("loader").show();
            },
            success: function () {
                $("#modal-delete-categoria").modal("hide");
                table.ajax.reload();
                $("#alert")
                    .removeClass("alert alert-danger")
                    .addClass("alert alert-success")
                    .text("Categoria removida com sucesso.")
                desabilitarBotoes();
            },
            statusCode: {
                422: function () {
                    $("#error-del-cat")
                        .addClass("text-danger")
                        .append("<span class='error-span-cat'><h6>> Há Promoções associadas à Categoria.</h6></span>");
                }
            },
            complete: function () {
                //mostra o conteúdo denovo e esconde o loader
                $("#loader-form").fadeOut(800, function () {
                    $("#tabela-categoria").fadeIn(250);
                    $("#loader-box").removeClass("pre-load");
                    $("#loader-form").removeClass("loader");
                });
            }
        });
    });


    function getCategoriaId() {
        return table.row(table.$('tr.selected')).data().id;
    }

    function isSelectedRow() {
        var trow = table.row(table.$('tr.selected'));
        return trow.data() != undefined;
    }

    function desabilitarBotoes() {
        table.buttons().disable();
    }
});