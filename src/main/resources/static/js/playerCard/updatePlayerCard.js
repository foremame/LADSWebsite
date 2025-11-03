$(document).ready(function(){
    $("input[name='playerCardId']").change( function() {
        url_get("/playerCard/getPlayerCardInfoById", {"playerCardId" : $(this).val()}).done( function(msg) {
            var playerCard = JSON.parse(msg);
            $("#rankType").val(playerCard["rankType"]);
            $("#cardLevel").val(Number(playerCard["level"]));
            $("#awakened")[0].checked = playerCard["awakened"] == "true" ? true : false;

            $("#rankTypeDiv").show();
            $("#cardLevelDiv").show();
            $("#awakenedDiv").show();
            $("#btnSubmit").show();
        });
    });
});