$(document).ready(function () {
    moment.locale('pt-br');
    var table = $("#table-server").DataTable({
        processing: true,
        serverSide: true,
        responsive: true,
        lengthMenu: [10, 25, 50, 100],
        ajax: {
            url: "/promocao/datatables/server",
            data: "data"
        },
        columns: [
            {data: 'id'},
            {data: 'titulo'},
            {data: 'site'},
            {data: 'preco', render: $.fn.dataTable.render.number('.', ',', 2, 'R$ ')},
            {data: 'likes'},
            {data: 'dtCadastro', render: function (dtCadastro) {
                    return moment(dtCadastro).format('LLL');
            }},
            {data: 'categoria.nome'},
            {data: 'descricao'},
            {data: 'linkPromocao'},
            {data: 'linkImagem'}
        ],
        dom: 'Blfrtip',
        buttons: [
            {
                text: 'Editar',
                attr: {
                    id: 'btn-editar',
                    type: 'button',
                    class: 'btn btn-primary'
                },
                enabled: false
            },
            {
                text: 'Remover',
                attr: {
                    id: 'btn-excluir',
                    type: 'button',
                    class: 'btn btn-danger me-3'

                },
                enabled: false
            }
        ]
    });
    //disabilita os botoes ao carregar a página pela primeira vez
    desabilitarBotoes();

    $("#table-server thead").on("click", 'tr', function () {
        desabilitarBotoes();
    });

    $("#table-server tbody").on("click", 'tr', function () {
        if($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            desabilitarBotoes();
        } else {
            $('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            table.buttons().enable();
        }
    });


    //abrir modal editar
    $("#btn-editar").on("click", function () {

        if (isSelectedRow()) {
            var id = getPromoId();
            $.ajax({
                method: "GET",
                url: "/promocao/edit/" + id,
                beforeSend: function () {
                    $("span").closest('.error-span').remove();
                    $(".is-invalid").removeClass("is-invalid");
                    $("#modal-form").modal('show');

                },
                success: function (data) {
                    $("#edt_id").val(data.id);
                    $("#edt_site").text(data.site);
                    $("#edt_titulo").val(data.titulo);
                    $("#edt_descricao").val(data.descricao);
                    $("#edt_preco").val(data.preco.toLocaleString('pt-BR', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }));
                    $("#edt_categoria").val(data.categoria.id);
                    $("#edt_linkImagem").val(data.linkImagem);
                    $("#edt_imagem").attr('src', data.linkImagem);
                },
                error: function () {
                    alert("Ops, ocorreu um erro, tente novamente mais tarde.");
                },
            });

        }

    });

    //submit do form edit
    $("#btn-edit-modal").on("click", function () {
        var promo = {};
        promo.id = $("#edt_id").val();
        promo.descricao = $("#edt_descricao").val();
        promo.preco = $("#edt_preco").val();
        promo.titulo = $("#edt_titulo").val();
        promo.categoria = $("#edt_categoria").val();
        promo.linkImagem = $("#edt_linkImagem").val();

        $.ajax({
            method: "POST",
            url: "/promocao/edit",
            data: promo,
            beforeSend: function () {
                $("span").closest('.error-span').remove();
                $(".is-invalid").removeClass("is-invalid");

                //loader
                $("#tabela-promocoes").hide();
                $("#loader-box").addClass("pre-load").show();
                $("#loader-form").addClass("loader").show();
            },
            success: function () {
                $("#modal-form").modal('hide');
                table.ajax.reload();
                $("#alert")
                    .removeClass("alert alert-danger")
                    .addClass("alert alert-success")
                    .text("Promoção editada com sucesso.")
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
            error: function (xhr) {
                console.log("> error: ", xhr.responseText);
                $("#alert").addClass("alert alert-danger").text("Não foi possível Editar esta promoção.");
            },
            complete: function () {
                $("#loader-form").fadeOut(800, function () {
                    table.ajax.reload();
                    $("#tabela-promocoes").fadeIn(250);
                    $("#loader-box").removeClass("pre-load");
                    $("#loader-form").removeClass("loader");
                });
            }
        });

    });

    //alterar a imagem edit
    $("#edt_linkImagem").on("change", function () {
        var link = $(this).val();
        $("#edt_imagem").attr("src", link);
    });

    //abrir modal remover
    $("#btn-excluir").on("click", function () {
        if (isSelectedRow()) {
            $("#modal-delete").modal('show');
        }
    });

    //remover promocao
    $("#btn-del-modal").on("click", function () {
        var id = getPromoId();
        $.ajax({
            method: "GET",
            url: "/promocao/delete/" + id,
            beforeSend: function () {
                //loader
                $("#tabela-promocoes").hide();
                $("#loader-box").addClass("pre-load").show();
                $("#loader-form").addClass("loader").show();
            },
            success: function () {
                $("#modal-delete").modal('hide');
                table.ajax.reload();
                $("#alert")
                    .removeClass("alert alert-danger")
                    .addClass("alert alert-success")
                    .text("Promoção removida com sucesso.")
                desabilitarBotoes();
            },
            error: function () {
                alert("Ops, ocorreu um erro, tente mais tarde!");
            },
            complete: function () {
                $("#loader-form").fadeOut(800, function () {
                    table.ajax.reload();
                    $("#tabela-promocoes").fadeIn(250);
                    $("#loader-box").removeClass("pre-load");
                    $("#loader-form").removeClass("loader");
                });
            }
        });
    });


    function getPromoId() {
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

