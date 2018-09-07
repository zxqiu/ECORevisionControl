$(document).on("click", ".moreBug", function(e){
    e.preventDefault();

    var btn = $(e.target);
    btn.removeClass("moreBug");
    btn.removeClass("btn-success");
    btn.addClass("lessBug");
    btn.addClass("btn-outline-danger");
    btn.html("-");

    var bugIDs = $("#bugIDs");
    var newDiv = $("<div class='form-inline input-group'></div>");
    var newInputBugID = "<input type='text' class='form-control' id='bugID' placeholder='Bug ID'/>";
    var newInputFixBranchName = "<input type='text' class='form-control' id='fixBranchName' placeholder='Fix Branch Name'/>";
    var newInputFixRevisionID = "<input type='text' class='form-control' id='fixRevisionID' placeholder='Fix Revision ID'/>";
    var newButton = "<button class='input-group-addon moreBug btn btn-success' style='width: 40px;'>+</button>";

    newDiv.append($(newInputBugID));
    newDiv.append($(newInputFixBranchName));
    newDiv.append($(newInputFixRevisionID));
    newDiv.append($(newButton));
    bugIDs.append(newDiv);
});

$(document).on("click", ".lessBug", function (e) {
    e.preventDefault();
    $(e.target).parent().remove();
})
