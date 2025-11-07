var limitedIds = [];
var isMulti = false;
const checkedCards = new Map();

document.addEventListener('DOMContentLoaded', function() {
    if (bannerId) {
        url_post("/cardPull/getLimitedCardIds", {"bannerId" : bannerId}).done( function(msg) {
            var ids = JSON.parse(msg);
            limitedIds = ids;
        });
        url_post("/banner/isMulti", {"id" : bannerId}).done( function(msg) {
            isMulti = JSON.parse(msg);
        });
        var bannerRadio = $('#Banner' + bannerId)[0];
        $('#bannerNameSearch').val(bannerRadio.labels[0].textContent);
        bannerRadio.checked = true;
        bannerRadio.parentElement.style.display = "block";
        $('#pullTimestamp').val(date + " " + time);
        $('#pullTimestampDiv').show();
        $('#pullTypeDiv').show();
        $('#submitButtonDiv').show();
    }
}, false);

$(document).ready(function(){
    $("form").submit( function(e) {
        // The rest of the validation is handled when the form is submitted by the controller.
        e.preventDefault();
        $("#errorDiv").hide();
        $("#errorDiv").empty();

        var form = this;
        var issue = false
        var errorText = "";

        if ($("input[name='multipleCardPulls']:checked").val() == "true") {
            for (var i = 0; i < 10; i++) {
                var checkedVal = $("input[name='cardPulls\\[" + i + "\\].cardId']:checked").val();
                if (!checkedVal) {
                    issue = true;
                    var number = i + 1;
                    errorText += "<p class='error-message'>Card " + number + " is not selected.</p>";
                }
            }
        }
        else {
            var checkedVal = $("input[name='cardPulls\\[0\\].cardId']:checked").val();
            if (!checkedVal) {
                issue = true;
                errorText += "<p class='error-message'>Please select a card.</p>";
            }
        }

        if (issue == true) {
            $("#errorDiv").append(errorText);
            $("#errorDiv").show();
        }
        else {
            form.submit();
        }
    });

    $("input[name='bannerId']").change(function(){
        url_post("/cardPull/getLimitedCardIds", {"bannerId" : $(this).val()}).done( function(msg) {
            var ids = JSON.parse(msg);
            limitedIds = ids;
        });
        url_post("/banner/isMulti", {"id" : $(this).val()}).done( function(msg) {
            isMulti = JSON.parse(msg);
        });
        uncheckCards();
        $('#pullTimestampDiv').show();
        $('#pullTypeDiv').show();
        $('#submitButtonDiv').show();
    });

    $("input[name='multipleCardPulls']").change( function() {
        var multiple = $(this).val();
        $('#cardList\\[0\\]').show();
        if (multiple == "true") {
            for (var i = 1; i < 10; i++) {
                $('#' + i + 'cardPullsMultiple').show();
                $('#cardList\\[' + i + '\\]').show();
            }
        }
        else {
            for (var i = 1; i < 10; i++) {
                $('#' + i + 'cardPullsMultiple').hide();
                $('#cardList\\[' + i + '\\]').hide();
            }
        }
    });
});

function uncheckCards() {
    for (var j = 0; j < 10; j++) {
        if (checkedCards.has(j)) {
            document.getElementById('cardNameSearch[' + j + ']').value = "";
            var radioEle = $('#' + checkedCards.get(j) + 'Limited\\[' + j + '\\]');
            var radio;
            // If checked element is limited or standard, it will have a different id value
            if (radioEle.length == 1) {
                radio = radioEle[0];
            }
            else {
                radio = $('#' + checkedCards.get(j) + 'Standard\\[' + j + '\\]')[0];
            }
            radio.checked = false;
            radio.parentElement.style.display = "none";
            var preciseWishName = "'cardPulls[" + j + "].preciseWish'"
            var checkbox = $("[name=" + preciseWishName + "]");
            checkbox[0].parentElement.style.display = "none";
            checkbox[0].checked = false;
        }
    }
    checkedCards.clear();
}

function cardRadioInputChange(selectedCardId, cardPullNum) {
    checkedCards.set(Number(cardPullNum), selectedCardId);
    var isLimitedCard = limitedIds.includes(Number(selectedCardId));
    var cardTypeString = isLimitedCard == true ? 'Limited' : 'Standard';

    var radio = $('#' + selectedCardId + cardTypeString + '\\[' + cardPullNum + '\\]');
    $('#cardNameSearch\\[' + cardPullNum + '\\]').val($('#' + selectedCardId + cardTypeString + '\\[' + cardPullNum + '\\]')[0].labels[0].textContent);
    hideOtherCardsFromSearch(selectedCardId, cardPullNum);
    var preciseWishAttrName = "'cardPulls[" + cardPullNum + "].preciseWish'";
    var checkbox = $("[name=" + preciseWishAttrName + "]")[0];
    if (isMulti) {
        if (isLimitedCard) {
            checkbox.parentElement.style.display = "block";
        }
        else {
            checkbox.parentElement.style.display = "none";
            checkbox.checked = false;
        }
    }
    else {
        checkbox.parentElement.style.display = "none";
        checkbox.checked = false;
    }
}

function searchForNameLimited(cardPullNum) {
    var cardNameSearch = "cardNameSearch[" + cardPullNum + "]";
    var listName = "cardsUL[" + cardPullNum + "]";
    searchForName(cardNameSearch, listName, limitedIds, "true");
}