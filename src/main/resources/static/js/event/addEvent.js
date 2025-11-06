$(document).ready(function(){
    $("form").submit( function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false;
        var errorText = "";
        if ($("#mainType").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select an event type</p>";
        }
        if ($("#loveInterestDiv").find('input').filter(':checked').length == 0) {
            issue = true;
            errorText += "<p class='error-message'>Please select at least one love interest</p>";
        }
        var params = {"eventName" : $("#name").val(), "startDate" : $("#startDate").val(), "endDate" : $("#endDate").val()};
        validateByUrl("/event/validateEventForm", params, issue, errorText, form);
    });
});