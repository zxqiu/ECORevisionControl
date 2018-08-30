var hostURL = window.location.protocol + "//" + window.location.host;
var API_V1 = "/api/v1";
var API_REVISION = "/revisions";

var APIs = {
    createNew: function () {
        var _api = {};

        _api.putRevisionSuccess = null;
        _api.putRevisionError = null;

        _api.setPutRevisionSuccess = function(callback) {
            _api.putRevisionSuccess = callback;
        }

        _api.setPutRevisionError = function(callback) {
            _api.putRevisionError = callback;
        }

        _api.putRevision = function(branchName, revisionID, jsonObj) {
            var putURL = hostURL + API_V1 + API_REVISION + "/" + branchName + "/" + revisionID;
            console.log(putURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "PUT",
                url: putURL,
                data: JSONString,
                //dataType: 'json',
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR) {
                    if (_api.putRevisionSuccess && typeof(_api.putRevisionSuccess) == "function") {
                        _api.putRevisionSuccess(data);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (_api.putRevisionError && typeof(_api.putRevisionError) == "function") {
                        _api.putRevisionError(textStatus);
                    }
                }
            });
        };

        return _api;
    }
};
