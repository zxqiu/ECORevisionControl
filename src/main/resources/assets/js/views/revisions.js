var api = APIs.createNew();

$(document).on("click", ".BtnDelete", function(e) {
    var btn = $(e.target).parent().parent();
    var parent = btn.parent();
    var grandparent = parent.parent();
    var grandgrandparent = grandparent.parent();
    var grandgrandgrandparent = grandgrandparent.parent();
    var data = grandgrandgrandparent[0];

    var request = new Object();
    var branchName = $(data).find(".branchName")[0].value;
    var revisionID = $(data).find(".revisionID")[0].value;
    var userName = $(data).find(".userName")[0].value;
    request["editor"] = userName;
    request["commitStatuses"] = [];
    request["commitStatuses"][0] = {};
    request["commitStatuses"][0]["branchName"] = $(parent[0]).find(".operationBranchName")[0].text;
    request["commitStatuses"][0]["status"] = 2; // deleted
    request["commitStatuses"][0]["commitID"] = $(parent[0]).find(".commitID")[0].text;
    request["commitStatuses"][0]["comment"] = ""; // deleted

    api.setPatchRevisionSuccess(function (response) {
        grandparent[0].removeChild(parent[0]);
        $(data).find("#editor").text(request["editor"]);
        console.log("Delete " + request["commitStatuses"][0]["branchName"] + " from " + branchName + " successfully");
    });

    api.patchRevision(branchName, revisionID, request);
});

$(document).on("click", ".BtnAdd", function(e) {
    var btn = $(e.target);
    var parent = btn.parent();
    var grandparent = parent.parent();
    var data = grandparent[0];

    var request = new Object();
    var branchName = $(data).find(".branchName")[0].value;
    var revisionID = $(data).find(".revisionID")[0].value;
    var userName = $(data).find(".userName")[0].value;
    request["editor"] = userName;
    request["bugID"] = $(data).find(".addingBugID")[0].value;
    request["commitStatuses"] = [];
    request["commitStatuses"][0] = {};
    request["commitStatuses"][0]["branchName"] = $(data).find(".addingBranchName")[0].value;
    request["commitStatuses"][0]["status"] = $(data).find('.addingStatus')[0].value; // deleted
    request["commitStatuses"][0]["commitID"] = $(data).find(".addingCommitID")[0].value;
    request["commitStatuses"][0]["comment"] = $(data).find(".addingComment")[0].value;;

    $(data).find(".addingBranchName").css("border-color", "");
    $(data).find(".addingCommitID").css("border-color", "");

    if (request["commitStatuses"][0]["status"] == 0) { // committed
        var hasError = false;

        if (request["commitStatuses"][0]["branchName"] == "") {
            $(data).find(".addingBranchName").css("border-color", "red");
            hasError = true;
        }

        if (request["commitStatuses"][0]["commitID"] == "") {
            $(data).find(".addingCommitID").css("border-color", "red");
            hasError = true;
        }

        if (hasError == true) {
            return;
        }
    } else if (request["commitStatuses"][0]["status"] == 1) { // skipped
        var hasError = false;

        if (request["commitStatuses"][0]["branchName"] == "") {
            $(data).find(".addingBranchName").css("border-color", "red");
            hasError = true;
        }

        if (hasError == true) {
            return;
        }
    }

    api.setPatchRevisionSuccess(function (response) {
        var grandgrandgrandgrandparent = btn.parent().parent().parent().parent().parent();
        var display = grandgrandgrandgrandparent.find("#" + revisionID);
        $(display).find("#editor").text(request["editor"]);

        if (request["commitStatuses"][0]["status"] == 0) {
            var list = display.find(".committedList");
            var li = list.find("#committed" + request["commitStatuses"][0]["commitID"]);

            if (li.length == 0) {
                var div = $("<div class='row'>");
                var li = $("<li id='committed" + request["commitStatuses"][0]["branchName"] + "'></li>");
                var span = $("<span class='commentTooltip' data-toggle='tooltip' data-placement='bottom' title='" + request["commitStatuses"][0]["comment"] + "'></span>");
                var a = $("<a class='operationBranchName'>" + request["commitStatuses"][0]["branchName"] + "</a> : <a class='commitID'>" + request["commitStatuses"][0]["commitID"] + "</a>");
                var button = $("<button type='button' class='close BtnDelete' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");

                span.append(a);
                li.append(span);
                li.append(button);
                div.append($(li));
                list.append(div);
            } else {
                li.find(".commentTooltip").attr("title", request["commitStatuses"][0]["comment"]);
                li.find(".commentTooltip").attr("data-original-title", "");
            }

            // remove the same node from skipped list
            list = display.find(".skippedList");
            if (list.find("#skipped" + request["commitStatuses"][0]["branchName"]).length > 0) {
                list[0].removeChild(list.find("#skipped" + request["commitStatuses"][0]["branchName"])[0])
            }
        } else if (request["commitStatuses"][0]["status"] == 1) {
            var list = display.find(".skippedList");
            var li = list.find("#skipped" + request["commitStatuses"][0]["commitID"]);

            if (li.length == 0) {
                var div = $("<div class='row'>");
                var li = $("<li id='skipped" + request["commitStatuses"][0]["branchName"] + "'></li>");
                var span = $("<span class='commentTooltip' data-toggle='tooltip' data-placement='bottom' title='" + request["commitStatuses"][0]["comment"] + "'></span>");
                var a0 = $("<a class='operationBranchName'>" + request["commitStatuses"][0]["branchName"] + "</a>");
                var a1 = $("<a class='commitID' style='display: none'>" + request["commitStatuses"][0]["commitID"] + "</a>");
                var button = $("<button type='button' class='close BtnDelete' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");

                span.append(a0);
                span.append(a1);
                li.append(span);
                li.append(button);
                div.append($(li));
                list.append(div);

                /*
                list.append("<li id=\"skipped" + request["commitStatuses"][0]["branchName"] + "\">"
                          + "<span class=\"commentTooltip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"" + request["commitStatuses"][0]["comment"] + "\">"
                          + "<a class=\"operationBranchName\">" + request["commitStatuses"][0]["branchName"] + "</a>"
                          + "<a class=\"commitID\" style=\"display: none\">" + request["commitStatuses"][0]["commitID"] + "</a>"
                          + "</span>"
                          + "<button type=\"button\" class=\"close BtnDelete\" aria-label=\"Close\">"
                          + "<span aria-hidden=\"true\">&times;</span>"
                          + "</button>"
                          + "</li>"
                           );
                           */
            } else {
                li.find(".commentTooltip").attr("title", request["commitStatuses"][0]["comment"]);
                li.find(".commentTooltip").attr("data-original-title", "");
            }

            // remove the same node from committed list
            list = display.find(".committedList");
            if (list.find("#committed" + request["commitStatuses"][0]["branchName"]).length > 0) {
                list[0].removeChild(list.find("#committed" + request["commitStatuses"][0]["branchName"])[0])
            }
        }

        console.log("Add " + request["commitStatuses"][0]["branchName"] + " to " + branchName + " successfully");
    });

    api.patchRevision(branchName, revisionID, request);
});

$(document).on("change", ".addingStatus", function(e) {
    var selector = $(e.target);
    var parent = selector.parent();
    var data = parent[0];
    var status = selector[0].value;

    if (status == 0) {
        $(data).find(".addingCommitID").removeAttr("disabled");
    } else if (status == 1) {
        $(data).find(".addingCommitID").attr("disabled", true);
    }
});
