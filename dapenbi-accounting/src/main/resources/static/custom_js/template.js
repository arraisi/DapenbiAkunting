var stompClient = null;

jQuery(document).ready(function () {
	var currentPage = document.title.split("|");
    $(document).on('show.bs.modal', '.modal', function (event) {
        var zIndex = 1040 + (10 * $('.modal:visible').length);
        $(this).css('z-index', zIndex);
        setTimeout(function () {
            $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
        }, 0);
    });

    $('#btnSignOut').attr("href", _baseUrl + "/logout");
    
    $('#cardBackground').css("background-image", "url(" + _baseUrl + "/metronic/media/misc/bg-1.jpg)");
    
    $('#currentPage').html(currentPage[currentPage.length - 1].trim());
    
    buildUrl();

	reLogin();
    idleAction();
});

function buildUrl(){
	var url = getUrl(window.location.href);

    if(url.host === LOCAL_URL || url.hostname === 'localhost'){
    	API_BASE_URL = PROTOCOL + LOCAL_URL + END_POINT; 
    } else if(url.hostname === LOCAL_HOSTNAME) {
		API_BASE_URL = PROTOCOL + LOCAL_HOSTNAME + END_POINT;
	} else {
    	API_BASE_URL = PROTOCOL + PUBLIC_URL + END_POINT; 
    }
    
    setTimeout(function(){
    	buildMenu();
        getUserIdentity();
    }, 1200);
}

function buildMenu(){
	$.get(API_BASE_URL + "/api/common/getUserMenu",
    {
        programName: PROGRAM_NAME,
        flagFunction: true,
        nip: _userLoggedIn,
        flagVisible: true
    }
    , function (response) {
        var toAppend = '';
        $.when(
    		buildFunctionMenu(response.data)
        ).done(function () {
            $.when(
            	buildParent(response.dataParent)
            ).done(function (){
            	buildFunctionWithParent(response.data)
            });
        });
    }, "json");
}

function buildFunctionMenu(data){
	$.each(data, function (index, item) {
    	if(item.menu.menuParent === null){
    		toAppend = '<li id="menu-' + item.menu.menuId + '" class="kt-menu__item kt-menu__item--submenu"><a href="' + _baseUrl + item.menu.menuUrl + '" class="kt-menu__link kt-menu__toggle"><i class="kt-menu__link-icon ' +
            item.menu.menuIcon + '"></i><span class="kt-menu__link-text">' + item.menu.menuName + '</span></a></li>';
    		$("#sidebarMenu").append(toAppend);
    	} 
    });
}

function buildFunctionWithParent(data){
	$.each(data, function (index, item) {
    	if(item.menu.menuParent !== null){
    		toAppend = '<li class="kt-menu__item " aria-haspopup="true">' +
    	    '<a href="' + _baseUrl + item.menu.menuUrl + '" class="kt-menu__link ">' +
    	    '<i class="kt-menu__link-bullet kt-menu__link-bullet--dot"><span></span></i>' +
    	    '<span class="kt-menu__link-text">' + item.menu.menuName + '</span>' +
    	    '</a></li>';
    		$('#ulMenu-' + item.menu.menuParent.menuId).append(toAppend);
    	} 
    });
}

