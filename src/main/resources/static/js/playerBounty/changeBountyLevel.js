$(document).ready(function(){
    $("form").submit(function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false;
        var errorText = "";

        if ($("#bountyLevel").val() < 1 || $("#bountyLevel").val() > 9) {
            issue = true;
            errorText += "<p class='error-message'>Invalid bounty level, please choose a level between 1 - 9.</p>";
        }
        if($("#bountyName").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a bounty.</p>";
        }

        if (issue == true) {
            $("#errorDiv").append(errorText);
            $("#errorDiv").show();
        }
        else {
            form.submit();
        }
    });
});