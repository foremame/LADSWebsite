function searchForName(searchId, listId, ids, include) {
    // Declare variables
    var input, filter, ul, li, label, i, txtValue;
    input = document.getElementById(searchId);
    filter = input.value.toUpperCase();
    ul = document.getElementById(listId);
    li = ul.getElementsByTagName('li');

    // Loop through all list items, and hide those who don't match the search query
    for (i = 0; i < li.length; i++) {
        label = li[i].getElementsByTagName("label")[0];
        radioInput = li[i].getElementsByTagName("input")[0];
        txtValue = label.textContent || label.innerText;
        if (filter != "" && txtValue.toUpperCase().indexOf(filter) > -1) {
            var curValue = Number(radioInput.value);
            if (ids && include == "true" && radioInput.id.indexOf("Limited") > -1) {
                if (ids.includes(curValue)) {
                    li[i].style.display = "";
                }
            }
            else if (ids && include == "false") {
                li[i].style.display = !ids.includes(curValue) ? "" : "none";
            }
            else {
                li[i].style.display = "";
            }
        } else {
            li[i].style.display = "none";
        }
    }
}

function url_get(url, params) {
    return $.ajax({
        type: "get",
        dataType: "text",
        url: url,
        data: params
    });
}

function url_post(url, params) {
    return $.ajax({
        type: "post",
        dataType: "text",
        url: url,
        data: params
    });
}

// Took this from stackoverflow, it works as needed for now but will likely replace with something different in the future as
// it has some quirks that I don't enjoy
function addTableSort() {
    const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

    const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
        v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
        )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

    // do the work...
    document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
        const table = th.closest('table');
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
            .forEach(tr => table.appendChild(tr) );
    })));
}

function hideOtherCardsFromSearch(cardId, cardPullNum) {
    ul = document.getElementById("cardsUL[" + cardPullNum + "]");
    li = ul.getElementsByTagName('li');
    for (i = 0; i < li.length; i++) {
        var input = li[i].getElementsByTagName("input")[0];
        if (input.value != cardId) {
            li[i].style.display = "none";
        }
    }
}

function validateByUrl(url, params, issue, errorText, form) {
    url_get(url, params).done( function (msg){
        var validation = JSON.parse(msg);

        for (let curError of validation) {
            issue = true;
            errorText += "<p>" + curError + "</p>";
        }

        if (issue == true) {
            $("#errorDiv").append(errorText);
            $("#errorDiv").show();
        }
        else {
            form.submit();
        }
    });
}