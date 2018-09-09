function submitQuery(queryType, url, data, contentType, successFunc, errorFunc) {

    $.ajax({
        type: queryType,
        url: url,
        data: data,
        contentType: contentType,
        success: function(data, textStatus, jqXHR) {
            if (successFunc && typeof(successFunc) == "function") {
                successFunc(data);
            }

            showSuccessSnackBar();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (errorFunc && typeof(errorFunc) == "function") {
                errorFunc(textStatus);
            }

            showErrorSnackBar(jqXHR, textStatus, errorThrown);
        }
    });
}

var APIs = {
    createNew: function () {
        var _api = {};

        _api.hostURL = window.location.protocol + "//" + window.location.host;
        _api.GUI_CHANGE_ORDER = "/changeOrders";
        _api.API_V1 = "/api/v1";
        _api.API_REVISION = "/revisions";
        _api.API_CHANGE_ORDER = "/changeOrders";
        _api.entriesPerPage = 10;

        _api.patchRevisionSuccess = null;
        _api.patchRevisionError = null;

        _api.setPatchRevisionSuccess = function(callback) {
            _api.patchRevisionSuccess = callback;
        }

        _api.setPatchRevisionError = function(callback) {
            _api.patchRevisionError = callback;
        }

        _api.patchRevision = function(branchName, revisionID, jsonObj) {
            var patchURL = _api.hostURL + _api.API_V1 + _api.API_REVISION + "/" + branchName + "/" + revisionID;
            console.log(patchURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            submitQuery("PATCH", patchURL, JSONString, 'application/json'
                , _api.patchRevisionSuccess, _api.patchRevisionError);
        };




        _api.postChangeOrderSuccess = null;
        _api.postChangeOrderError = null;

        _api.setCallbacks = function (success, error) {
            _api.postChangeOrderSuccess = success;
            _api.postChangeOrderError = error;
        }

        _api.postChangeOrder = function(commitID, jsonObj) {
            var patchURL = _api.hostURL + _api.API_V1 + _api.API_CHANGE_ORDER + "/" + commitID;
            console.log(patchURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            submitQuery("POST", patchURL, JSONString, 'application/json'
                , _api.postChangeOrderSuccess, _api.postChangeOrderError);
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
