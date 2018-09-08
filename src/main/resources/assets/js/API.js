var hostURL = window.location.protocol + "//" + window.location.host;
var API_V1 = "/api/v1";
var API_REVISION = "/revisions";
var API_CHANGE_ORDER = "/changeOrder";

var APIs = {
    createNew: function () {
        var _api = {};

        _api.patchRevisionSuccess = null;
        _api.patchRevisionError = null;

        _api.setPatchRevisionSuccess = function(callback) {
            _api.patchRevisionSuccess = callback;
        }

        _api.setPatchRevisionError = function(callback) {
            _api.patchRevisionError = callback;
        }

        _api.patchRevision = function(branchName, revisionID, jsonObj) {
            var patchURL = hostURL + API_V1 + API_REVISION + "/" + branchName + "/" + revisionID;
            console.log(patchURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "PATCH",
                url: patchURL,
                data: JSONString,
                //dataType: 'json',
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR) {
                    if (_api.patchRevisionSuccess && typeof(_api.patchRevisionSuccess) == "function") {
                        _api.patchRevisionSuccess(data);
                    }

                    showSuccessSnackBar();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (_api.patchRevisionError && typeof(_api.patchRevisionError) == "function") {
                        _api.patchRevisionError(textStatus);
                    }

                    showErrorSnackBar(jqXHR, textStatus, errorThrown);
                }
            });
        };

        _api.postChangeOrderSuccess = null;
        _api.postChangeOrderError = null;

        _api.setPostChangeOrderSuccess = function(callback) {
            _api.postChangeOrderSuccess = callback;
        }

        _api.setPostChangeOrderError = function(callback) {
            _api.postChangeOrderError = callback;
        }

        _api.postChangeOrder = function(commitID, jsonObj) {
            var patchURL = hostURL + API_V1 + API_CHANGE_ORDER + "/" + commitID;
            console.log(patchURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: patchURL,
                data: JSONString,
                //dataType: 'json',
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR) {
                    if (_api.postChangeOrderSuccess && typeof(_api.postChangeOrderSuccess) == "function") {
                        _api.postChangeOrderSuccess(data);
                    }

                    showSuccessSnackBar();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (_api.postChangeOrderError && typeof(_api.postChangeOrderError) == "function") {
                        _api.postChangeOrderError(textStatus);
                    }

                    showErrorSnackBar(jqXHR, textStatus, errorThrown);
                }
            });
        };

        return _api;
    }
};

function showSuccessSnackBar() {
    var snackbar = $("#successSnackBar");
    if (snackbar.length > 0) {
        snackbar.addClass("show");
        setTimeout(function () {
            snackbar.removeClass("show");
        }, 3000);
    }
}

function showErrorSnackBar(jqXHR, textStatus, errorThrown) {
    var snackbar = $("#errorSnackBar");
    if (snackbar.length > 0) {

        if (jqXHR.responseText.length > 0) {
            snackbar.html(textStatus + ": " + jqXHR.responseText);
            console.log(jqXHR.responseText);
        } else if (textStatus.length > 0) {
            snackbar.html(textStatus + ": " + errorThrown);
        }

        snackbar.addClass("show");
        setTimeout(function () {
            snackbar.removeClass("show");
        }, 3000);
    }
}
