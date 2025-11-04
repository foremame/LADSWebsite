var numCardPrev = 0;
const selectedCards = new Map();

function changeCardForm() {
    const numCards = Number($("#numCards").val());
    var i,j;
    if (numCards >= 0 && $("#numCards").val() != "") {
        // If user decides they want to level less cards, remove the cards by hiding them.
        if (numCards < numCardPrev) {
            if (numCards == 0) {
                hideOldCardDiv(numCards);
            }
            for (j = numCardPrev-1; j > numCards-1; j--) {
                hideOldCardDiv(j);
            }
        }
        // For each additional card the user wants to level (compared to the last time this function was run):
        // Add a new card leveling form group to the page.
        for (i = numCardPrev; i < numCards; i++) {
            var nameAndId = "cardInfo[" + i + "]";
            var divElement = document.getElementById("cardInfo[" + i + "]Div");

            // If the card form for this number has already been generated, show it
            if (divElement) {
                divElement.style.display = "block";
            }
            else {
                var appendString = '<div id="' + nameAndId + 'Div">';
                var cardUserNumber = Number(i) + 1;
                appendString += '<p>Card #' + cardUserNumber + '</p>';

                // Card Name Selector
                appendString += '<label for="cardNameSearch[' + i + ']">Level Owned Card(if needed):</label>';
                appendString += '<input type="text" id="cardNameSearch[' + i + ']" onkeyup="searchForCardName(' + i + ')" placeholder="Search for card name..">';
                appendString += '<ul id="cardsUL[' + i + ']">';
                for (var playerCard of playerCards) {
                    var listId = nameAndId + '.cardId' + playerCard.cardId;
                    appendString += '<li style="display:none">';
                    appendString += '<input type="radio" id="' + listId + '" name="' + nameAndId + '.cardId" value="' + playerCard.cardId + '" onchange="cardRadioChange(' + i + ', ' + playerCard.cardId + ')">';
                    appendString += '<label for="' + listId + '">' + playerCard.name + '</label>';
                    appendString += '</li>';
                }
                appendString += '</ul>';

                // Card Rarity
                appendString += '<div class="form-group" id="cardRarityDiv[' + i + ']">';
                appendString += '<label for="' + nameAndId + '.rarityType">Card Rarity: </label>';
                appendString += '<select id="' + nameAndId + '.rarityType" name="' + nameAndId + '.rarityType">';
                appendString += '<option value="">--Please Choose An Option--</option>';
                for (var rarity of rarities) {
                    appendString += '<option value="' + rarity + '">' + rarity + '</option>';
                }
                appendString += '</select></div>';

                // Card Starting Level
                appendString += '<div class="form-group" id="startingLevelDiv[' + i + ']">';
                appendString += '<label for="' + nameAndId + '.startLevel">Starting Level: </label>';
                appendString += '<input type="number" id="' + nameAndId + '.startLevel" name="' + nameAndId + '.startLevel" onkeyup="startLevelChange(' + i + ')">';
                appendString += '</div>';

                // Card Ending Level
                appendString += '<div class="form-group" id="endingLevelDiv[' + i + ']">';
                appendString += '<label for="' + nameAndId + '.endLevel">Ending Level: </label>';
                appendString += '<input type="number" id="' + nameAndId + '.endLevel" name="' + nameAndId + '.endLevel" onkeyup="endLevelChange(' + i + ')">';
                appendString += '</div>';

                // Card Stellacrum
                appendString += '<div class="form-group" id="stellacrumDiv[' + i + ']">';
                appendString += '<label for="' + nameAndId + '.stellacrum">Stellacrum: </label>';
                appendString += '<select id="' + nameAndId + '.stellacrum" name="' + nameAndId + '.stellacrum">';
                appendString += '<option value="">--Please Choose An Option--</option>';
                for (var stellacrum of stellacrums) {
                    appendString += '<option value="' + stellacrum + '">' + stellacrum + '</option>';
                }
                appendString += '</select></div>';

                // Do they have the starting ascension (if starting level%10=0)
                appendString += '<div class="form-group" id="startAscensionDiv[' + i + ']" style="display:none">';
                appendString += '<p>Has Starting Ascension? (ie. at lvl 10, card has already ascended from 10 -> 20)</p>';
                appendString += '<label for="' + nameAndId + '.startAscensionYes">Yes</label>';
                appendString += '<input type="radio" id="' + nameAndId + '.startAscensionYes" name="' + nameAndId + '.startAscension" value="true">';
                appendString += '<label for="' + nameAndId + '.startAscensionNo">No</label>';
                appendString += '<input type="radio" id="' + nameAndId + '.startAscensionNo" name="' + nameAndId + '.startAscension" value="false">';
                appendString += '</div>';

                // Do they want to get the last ascension? (if ending level%10=0)
                appendString += '<div class="form-group" id="endAscensionDiv[' + i + ']" style="display:none">';
                appendString += '<p>Want Final Ascension?</p>';
                appendString += '<label for="' + nameAndId + '.endAscensionYes">Yes</label>';
                appendString += '<input type="radio" id="' + nameAndId + '.endAscensionYes" name="' + nameAndId + '.endAscension" value="true">';
                appendString += '<label for="' + nameAndId + '.endAscensionNo">No</label>';
                appendString += '<input type="radio" id="' + nameAndId + '.endAscensionNo" name="' + nameAndId + '.endAscension" value="false">';
                appendString += '</div>';

                appendString += '</div>';
                $('#cardInfoDiv').append(appendString);
            }
        }
        numCardPrev = numCards;
    }
    $('#cardInfoDiv').show();
    $('#btnSubmit').show();
}

