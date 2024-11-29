$(function () {
    $("#jqGrid").jqGrid({
        url: _ctx + 'admin/comment/list',
        datatype: "json",
        viewrecords: true,
        colModel: [
            {label: '#', name: 'id', index: 'id', width: 60, key: true},
            {label: '订单ID', name: 'orderId', index: 'orderId', width: 60},
            {label: '用户ID', name: 'userId', index: 'userId', width: 60},
            {label: '用户名', name: 'userName', index: 'userName', width: 120},
            {label: '评论内容', name: 'content', index: 'content', width: 120},
            {label: '评论商品ID', name: 'goodId', index: 'goodId', width: 60},
            {label: '创建时间', name: 'createTime', index: 'createTime', width: 60},

        ],
        height: 760,
        rowNum: 20,
        rowList: [20, 50, 80],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: false,
        rownumWidth: 20,
        autowidth: true,
        multiselect: true,
        sortable: true,
        sortname: 'createTime', //设置默认的排序列
        sortorder: 'desc',
        pager: "#jqGridPager",
        jsonReader: {
            root: "records",
            page: "current",
            total: "pages",
            records: "total"
        },
        prmNames: {
            page: "pageNumber",
            rows: "pageSize",
            order: "order",
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });

});

/**
 * jqGrid重新加载
 */
// function reload() {
//     var goodsId = $('#goodsId').val() || '';
//     var goodsName = $('#goodsName').val() || '';
//     var goodsIntro = $('#goodsIntro').val() || '';
//     var goodsSellStatus = $('#goodsSellStatus').val() || '';
//     $("#jqGrid").jqGrid('setGridParam', {
//         page: 1,
//         postData: {
//             goodsId: goodsId,
//             goodsName: goodsName,
//             goodsIntro: goodsIntro,
//             goodsSellStatus: goodsSellStatus
//         }
//     }).trigger("reloadGrid");
// }





function delGoods() {
    var selectedRows = getSelectedRows();

    if (selectedRows.length !== 1) {
        swal("提示", "请选择一条数据进行删除操作", "warning");
        return;
    }

    var ids = selectedRows[0];
    swal({
        title: "确认弹框",
        text: "确认要执行删除操作吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {

        if (flag) {

            // 执行删除操作
            $.ajax({
                type: "POST",
                url: _ctx + "admin/comment/del/" + ids,
                contentType: "application/json",
                data: JSON.stringify(ids),
                success: function (r) {
                    $("#jqGrid").trigger("reloadGrid");

                }
            });

        }



    })
}

