$(document).ready(function(){
    $("form").submit( function(e) {
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false;
        var errorText = "";
        var cardOrigin = $("input[name='cardOrigin']:checked").val();
        var activityId = cardOrigin == "Event" ? $("input[name='eventId']:checked").val() : $("input[name='bannerId']:checked").val();

        if (!activityId) {
            issue = true;
            errorText += "<p class='error-message'>Please select a banner/event.</p>";
        }
        if ($("#loveInterest").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a love interest.</p>";
        }
        if ($("#rarityTypes").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a card rarity.</p>";
        }
        if ($("#cardTypes").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a card type.</p>";
        }
        if ($("#stellacrumTypes").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a stellacrum color.</p>";
        }
        if ($("#mainStats").val() == "") {
            issue = true;
            errorText += "<p class='error-message'>Please select a main stat.</p>";
        }

        var params = {"cardName" : $("#name").val()};
        validateByUrl("/card/validateCardForm", params, issue, errorText, form);
    });

    $("input[name='eventId']").change( function() {
        var cardOrigin = $("input[name='cardOrigin']:checked").val();
        var activityId = $(this).val();
        populateLoveInterestList(cardOrigin, activityId);
    });

    $("input[name='bannerId']").change( function() {
        var cardOrigin = $("input[name='cardOrigin']:checked").val();
        var activityId = $(this).val();
        populateLoveInterestList(cardOrigin, activityId);
    });

    $("input[name='cardOrigin']").change( function() {
        if ($(this).val() == "Event") {
            $("#bannerDiv").hide();
            $("#eventDiv").show();
        }
        else {
            $("#eventDiv").hide();
            $("#bannerDiv").show();
        }
    });
});

function populateLoveInterestList(cardOrigin, activityId) {
    url_get("/activityLoveInterest/getLoveInterestListByActivity", {"activityType" : cardOrigin, "activityId" : activityId}).done( function(msg) {
        var lis = JSON.parse(msg);
        $("#loveInterestDiv").hide();
        $("#loveInterestDiv").empty();
        var appendText = '<label for="loveInterest">Love Interest:</label>';
        appendText += '<select id="loveInterest" name="loveInterestType">';
        appendText += '<option value="">--Please Choose An Option--</option>';

        for (var li of lis) {
            appendText += '<option value="' + li + '">' + li + '</option>';
        }

        appendText += "</select>";
        $("#loveInterestDiv").append(appendText);

        $("#loveInterestDiv").show();
        $("#rarityTypeDiv").show();
        $("#cardTypeDiv").show();
        $("#stellacrumTypeDiv").show();
        $("#mainStatDiv").show();
        $("#btnSubmit").show();
    });
}