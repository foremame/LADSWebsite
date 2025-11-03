$(document).ready(function(){
    $("form").submit( function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false;
        var errorText = "";
        if ($("#subType").val() == "Multi") {
            if ($("#loveInterestDiv").find('input').filter(':checked').length == 0) {
                issue = true;
                errorText += "<p>Please select at least one love interest</p>";
            }
        }
        else {
            if ($("#loveInterest").val() == "") {
                issue = true;
                errorText += "<p>Please select a love interest.</p>";
            }
        }
        if ($("#mainType").val() == "" || $("#subType").val() == "") {
            issue = true;
            errorText += "<p>Invalid banner type entered</p>";
        }
        var params = {"bannerName" : $("#name").val(), "startDate" : $("#startDate").val(), "endDate" : $("#endDate").val()};
        validateByUrl("/banner/validateBannerForm", params, issue, errorText, form);
    });

    $("select[name='mainType']").change( function() {
        url_post("/bannerCategory/getSubTypes", {"mainType" : $(this).val()})
            .done( function(msg) {
                var subtypes = JSON.parse(msg);
                $("#subType").empty().append("<option value=''>--Please Choose An Option--</option>");
                for (var subtype of subtypes) {
                    $("#subType").append("<option value='" + subtype + "'>" + subtype + "</option>");
                }
                $("#subTypesDiv").show();
            });
    });

    $("select[name='subType']").change( function() {
        var subTypeValue = $(this).val();
        url_get("/activityLoveInterest/getLoveInterestList", "")
            .done( function(msg) {
                var loveInterests = JSON.parse(msg);
                $("#loveInterestDiv").empty();
                if (subTypeValue == "Multi") {
                    $('#loveInterestDiv').append('<p>Select Love Interests:</p>');
                    for (var i = 0; i < loveInterests.length; i++) {
                        $("#loveInterestDiv").append('<input type="checkbox" id="' + loveInterests[i] + '" name="activitySubForms[' + i + '].loveInterest" value="' + loveInterests[i] + '" checked>');
                        $("#loveInterestDiv").append('<label for="' + loveInterests[i] + '"> ' + loveInterests[i] + '</label><br>');
                    }
                    $("#loveInterestDiv").append('<br>');
                }
                else {
                    var appendString = '<label for="loveInterest">Select Love Interest: </label><select id="loveInterest" name="activitySubForms[0].loveInterest"><option value="">--Please Choose An Option--</option>';
                    for (var i = 0; i < loveInterests.length; i++) {
                        appendString += '<option value="' + loveInterests[i] + '">' + loveInterests[i] + '</option>';
                    }
                    appendString += '</select>';
                    $("#loveInterestDiv").append(appendString);
                }
            }
        );

        $("#loveInterestDiv").show();
        $("#startDiv").show();
        $("#endDiv").show();
        $("#btnSubmit").show();
    });
});