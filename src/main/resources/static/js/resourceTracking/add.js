function showResourceType(checked, resourceType) {
    if (checked == true) {
        $("#" + resourceType + "ValuesDiv").show();
    }
    else {
        $("#" + resourceType + "ValuesDiv").hide();
    }
}