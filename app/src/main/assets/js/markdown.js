Zepto(function($){
    $("input[type=checkbox]").on('change', function(){
        element = $(this);
        id = element.attr('id');
        checked = element.is(':checked');
        content = $("#" + id + '_label').html();

        if (md) {
            md.todoListItemStatusChanged(id, content, checked);
        }
    });
})