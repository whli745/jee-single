<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../base/taglib.jsp" %>
<html lang="zh_CN">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <meta name="description" content="User login page"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <title>登录页 - J2E_SINGLE</title>
    <%@include file="../base/meta.jsp" %>
    <style type="text/css">
        #msg-error {
            position: relative;
            background: #ffebeb;
            color: #e4393c;
            border: 1px solid #faccc6;
            padding: 3px 10px 3px 40px;
            line-height: 15px;
            height: auto;
            visibility: hidden;
        }
    </style>
</head>
<body class="login-layout" style="background-image: url(${ctx}/static/ace/images/bg-img.jpg);">
<div class="main-container">
    <div class="main-content">
        <div class="row" style="height: 450px;">
            <div class="col-sm-10 col-sm-offset-1">
                <div class="login-container">
                    <div class="center">
                        <h1>
                            <i class="ace-icon fa fa-leaf green"></i>
                            <span class="red">JEEWHL</span>
                            <span class="white" id="id-text2">Application</span>
                        </h1>
                    </div>
                    <div class="space-6"></div>

                    <div class="position-relative">
                        <div id="login-box" class="login-box visible widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header blue lighter bigger">
                                        <i class="ace-icon fa fa-coffee green"></i>
                                        请 登 录
                                    </h4>

                                    <div class="space-6"></div>

                                    <form id="loginForm">
                                        <fieldset>
                                            <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text" id="loginName" name="loginName"
                                                                   class="form-control" placeholder="请输入用户名" autofocus/>
															<i class="ace-icon fa fa-user"></i>
														</span>
                                            </label>

                                            <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" id="password" name="password"
                                                                   class="form-control" placeholder="请输入密码"/>
															<i class="ace-icon fa fa-lock"></i>
														</span>
                                            </label>

                                            <div class="space"></div>

                                            <div class="clearfix">
                                                <button id="loginButn" type="button"
                                                        class="width-35 pull-right btn btn-sm btn-primary">
                                                    <i class="ace-icon fa fa-key"></i>
                                                    <span class="bigger-110">登 录</span>
                                                </button>
                                            </div>

                                            <div class="space-4"></div>
                                        </fieldset>
                                    </form>

                                    <div class="social-or-login center"></div>

                                    <div class="space-6"></div>
                                    <div id="msg-wrap"
                                         style="min-height: 23px;margin-top: 5px;margin-bottom: 5px;height: auto!important;">
                                        <div style="text-align: center;"><span>&copy; 立群工作室</span></div>
                                        <div id="msg-error">
                                            <i class="fa fa-times-circle"></i>
                                            <span></span>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.widget-main -->
                            </div>
                            <!-- /.widget-body -->
                        </div>
                        <!-- /.login-box -->
                    </div>
                    <!-- /.position-relative -->
                </div>
            </div>
            <!-- /.col -->
        </div>
        <!-- /.row -->
    </div>
    <!-- /.main-content -->
</div>
<!-- /.main-container -->
<%@ include file="../base/foot.jsp"%>
<!-- inline scripts related to this page -->
<script type="text/javascript">
    jQuery(function ($) {
        $(document).on('click', '.toolbar a[data-target]', function (e) {
            e.preventDefault();
            var target = $(this).data('target');
            $('.widget-box.visible').removeClass('visible'); //hide others
            $(target).addClass('visible'); //show target
        });

        $("#loginForm").validate({
            errorElement: "em",
            errorPlacement: function (error, element) {
                // Add the `help-block` class to the error element
                error.addClass("help-block");

                if (element.prop("type") === "checkbox") {
                    error.insertAfter(element.parent("label"));
                } else {
                    error.insertAfter(element);
                }
            },
            highlight: function (element, errorClass, validClass) {
                $(element).parents(".block").addClass("has-error").removeClass("has-success");
            },
            unhighlight: function (element, errorClass, validClass) {
                $(element).parents(".block").addClass("has-success").removeClass("has-error");
            },
            rules: {
                loginName: "required",
                password: "required"
            }

        });

        $("#loginButn").on('click', function () {

            if ($("#loginForm").valid()) {
                var remember = "";
                if ($("#remember").prop('checked')) {
                    var remember = 1;
                }

                $.post("${ctx}/login", {
                    username: $("#loginName").val(),
                    password: $("#password").val()
                }, function (data) {
                    if (!data.results) {
                        $("#msg-error").css("visibility", "visible");
                        $("#msg-error span").text(data.message);
                        return;
                    }
                    var result = data.results;
                    sessionStorage.setItem("token", result.token);
                    sessionStorage.setItem("loginName", result.loginName);
                    sessionStorage.setItem("loginId", result.id);
                    sessionStorage.setItem("expire", new Date().getTime());
                    location.href = "${ctx}/index";
                })
            }
        });

        $(document).keydown(function (event) {
            if (event.keyCode == 13) {
                $("#loginButn").click();
            }
        });
    });
</script>
</body>
</html>
