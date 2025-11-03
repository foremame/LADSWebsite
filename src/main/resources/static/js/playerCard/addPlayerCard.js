$(document).ready(function(){
    $("form").submit(function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false;
        var errorText = "";

        if ($("#cardLevel").val() < 1 || $("#cardLevel").val() > 80) {
            issue = true;
            errorText += "<p>Invalid card level, please choose a level between 1 - 80.</p>";
        }
        if($("#rankType").val() == "") {
            issue = true;
            errorText += "<p>Please select a card rank.</p>";
        }

        params = {"cardId": $("input[name='cardId']:checked").val()};
        validateByUrl("/playerCard/validatePlayerCard", params, issue, errorText, form);
    });

    $("input[name='cardId']").change( function() {
        $("#rankTypeDiv").show();
        $("#cardLevelDiv").show();
        $("#btnSubmit").show();
    });
});

function showAwakenedDiv() {
    if ($("#cardLevel").val() == 80) {
        $("#awakenedDiv").show();
    } else {
        $("#awakenedDiv").hide();
    }
}