function buildParent(dataParent){
	if(dataParent[0] !== null){
        $.each(dataParent, function (index, item) {
            toAppend = '<li id="menu-' + item.menuId + '" class="kt-menu__item kt-menu__item--submenu"><a href="javascript:void(0);" class="kt-menu__link kt-menu__toggle"><i class="kt-menu__link-icon ' +
            item.menuIcon + '"></i><span class="kt-menu__link-text">' + item.menuName + '</span></a></li>';
            
        	if(item.menuParent !== null){
        		toAppend = '<li id="menu-' + item.menuParent.menuId + '" class="kt-menu__item kt-menu__item--submenu"><a href="javascript:void(0);" class="kt-menu__link kt-menu__toggle"><i class="kt-menu__link-icon ' +
                item.menuParent.menuIcon + '"></i><span class="kt-menu__link-text">' + item.menuParent.menuName + '</span></a></li>';
        	}
        	
            if(!$('#menu-' + item.menuId).length){
                $.when(
                		appendMenuItem(item, toAppend)
                ).done(function () {
                    toAppend = '<div class="kt-menu__submenu " kt-hidden-height="840" style="">' +
                        '<span class="kt-menu__arrow"></span>' +
                        '<ul class="kt-menu__subnav" id="ulMenu-' + item.menuId + '">' +
                        '<li class="kt-menu__item  kt-menu__item--parent" aria-haspopup="true">' +
                        '<span class="kt-menu__link"><span class="kt-menu__link-text">' + item.menuName + '</span></span>' +
                        '</li></ul></div>';
                    
                    if(item.menuParent != null){
                    	toAppend = '<div class="kt-menu__submenu " kt-hidden-height="840" style="">' +
                        '<span class="kt-menu__arrow"></span>' +
                        '<ul class="kt-menu__subnav" id="ulMenu-' + item.menuParent.menuId + '">' +
                        '<li class="kt-menu__item  kt-menu__item--parent" aria-haspopup="true">' +
                        '<span class="kt-menu__link"><span class="kt-menu__link-text">' + item.menuParent.menuName + '</span></span>' +
                        '</li></ul></div>';
                    	
                    	$('#menu-' + item.menuParent.menuId).append(toAppend);
                    } else {
                    	$('#menu-' + item.menuId).append(toAppend);
                    }
                    
                    $.when(
                    		appendRightArrow(item)
                    ).done(function(){
                    	if(item.menuParent !== null){
                    		toAppend = '<li id="menu-' + item.menuId + '" class="kt-menu__item kt-menu__item--submenu"><a href="javascript:void(0);" class="kt-menu__link kt-menu__toggle"><i class="kt-menu__link-icon ' +
                            item.menuIcon + '"></i><span class="kt-menu__link-text">' + item.menuName + '</span></a></li>';
                    		
                    		$.when(
                				appendSubMenu(item, toAppend)
                    		).done(function(){
                    			toAppend = '<div class="kt-menu__submenu " kt-hidden-height="840" style="">' +
    	                        '<span class="kt-menu__arrow"></span>' +
    	                        '<ul class="kt-menu__subnav" id="ulMenu-' + item.menuId + '">' +
    	                        '<li class="kt-menu__item  kt-menu__item--parent" aria-haspopup="true">' +
    	                        '<span class="kt-menu__link"><span class="kt-menu__link-text">' + item.menuName + '</span></span>' +
    	                        '</li></ul></div>';
                    			$('#menu-' + item.menuId).append(toAppend);
                    			$('#menu-' + item.menuId).find('a').append('<i class="kt-menu__ver-arrow la la-angle-right"></i>');
                    		});
	                    }
                    })
                });
            }
        });
	} 
}

function getUserIdentity() {
    $.get(API_BASE_URL + "/api/common/getUserIdentity", {
        nip:_userLoggedIn
    }, function (data) {    	
    	var name = data.name;
    	var nip = data.nip;
    	
        $('#lblUserName').html(name);
        $('#cardUserName').html(name + " - " + nip);

        var words = name.split(" ");
        var char = '';
        for (i = 0; i < words.length; i++) {
            char += words[i].charAt(0);
        }
        $('#lblUserInitial').html(char);
        $('#cardUserInitial').html(char);
    });
}

function appendMenuItem(item, toAppend){
	if(item.menuParent !== null){
		if(!$('#menu-' + item.menuParent.menuId).length)
			$("#sidebarMenu").append(toAppend);
	} else {
		if(!$('#menu-' + item.menuId).length)
			$("#sidebarMenu").append(toAppend);
	}
}

function appendSubMenu(item, toAppend){
	if(!$('#menu-' + item.menuId).length){
		$('#ulMenu-' + item.menuParent.menuId).append(toAppend)
	}
}

function appendRightArrow(item){
	if(item.menuParent !== null){
		if(!$('#menu-' + item.menuParent.menuId).find(".kt-menu__ver-arrow").length)
			$('#menu-' + item.menuParent.menuId).find('a').append('<i class="kt-menu__ver-arrow la la-angle-right"></i>')
	}
	else{
		if(!$('#menu-' + item.menuId).find(".kt-menu__ver-arrow").length)
			$('#menu-' + item.menuId).find('a').append('<i class="kt-menu__ver-arrow la la-angle-right"></i>')
	}
}

function getUrl(url) {
    var l = document.createElement("a");
    l.href = url;
    return l;
}

function reLogin(){
	$('#modalRelogin').find('form').validate({
		rules: {
			username: {
				required: true
			},
			password: {
				required: true
			}
		},
		submitHandler: function (form) {
			$.post(_baseUrl + '/login', {
				username: $('#txtUsername').val(),
				password: $('#txtPassword').val(),
				fromPage: $("#txtFromPage").val()
			}, function (data) {
				$('#modalRelogin').modal('toggle');
			}).fail(function(){
				showError("Login gagal!")
			});

			return false;
		}
	});
}

function checkSession(){
	$.post(_baseUrl + '/session/check', {

	}, function (response) {
		if(response == false) {
			$('#modalRelogin').modal({
				backdrop: 'static',
				keyboard: false
			});
		}
	}).fail(function(){
		$('#modalNoConnection').modal('show');
	});
}

function idleAction(){
	$(document).idleTimer( 10000 );
	let intervalSession;
	$(document).on( "idle.idleTimer", function(event, elem, obj){
		intervalSession = setInterval(function(){
			checkSession();
		}, 3000);
	});

	$(document).on( "active.idleTimer", function(event, elem, obj, triggerevent){
		clearInterval(intervalSession);
	});


	$('#currentPage').on('click', function(){
		$('#modalRelogin').modal({
			backdrop: 'static',
			keyboard: false
		});
	});
}