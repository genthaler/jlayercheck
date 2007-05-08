// $Id$

/** main init function, called on document load */
jlayercheckInit = function() {

	// toggles css class on element.
	// if added via YAHOO.util.Event.addListener
	// scope on event call is element and 'this' should work
	var toggleText = function(e) {
		if (YAHOO.util.Dom.hasClass(this, 'clicked')) {
			YAHOO.util.Dom.removeClass(this, 'clicked');
			YAHOO.util.Dom.addClass(this, 'unclicked');
		} else {
			YAHOO.util.Dom.removeClass(this, 'unclicked');
			YAHOO.util.Dom.addClass(this, 'clicked');
		}
	};

	// decorate all msgErr-div blocks
	var elements = YAHOO.util.Dom.getElementsByClassName('msgErr', 'div');
	for(var i = 0; i < elements.length; i++) {
		var element = elements[i];
		// toggle text on click
		YAHOO.util.Event.addListener(element, 'click', toggleText);
	}
   
};
