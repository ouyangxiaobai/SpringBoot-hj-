/*
Template Name: 系统 - Bootstrap Light Real Estate HTML Template
Author: Webartinfo
Author URI: https://themeforest.net/user/webartinfothems
Version: 1.0
*/
/*
	-- Hover Nav
	-- Select2
*/
$(document).ready(function () {
    "use strict";

    // ===========Hover Nav============	
    $('.navbar-nav li.dropdown').hover(function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeIn(500);
    }, function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeOut(500);
    });

    // ===========Select2============
    $(document).ready(function () {
        $('.select2').select2();
    });

});

$('#toLogin').click(function () {
    localStorage.setItem('lastUrl', window.location.href);
    window.location.href = '/login';
});
$('#toRegister').click(function () {
    localStorage.setItem('lastUrl', window.location.href);
    window.location.href = '/register';
});


$('#btn-login').click(function () {
    const name = $("#userName").val();
    const pwd = $("#userPass").val();
    const kaptcha = $("#kaptcha").val();

    if (name == "" || pwd == "" || kaptcha == "") {
        alert("请输入完整信息！");
    } else {
        $.ajax({
            type: 'POST',
            url: '/login',
            async: false,
            data: {
                'userName': name,
                'userPass': pwd,
                'kaptcha': kaptcha
            },
            success: function (data) {
                if (data.code == 1) {
                    alert('登录成功')
                    let lastUrl = localStorage.getItem('lastUrl');
                    if (lastUrl == null || lastUrl == '') {
                        window.location.href = '/admin';
                    } else {
                        localStorage.setItem('lastUrl', '');
                        window.location.href = lastUrl;
                    }
                } else {
                    alert(data.msg);
                    // 刷新验证码
                    $('.changeCaptcha').click();
                }
            }
        });
    }
})


$('#btn-register').click(function () {
    const userName = $("#userName").val();
    const userPass = $("#userPass").val();
    const idCard = $("#idCard").val();
    const kaptcha = $("#kaptcha").val();

    const role = $("input[name='role']:checked").val();
    const userDisplayName = $("#userDisplayName").val();
    if (userName == "" || userPass == "" || idCard == "" || userDisplayName == "" || role == "") {
        alert("请输入完整信息！");
    } else {
        $.ajax({
            type: 'POST',
            url: '/register',
            async: false,
            data: {
                'userName': userName,
                'userPass': userPass,
                'idCard': idCard,
                'userDisplayName': userDisplayName,
                'role': role,
                'kaptcha': kaptcha

            },
            success: function (data) {
                if (data.code == 1) {
                    alert('注册成功');
                    window.location.href = '/login';
                } else {
                    alert(data.msg);
                    // 刷新验证码
                    $('.changeCaptcha').click();
                }
            }
        });
    }
});


$('#btn-logout').click(function () {
    $.ajax({
        type: 'POST',
        url: '/logout',
        async: false,
        success: function () {
            window.location.reload();
        }
    });
});

// 创建订单
$('#btn-addOrder').click(function () {
    const endDate = $("#endDate").val();
    const postId = $("#postId").val();

    if (confirm('您确定是否租赁该房子吗？')) {

        $.ajax({
            type: 'POST',
            url: '/order/create',
            async: false,
            data: {
                'postId': postId,
                'endDate': endDate
            },
            success: function (data) {
                if (data.code == 1) {
                    alert('订单生成成功，请签订合同');
                    window.open('/order/agreement?orderId=' + data.result);
                } else {
                    alert(data.msg);
                }
            }
        });
    }

});

$('#btn-agreement').click(function () {
    let orderId = $(this).attr('data-id');
    if (confirm('您确定已阅读并同意上述合同内容？')) {
        window.location.href = '/order/pay?orderId=' + orderId;
    }
});

$('#btn-pay').click(function () {
    $.ajax({
        type: 'post',
        url: '/order/pay',
        data: {
            'orderId': $('#orderId').val()
        },
        success: function (data) {
            if (data.code == 1) {
                alert('支付成功');
                window.location.href = '/admin/order'
            } else {
                alert(data.msg);
            }
        }
    });
});
//切换验证码
$(document).on('click', '.changeCaptcha', function () {
    var num = Math.random() * (1000 - 1) + 1;
    var url = "/getKaptchaImage?random=" + num;
    $(".captcha").attr("src", url);
});