var api = APIs.createNew();

$(document).on("click", ".moreBug", function(e){
    e.preventDefault();

    var btn = $(e.target);
    btn.removeClass("moreBug");
    btn.removeClass("btn-success");
    btn.addClass("lessBug");
    btn.addClass("btn-outline-danger");
    btn.html("-");

    var bugIDs = $("#bugIDs");
    var newDiv = $("<div class='form-inline input-group bug'></div>");
    var newInputBugID = "<input type='text' class='form-control' id='bugID' placeholder='Bug ID'/>";
    var newInputFixBranchName = "<input type='text' class='form-control' id='fixBranchName' placeholder='Fix Branch Name'/>";
    var newInputFixRevisionID = "<input type='text' class='form-control' id='fixRevisionID' placeholder='Fix Revision ID'/>";
    var newInputBugComment = "<input type='text' class='form-control' id='bugComment' placeholder='Comment'/>";
    var newButton = "<button class='input-group-addon moreBug btn btn-success' style='width: 40px;'>+</button>";

    newDiv.append($(newInputBugID));
    newDiv.append($(newInputFixBranchName));
    newDiv.append($(newInputFixRevisionID));
    newDiv.append($(newInputBugComment));
    newDiv.append($(newButton));
    bugIDs.append(newDiv);
});

$(document).on("click", ".lessBug", function (e) {
    e.preventDefault();
    $(e.target).parent().remove();
})

function postChangeOrderSuccess(data, textStatus, jqXHR) {
    window.location.replace(api.hostURL + api.GUI_CHANGE_ORDER + "?begin=0&end=" + api.entriesPerPage);
}

$(document).on("click", ".submit", function (e) {
    e.preventDefault();

    var hasError = false;
    for (var i = 0; i < $(".required").length; i++) {
        var required = $(".required")[i];

        $(required).css("border-color", "");

        if (required.value == "") {
            $(required).css("border-color", "red");
            hasError = true;
        }
    }

    var requiredGroup0 = false;
    for (var i = 0; i < $(".requiredGroup0").length; i++) {
        var required = $(".requiredGroup0")[i];

        $(required).css("border-color", "");

        if (required.value == "") {
            $(required).css("border-color", "red");
            requiredGroup0 = true;
        }
    }

    var requiredGroup1 = false;
    for (var i = 0; i < $(".requiredGroup1").length; i++) {
        var required = $(".requiredGroup1")[i];

        $(required).css("border-color", "");

        if (required.value == "") {
            $(required).css("border-color", "red");
            requiredGroup1 = true;
        }
    }

    if (requiredGroup0 == true && requiredGroup1 == true) {
        hasError = true;
        showSnackBar("(Bug ID) or (Fix Branch Name & Fix Revision ID) must not be empty");
    }

    if (hasError) {
        return;
    }

    var param = {};
    param["id"] = $("#changeOrderID")[0].value;
    param["branchName"] = $("#branchName")[0].value;
    param["author"] = $("#author")[0].value;
    param["time"] = $("#time")[0].value;
    param["data"] = {}
    param["data"]["comment"] = $("#comment")[0].value;
    param["data"]["bugs"] = [];

    var bugs = $(".bug")
    var cnt = 0;
    for (var i = 0; i < bugs.length; i++) {
        if ($(bugs[i]).find("#bugID")[0].value == ""
            && ($(bugs[i]).find("#fixBranchName")[0].value == ""
            || $(bugs[i]).find("#fixRevisionID")[0].value == "")) {
            continue;
        }

        param["data"]["bugs"][cnt] = {};
        param["data"]["bugs"][cnt]["id"] = $(bugs[i]).find("#bugID")[0].value;
        param["data"]["bugs"][cnt]["branchName"] = $(bugs[i]).find("#fixBranchName")[0].value;
        param["data"]["bugs"][cnt]["revisionID"] = $(bugs[i]).find("#fixRevisionID")[0].value;
        param["data"]["bugs"][cnt]["comment"] = $(bugs[i]).find("#bugComment")[0].value;

        cnt++;
    }

    api.setCallbacks(postChangeOrderSuccess, null);
    api.postChangeOrder(param["changeOrderID"], param);
})

function showSnackBar(message) {
    var snackbar = $("#snackBar");
    if (snackbar.length > 0) {

        if (message.length > 0) {
            snackbar.html(message);
        }

        snackbar.addClass("show");
        setTimeout(function () {
            snackbar.removeClass("show");
        }, 3000);
    }
}
