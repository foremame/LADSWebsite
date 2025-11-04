$(document).ready(function(){
    $("form").submit( function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();
        var form = this;
        var issue = false;
        var errorText = "";

        var activityType = $("input[name='activityType']:checked").val();
        var activityId = activityType == "Event" ? $("input[name='eventId']:checked").val() : $("input[name='bannerId']:checked").val();

        if (!activityId) {
            issue = true;
            errorText += "<p class='error-color'>No " + activityType + " is selected.</p>";
        }

        var params = {"startDate" : $("#startDate").val(), "endDate" : $("#endDate").val(),
            "activityType" : activityType, "activityId" : activityId};

        validateByUrl("/activityRunPeriod/validateRunPeriodForm", params, issue, errorText, form);
    });

    $("input[name='activityType']").change( function() {
        if ($(this).val() == "Event") {
            $("#bannerDiv").hide();
            $("#eventDiv").show();
        }
        else {
            $("#eventDiv").hide();
            $("#bannerDiv").show();
        }
        $("#startDiv").show();
        $("#endDiv").show();
        $("#btnSubmit").show();
    });
});