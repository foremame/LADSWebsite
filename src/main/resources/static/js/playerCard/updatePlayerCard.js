$(document).ready(function(){
    if (selectedCard != null) {
        var cardRadio = $('#' + selectedCard)[0];
        cardRadio.checked = true;
        cardRadio.parentElement.style.display = "block";
        updateSelectedCard(selectedCard);
    }

    $("input[name='playerCardId']").change( function() {
        updateSelectedCard($(this).val());
    });
});

function updateSelectedCard (cardId) {
    url_get("/playerCard/getPlayerCardInfoById", {"playerCardId" : cardId}).done( function(msg) {
        var playerCard = JSON.parse(msg);
        $("#rankType").val(playerCard["rankType"]);
        $("#cardLevel").val(Number(playerCard["level"]));
        $("#awakened")[0].checked = playerCard["awakened"] == "true" ? true : false;
        hideItemsFromSearch(cardId, "playerCardsUL");
        $("#cardNameSearch").val(playerCard["name"]);

        $("#rankTypeDiv").show();
        $("#cardLevelDiv").show();
        $("#awakenedDiv").show();
        $("#btnSubmit").show();
    });
}