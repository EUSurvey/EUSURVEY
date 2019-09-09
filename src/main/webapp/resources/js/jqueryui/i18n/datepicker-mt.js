
(function( factory ) {
	if ( typeof define === "function" && define.amd ) {

		// AMD. Register as an anonymous module.
		define([ "../datepicker" ], factory );
	} else {

		// Browser globals
		factory( jQuery.datepicker );
	}
}(function( datepicker ) {

 $.datepicker.regional['mt'] = {
                closeText: 'Ghalaq',
                prevText: 'Ta Qabel',
                nextText: 'Li Jmiss',
                currentText: 'Illum',
                monthNames: ['Jannar','Frar','Marzu','April','Mejju','Gunju',
                'Lulju','Awissu','Settembru','Ottubru','Novembru','Dicembru'],
                monthNamesShort: ['Jan', 'Fra', 'Mar', 'Apr', 'Mej', 'Gun',
                'Lul', 'Awi', 'Set', 'Ott', 'Nov', 'Dic'],
                dayNames: ['Il-Hadd', 'It-Tnejn', 'It-Tlieta', 'L-Erbgha', 'Il-Hamis', 'Il-Gimgha', 'Is-Sibt'],
                dayNamesShort: ['Had', 'Tne', 'Tli', 'Erb', 'Ham', 'Gim', 'Sib'],
                dayNamesMin: ['H','T','T','E','H','G','S'],
                weekHeader: 'Gm',
                dateFormat: 'dd/mm/yy',
                firstDay: 1,
                isRTL: false,
                showMonthAfterYear: false,
                yearSuffix: ''};
        $.datepicker.setDefaults($.datepicker.regional['mt']);

return datepicker.regional['mt'];

}));
