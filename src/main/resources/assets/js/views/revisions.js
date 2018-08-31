var api = APIs.createNew();

$(".BtnDelete").on("click", function(e) {
    var btn = $(e.target);
    var parent = btn.parent();
    var grandparent = parent.parent();
    var grandgrandparent = grandparent.parent();
    var grandgrandgrandparent = grandgrandparent.parent();
    var data = grandgrandgrandparent[0];

    var request = new Object();
    var branchName = $(data).find(".branchName")[0].value;
    var revisionID = $(data).find(".revisionID")[0].value;
    request["editor"] = "user0";
    request["commitStatuses"] = [];
    request["commitStatuses"][0] = {};
    request["commitStatuses"][0]["branchName"] = $(parent[0]).find(".operationBranchName")[0].text;
    request["commitStatuses"][0]["status"] = 2; // deleted
    request["commitStatuses"][0]["commitID"] = $(parent[0]).find(".commitID")[0].text;
    request["commitStatuses"][0]["comment"] = ""; // deleted

    api.setPutRevisionSuccess(function (response) {
        grandparent[0].removeChild(parent[0]);
        console.log("Delete " + request["commitStatuses"][0]["branchName"] + " from " + branchName + " successfully")
    });

    api.putRevision(branchName, revisionID, request);
});