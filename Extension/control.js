var presentationController = {};

presentationController.raiseMouseEvent = function (target, typeArg) {
	var event = document.createEvent('MouseEvents'); 
	event.initMouseEvent(typeArg, true, true); 
	target.dispatchEvent(event); 
};

presentationController.showSlideByNumber = function(slideNumber) {
	var elementToClick = null;
	var filmStrip = document.getElementById('filmstrip');  

	if(filmStrip == null) {
		var a = document.getElementsByClassName('footer-menu-item'); 
		if (slideNumber < 1) { 
			slideNumber = 1; 
		}
	
		var slideCount = 0;
	
		for(var count = 0; count < a.length; count++) {
			if (a[count].innerText.indexOf(':') >= 0) { 
				var slide = parseInt(a[count].innerText.substring(0, a[count].innerText.indexOf(':')));
				if (!isNaN(slide)) {
					slideCount++;
				}
			}
		}
	
		if (slideNumber > slideCount) { 
			slideNumber = slideCount; 
		}
	
		for(var count = 0; count < a.length; count++) {
			if (a[count].innerText.indexOf(':') >= 0) { 
				var slide = parseInt(a[count].innerText.substring(0, a[count].innerText.indexOf(':')));
				if (!isNaN(slide) && slide == slideNumber) { 
					elementToClick = a[count]; 
					var footerSlideMenuContainer = document.getElementById('footerSlideMenuContainer'); 
					presentationController.raiseMouseEvent(footerSlideMenuContainer , 'mouseover');				
					presentationController.raiseMouseEvent(footerSlideMenuContainer.children[0] , 'mousedown');				
					presentationController.raiseMouseEvent(elementToClick , 'mouseover');				
					presentationController.raiseMouseEvent(elementToClick , 'click');		 
					break; 
				} 
			}
		}
	}
	else
	{
		elementToClick = filmStrip.children[slideNumber < 1 ? 0 : slideNumber > filmStrip.children.length ? filmStrip.children.length - 1 : slideNumber - 1];
		if (elementToClick != null)
		{
			presentationController.raiseMouseEvent(elementToClick , 'mousedown');
			presentationController.raiseMouseEvent(elementToClick , 'mouseup');		
		}
	}
};

presentationController.showFirst = function () {
	presentationController.showSlideByNumber(1);
};

presentationController.showNext = function () {
	var nextButton = document.getElementById('ToolbarNext'); 
	if (!nextButton) nextButton = document.getElementById('NEXT_SLIDE'); 
	if(nextButton) { 
		presentationController.raiseMouseEvent(nextButton , 'mousedown');
		presentationController.raiseMouseEvent(nextButton , 'mouseup');		
	}
};

presentationController.showPrev = function () {
	var prevButton = document.getElementById('ToolbarPrev'); 
	if (!prevButton) prevButton = document.getElementById('PREVIOUS_SLIDE'); 
	if(prevButton) { 
		presentationController.raiseMouseEvent(prevButton , 'mousedown');
		presentationController.raiseMouseEvent(prevButton , 'mouseup');		
	}
};

presentationController.registerEventListeners = function () {
	document.addEventListener("getnext", presentationController.showNext, false);
	document.addEventListener("getprev", presentationController.showPrev, false);
	document.addEventListener("getfirst", presentationController.showFirst, false);
	document.addEventListener("getnumber", function (event) { console.dir(event); presentationController.showSlideByNumber(event.detail); }, false);
};

presentationController.registerEventListeners();

 