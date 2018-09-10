/**
 * Created by neo on 9/9/18.
 */
var api = APIs.createNew();

$(document).on("click", ".BtnDelete", function (e) {
    var btn = $(e.target);
    var tr = btn.parent().parent().parent();
    var id = tr.find(".ChangeOrderID")[0].value;

    api.setDeleteChangeOrderSuccess(function (data, textStatus, jqXHR) {
        for (var i = 0; i < $("." + id).length; i++) {
            $("." + id).remove();
        }
    });

    api.deleteChangeOrder(id);
});
