var hostURL = window.location.protocol + "//" + window.location.host;
var API_V1 = "/api/v1";
var API_REVISION = "/revisions";

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
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (_api.patchRevisionError && typeof(_api.patchRevisionError) == "function") {
                        _api.patchRevisionError(textStatus);
                    }
                }
            });
        };

        return _api;
    }
};