function hideOldCardDiv(cardIndex) {
    $("#cardInfo\\[" + cardIndex + "\\]Div").hide();
    $("#cardNameSearch\\[" + cardIndex + "\\]").val("");
    searchForCardName(cardIndex);
    if (selectedCards.has(cardIndex)) {
        var curSelected = selectedCards.get(cardIndex);
        $("#cardInfo\\[" + cardIndex + "\\]\\.cardId" + curSelected)[0].checked = false;
        selectedCards.delete(cardIndex);
    }
}

function startLevelChange(cardIndex) {
    var val = $("#cardInfo\\[" + cardIndex + "\\]\\.startLevel").val();
    if (val%10 == 0) {
        $("#startAscensionDiv\\[" + cardIndex + "\\]").show();
    }
    else {
        $("#startAscensionDiv\\[" + cardIndex + "\\]").hide();
    }
}

function endLevelChange(cardIndex) {
    console.log("hello");
    var val = $("#cardInfo\\[" + cardIndex + "\\]\\.endLevel").val();
    if (val%10 == 0) {
        $("#endAscensionDiv\\[" + cardIndex + "\\]").show();
    }
    else {
        $("#endAscensionDiv\\[" + cardIndex + "\\]").hide();
    }
}

function cardRadioChange(cardIndex, cardId) {
    selectedCards.set(cardIndex, cardId);
    var nameAndId = "cardInfo\\[" + cardIndex + "\\]";

    url_get("/playerCard/getPlayerCardInfoById", {"playerCardId" : cardId}).done( function(msg) {
        var playerCardVals = JSON.parse(msg);
        $("#" + nameAndId + "\\.stellacrum").val(playerCardVals["stellacrum"]);
        $("#" + nameAndId + "\\.startLevel").val(Number(playerCardVals["level"]));
        startLevelChange(cardIndex);
        $("#" + nameAndId + "\\.rarityType").val(playerCardVals["rarity"]);
        $("#cardNameSearch\\[" + cardIndex + "\\]").val(playerCardVals["name"]);
        hideOtherCardsFromSearch(cardId, cardIndex);
        // hide from all other displayed card lists to prevent users from selecting the card twice
        for (var i = 0; i < numCardPrev; i++) {
            if (i != cardIndex) {
                $("#cardInfo\\[" + i + "\\]\\.cardId" + cardId)[0].parentElement.style.display = "none";
            }
        }
    });
}

function searchForCardName(cardIndex) {
    const ids = [];
    for (let [key,value] of selectedCards) {
        if (key != cardIndex) {
            ids.push(Number(value));
        }
    }
    var searchId = "cardNameSearch[" + cardIndex + "]";
    var listId = "cardsUL[" + cardIndex + "]";
    searchForName(searchId, listId, ids, "false");
}