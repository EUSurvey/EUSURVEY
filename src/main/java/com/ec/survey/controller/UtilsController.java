package com.ec.survey.controller;

import com.ec.survey.tools.Tools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/utils")
public class UtilsController extends BasicController {
	
	public @Value("${captcha.bypass:@null}") String bypassCaptcha;
	
	@RequestMapping(value = "/euCountries", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Map<String, String> getListOfCountries(HttpServletRequest request) {
		Locale locale = new Locale(request.getParameter("lang"));
		
		Map<String, String> countryList = new HashMap<>();

		countryList.put("AT", resources.getMessage("label.country.Austria", null, locale));
		countryList.put("BE", resources.getMessage("label.country.Belgium", null, locale));
		countryList.put("BG", resources.getMessage("label.country.Bulgaria", null, locale));
		countryList.put("CY", resources.getMessage("label.country.Cyprus", null, locale));
		countryList.put("CZ", resources.getMessage("label.un.Czechia", null, locale));
		countryList.put("DE", resources.getMessage("label.country.Germany", null, locale));
		countryList.put("DK", resources.getMessage("label.country.Denmark", null, locale));
		countryList.put("EE", resources.getMessage("label.country.Estonia", null, locale));
		countryList.put("EL", resources.getMessage("label.country.Greece", null, locale));
		countryList.put("ES", resources.getMessage("label.country.Spain", null, locale));
		countryList.put("FI", resources.getMessage("label.country.Finland", null, locale));
		countryList.put("FR", resources.getMessage("label.country.France", null, locale));
		countryList.put("HU", resources.getMessage("label.country.Hungary", null, locale));
		countryList.put("HR", resources.getMessage("label.country.Croatia", null, locale));
		countryList.put("IE", resources.getMessage("label.country.Ireland", null, locale));
		countryList.put("IR", resources.getMessage("label.country.Italy", null, locale));
		countryList.put("LT", resources.getMessage("label.country.Lithuania", null, locale));
		countryList.put("LU", resources.getMessage("label.country.Luxembourg", null, locale));
		countryList.put("LV", resources.getMessage("label.country.Latvia", null, locale));
		countryList.put("MT", resources.getMessage("label.country.Malta", null, locale));
		countryList.put("NL", resources.getMessage("label.country.Netherlands", null, locale));
		countryList.put("PL", resources.getMessage("label.country.Poland", null, locale));
		countryList.put("PT", resources.getMessage("label.country.Portugal", null, locale));
		countryList.put("RO", resources.getMessage("label.country.Romania", null, locale));
		countryList.put("SE", resources.getMessage("label.country.Sweden", null, locale));
		countryList.put("SI", resources.getMessage("label.country.Slovenia", null, locale));
		countryList.put("SK", resources.getMessage("label.country.SlovakRepublic", null, locale));
				
		countryList = Tools.sortByComparator(countryList);
		return countryList;
	}
	
	@RequestMapping(value = "/euLanguages", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Map<String, String> getListOfLanguages(HttpServletRequest request) {
		Locale locale = new Locale(request.getParameter("lang"));
		
		Map<String, String> langList = new HashMap<>();

		langList.put("BG", resources.getMessage("label.lang.Bulgarian", null, locale));
		langList.put("CS", resources.getMessage("label.lang.Czech", null, locale));
		langList.put("DA", resources.getMessage("label.lang.Danish", null, locale));
		langList.put("DE", resources.getMessage("label.lang.German", null, locale));
		langList.put("EL", resources.getMessage("label.lang.Greek", null, locale));
		langList.put("EN", resources.getMessage("label.lang.English", null, locale));
		langList.put("ES", resources.getMessage("label.lang.Spanish", null, locale));
		langList.put("ET", resources.getMessage("label.lang.Estonian", null, locale));
		langList.put("FI", resources.getMessage("label.lang.Finnish", null, locale));
		langList.put("FR", resources.getMessage("label.lang.French", null, locale));
		langList.put("GA", resources.getMessage("label.lang.Gaelic", null, locale));
		langList.put("HR", resources.getMessage("label.lang.Croatian", null, locale));
		langList.put("HU", resources.getMessage("label.lang.Hungarian", null, locale));
		langList.put("IT", resources.getMessage("label.lang.Italian", null, locale));
		langList.put("LT", resources.getMessage("label.lang.Lithuanian", null, locale));
		langList.put("LV", resources.getMessage("label.lang.Latvian", null, locale));
		langList.put("MT", resources.getMessage("label.lang.Maltese", null, locale));
		langList.put("NL", resources.getMessage("label.lang.Dutch", null, locale));
		langList.put("PL", resources.getMessage("label.lang.Polish", null, locale));
		langList.put("PT", resources.getMessage("label.lang.Portuguese", null, locale));
		langList.put("RO", resources.getMessage("label.lang.Romanian", null, locale));
		langList.put("SK", resources.getMessage("label.lang.Slovak", null, locale));
		langList.put("SL", resources.getMessage("label.lang.Slovenian", null, locale));
		langList.put("SV", resources.getMessage("label.lang.Swedish", null, locale));	
		
		langList = Tools.sortByComparator(langList);
		return langList;
	}
	
	@RequestMapping(value = "/unCountries", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Map<String, String> getListOfUNCountries(HttpServletRequest request) {
		Locale locale = new Locale(request.getParameter("lang"));
	
		Map<String, String> countryList = new HashMap<>();
	
		countryList.put("Afghanistan", resources.getMessage("label.un.Afghanistan", null, locale));
		countryList.put("Albania", resources.getMessage("label.un.Albania", null, locale));
		countryList.put("Algeria", resources.getMessage("label.un.Algeria", null, locale));
		countryList.put("Andorra", resources.getMessage("label.un.Andorra", null, locale));
		countryList.put("Angola", resources.getMessage("label.un.Angola", null, locale));
		countryList.put("AntiguaandBarbuda", resources.getMessage("label.un.AntiguaandBarbuda", null, locale));
		countryList.put("Argentina", resources.getMessage("label.un.Argentina", null, locale));
		countryList.put("Armenia", resources.getMessage("label.un.Armenia", null, locale));
		countryList.put("Australia", resources.getMessage("label.un.Australia", null, locale));
		countryList.put("Austria", resources.getMessage("label.un.Austria", null, locale));
		countryList.put("Azerbaijan", resources.getMessage("label.un.Azerbaijan", null, locale));
		countryList.put("Bahamas", resources.getMessage("label.un.Bahamas", null, locale));
		countryList.put("Bahrain", resources.getMessage("label.un.Bahrain", null, locale));
		countryList.put("Bangladesh", resources.getMessage("label.un.Bangladesh", null, locale));
		countryList.put("Barbados", resources.getMessage("label.un.Barbados", null, locale));
		countryList.put("Belarus", resources.getMessage("label.un.Belarus", null, locale));
		countryList.put("Belgium", resources.getMessage("label.un.Belgium", null, locale));
		countryList.put("Belize", resources.getMessage("label.un.Belize", null, locale));
		countryList.put("Benin", resources.getMessage("label.un.Benin", null, locale));
		countryList.put("Bhutan", resources.getMessage("label.un.Bhutan", null, locale));
		countryList.put("Bolivia", resources.getMessage("label.un.Bolivia", null, locale));
		countryList.put("BosniaandHerzegovina", resources.getMessage("label.un.BosniaandHerzegovina", null, locale));
		countryList.put("Botswana", resources.getMessage("label.un.Botswana", null, locale));
		countryList.put("Brazil", resources.getMessage("label.un.Brazil", null, locale));
		countryList.put("BruneiDarussalam", resources.getMessage("label.un.BruneiDarussalam", null, locale));
		countryList.put("Bulgaria", resources.getMessage("label.un.Bulgaria", null, locale));
		countryList.put("BurkinaFaso", resources.getMessage("label.un.BurkinaFaso", null, locale));
		countryList.put("Burundi", resources.getMessage("label.un.Burundi", null, locale));
		countryList.put("CaboVerde", resources.getMessage("label.un.CaboVerde", null, locale));
		countryList.put("Cambodia", resources.getMessage("label.un.Cambodia", null, locale));
		countryList.put("Cameroon", resources.getMessage("label.un.Cameroon", null, locale));
		countryList.put("Canada", resources.getMessage("label.un.Canada", null, locale));
		countryList.put("CentralAfricanRepublic", resources.getMessage("label.un.CentralAfricanRepublic", null, locale));
		countryList.put("Chad", resources.getMessage("label.un.Chad", null, locale));
		countryList.put("Chile", resources.getMessage("label.un.Chile", null, locale));
		countryList.put("China", resources.getMessage("label.un.China", null, locale));
		countryList.put("Colombia", resources.getMessage("label.un.Colombia", null, locale));
		countryList.put("Comoros", resources.getMessage("label.un.Comoros", null, locale));
		countryList.put("Congo", resources.getMessage("label.un.Congo", null, locale));
		countryList.put("CostaRica", resources.getMessage("label.un.CostaRica", null, locale));
		countryList.put("CôteD'Ivoire", resources.getMessage("label.un.CoteDIvoire", null, locale));
		countryList.put("Croatia", resources.getMessage("label.un.Croatia", null, locale));
		countryList.put("Cuba", resources.getMessage("label.un.Cuba", null, locale));
		countryList.put("Cyprus", resources.getMessage("label.un.Cyprus", null, locale));
		countryList.put("Czechia", resources.getMessage("label.un.Czechia", null, locale));
		countryList.put("NorthKorea", resources.getMessage("label.un.NorthKorea", null, locale));
		countryList.put("DemocraticRepublicoftheCongo", resources.getMessage("label.un.DemocraticRepublicoftheCongo", null, locale));
		countryList.put("Denmark", resources.getMessage("label.un.Denmark", null, locale));
		countryList.put("Djibouti", resources.getMessage("label.un.Djibouti", null, locale));
		countryList.put("Dominica", resources.getMessage("label.un.Dominica", null, locale));
		countryList.put("DominicanRepublic", resources.getMessage("label.un.DominicanRepublic", null, locale));
		countryList.put("Ecuador", resources.getMessage("label.un.Ecuador", null, locale));
		countryList.put("Egypt", resources.getMessage("label.un.Egypt", null, locale));
		countryList.put("ElSalvador", resources.getMessage("label.un.ElSalvador", null, locale));
		countryList.put("EquatorialGuinea", resources.getMessage("label.un.EquatorialGuinea", null, locale));
		countryList.put("Eritrea", resources.getMessage("label.un.Eritrea", null, locale));
		countryList.put("Estonia", resources.getMessage("label.un.Estonia", null, locale));
		countryList.put("Ethiopia", resources.getMessage("label.un.Ethiopia", null, locale));
		countryList.put("Fiji", resources.getMessage("label.un.Fiji", null, locale));
		countryList.put("Finland", resources.getMessage("label.un.Finland", null, locale));
		countryList.put("France", resources.getMessage("label.un.France", null, locale));
		countryList.put("Gabon", resources.getMessage("label.un.Gabon", null, locale));
		countryList.put("Gambia", resources.getMessage("label.un.Gambia", null, locale));
		countryList.put("Georgia", resources.getMessage("label.un.Georgia", null, locale));
		countryList.put("Germany", resources.getMessage("label.un.Germany", null, locale));
		countryList.put("Ghana", resources.getMessage("label.un.Ghana", null, locale));
		countryList.put("Greece", resources.getMessage("label.un.Greece", null, locale));
		countryList.put("Grenada", resources.getMessage("label.un.Grenada", null, locale));
		countryList.put("Guatemala", resources.getMessage("label.un.Guatemala", null, locale));
		countryList.put("Guinea", resources.getMessage("label.un.Guinea", null, locale));
		countryList.put("GuineaBissau", resources.getMessage("label.un.GuineaBissau", null, locale));
		countryList.put("Guyana", resources.getMessage("label.un.Guyana", null, locale));
		countryList.put("Haiti", resources.getMessage("label.un.Haiti", null, locale));
		countryList.put("Honduras", resources.getMessage("label.un.Honduras", null, locale));
		countryList.put("Hungary", resources.getMessage("label.un.Hungary", null, locale));
		countryList.put("Iceland", resources.getMessage("label.un.Iceland", null, locale));
		countryList.put("India", resources.getMessage("label.un.India", null, locale));
		countryList.put("Indonesia", resources.getMessage("label.un.Indonesia", null, locale));
		countryList.put("Iran", resources.getMessage("label.un.Iran", null, locale));
		countryList.put("Iraq", resources.getMessage("label.un.Iraq", null, locale));
		countryList.put("Ireland", resources.getMessage("label.un.Ireland", null, locale));
		countryList.put("Israel", resources.getMessage("label.un.Israel", null, locale));
		countryList.put("Italy", resources.getMessage("label.un.Italy", null, locale));
		countryList.put("Jamaica", resources.getMessage("label.un.Jamaica", null, locale));
		countryList.put("Japan", resources.getMessage("label.un.Japan", null, locale));
		countryList.put("Jordan", resources.getMessage("label.un.Jordan", null, locale));
		countryList.put("Kazakhstan", resources.getMessage("label.un.Kazakhstan", null, locale));
		countryList.put("Kenya", resources.getMessage("label.un.Kenya", null, locale));
		countryList.put("Kiribati", resources.getMessage("label.un.Kiribati", null, locale));
		countryList.put("Kuwait", resources.getMessage("label.un.Kuwait", null, locale));
		countryList.put("Kyrgyzstan", resources.getMessage("label.un.Kyrgyzstan", null, locale));
		countryList.put("Laos", resources.getMessage("label.un.Laos", null, locale));
		countryList.put("Latvia", resources.getMessage("label.un.Latvia", null, locale));
		countryList.put("Lebanon", resources.getMessage("label.un.Lebanon", null, locale));
		countryList.put("Lesotho", resources.getMessage("label.un.Lesotho", null, locale));
		countryList.put("Liberia", resources.getMessage("label.un.Liberia", null, locale));
		countryList.put("Libya", resources.getMessage("label.un.Libya", null, locale));
		countryList.put("Liechtenstein", resources.getMessage("label.un.Liechtenstein", null, locale));
		countryList.put("Lithuania", resources.getMessage("label.un.Lithuania", null, locale));
		countryList.put("Luxembourg", resources.getMessage("label.un.Luxembourg", null, locale));
		countryList.put("Madagascar", resources.getMessage("label.un.Madagascar", null, locale));
		countryList.put("Malawi", resources.getMessage("label.un.Malawi", null, locale));
		countryList.put("Malaysia", resources.getMessage("label.un.Malaysia", null, locale));
		countryList.put("Maldives", resources.getMessage("label.un.Maldives", null, locale));
		countryList.put("Mali", resources.getMessage("label.un.Mali", null, locale));
		countryList.put("Malta", resources.getMessage("label.un.Malta", null, locale));
		countryList.put("MarshallIslands", resources.getMessage("label.un.MarshallIslands", null, locale));
		countryList.put("Mauritania", resources.getMessage("label.un.Mauritania", null, locale));
		countryList.put("Mauritius", resources.getMessage("label.un.Mauritius", null, locale));
		countryList.put("Mexico", resources.getMessage("label.un.Mexico", null, locale));
		countryList.put("Micronesia", resources.getMessage("label.un.Micronesia", null, locale));
		countryList.put("Monaco", resources.getMessage("label.un.Monaco", null, locale));
		countryList.put("Mongolia", resources.getMessage("label.un.Mongolia", null, locale));
		countryList.put("Montenegro", resources.getMessage("label.un.Montenegro", null, locale));
		countryList.put("Morocco", resources.getMessage("label.un.Morocco", null, locale));
		countryList.put("Mozambique", resources.getMessage("label.un.Mozambique", null, locale));
		countryList.put("Myanmar", resources.getMessage("label.un.Myanmar", null, locale));
		countryList.put("Namibia", resources.getMessage("label.un.Namibia", null, locale));
		countryList.put("Nauru", resources.getMessage("label.un.Nauru", null, locale));
		countryList.put("Nepal", resources.getMessage("label.un.Nepal", null, locale));
		countryList.put("Netherlands", resources.getMessage("label.un.Netherlands", null, locale));
		countryList.put("NewZealand", resources.getMessage("label.un.NewZealand", null, locale));
		countryList.put("Nicaragua", resources.getMessage("label.un.Nicaragua", null, locale));
		countryList.put("Niger", resources.getMessage("label.un.Niger", null, locale));
		countryList.put("Nigeria", resources.getMessage("label.un.Nigeria", null, locale));
		countryList.put("Norway", resources.getMessage("label.un.Norway", null, locale));
		countryList.put("Oman", resources.getMessage("label.un.Oman", null, locale));
		countryList.put("Pakistan", resources.getMessage("label.un.Pakistan", null, locale));
		countryList.put("Palau", resources.getMessage("label.un.Palau", null, locale));
		countryList.put("Panama", resources.getMessage("label.un.Panama", null, locale));
		countryList.put("PapuaNewGuinea", resources.getMessage("label.un.PapuaNewGuinea", null, locale));
		countryList.put("Paraguay", resources.getMessage("label.un.Paraguay", null, locale));
		countryList.put("Peru", resources.getMessage("label.un.Peru", null, locale));
		countryList.put("Philippines", resources.getMessage("label.un.Philippines", null, locale));
		countryList.put("Poland", resources.getMessage("label.un.Poland", null, locale));
		countryList.put("Portugal", resources.getMessage("label.un.Portugal", null, locale));
		countryList.put("Qatar", resources.getMessage("label.un.Qatar", null, locale));
		countryList.put("SouthKorea", resources.getMessage("label.un.SouthKorea", null, locale));
		countryList.put("RepublicMoldova", resources.getMessage("label.un.RepublicMoldova", null, locale));
		countryList.put("Romania", resources.getMessage("label.un.Romania", null, locale));
		countryList.put("RussianFederation", resources.getMessage("label.un.RussianFederation", null, locale));
		countryList.put("Rwanda", resources.getMessage("label.un.Rwanda", null, locale));
		countryList.put("SaintKittsandNevis", resources.getMessage("label.un.SaintKittsandNevis", null, locale));
		countryList.put("SaintLucia", resources.getMessage("label.un.SaintLucia", null, locale));
		countryList.put("SaintVincentandtheGrenadines", resources.getMessage("label.un.SaintVincentandtheGrenadines", null, locale));
		countryList.put("Samoa", resources.getMessage("label.un.Samoa", null, locale));
		countryList.put("SanMarino", resources.getMessage("label.un.SanMarino", null, locale));
		countryList.put("SaoTomeandPrincipe", resources.getMessage("label.un.SaoTomeandPrincipe", null, locale));
		countryList.put("SaudiArabia", resources.getMessage("label.un.SaudiArabia", null, locale));
		countryList.put("Senegal", resources.getMessage("label.un.Senegal", null, locale));
		countryList.put("Serbia", resources.getMessage("label.un.Serbia", null, locale));
		countryList.put("Seychelles", resources.getMessage("label.un.Seychelles", null, locale));
		countryList.put("SierraLeone", resources.getMessage("label.un.SierraLeone", null, locale));
		countryList.put("Singapore", resources.getMessage("label.un.Singapore", null, locale));
		countryList.put("Slovakia", resources.getMessage("label.un.Slovakia", null, locale));
		countryList.put("Slovenia", resources.getMessage("label.un.Slovenia", null, locale));
		countryList.put("SolomonIslands", resources.getMessage("label.un.SolomonIslands", null, locale));
		countryList.put("Somalia", resources.getMessage("label.un.Somalia", null, locale));
		countryList.put("SouthAfrica", resources.getMessage("label.un.SouthAfrica", null, locale));
		countryList.put("South?Sudan", resources.getMessage("label.un.SouthSudan", null, locale));
		countryList.put("Spain", resources.getMessage("label.un.Spain", null, locale));
		countryList.put("SriLanka", resources.getMessage("label.un.SriLanka", null, locale));
		countryList.put("Sudan", resources.getMessage("label.un.Sudan", null, locale));
		countryList.put("Suriname", resources.getMessage("label.un.Suriname", null, locale));
		countryList.put("Eswatini", resources.getMessage("label.un.Eswatini", null, locale));
		countryList.put("Sweden", resources.getMessage("label.un.Sweden", null, locale));
		countryList.put("Switzerland", resources.getMessage("label.un.Switzerland", null, locale));
		countryList.put("SyrianArabRepublic", resources.getMessage("label.un.SyrianArabRepublic", null, locale));
		countryList.put("Tajikistan", resources.getMessage("label.un.Tajikistan", null, locale));
		countryList.put("Thailand", resources.getMessage("label.un.Thailand", null, locale));
		countryList.put("NorthMacedonia", resources.getMessage("label.un.NorthMacedonia", null, locale));
		countryList.put("Timor-Leste", resources.getMessage("label.un.Timor-Leste", null, locale));
		countryList.put("Togo", resources.getMessage("label.un.Togo", null, locale));
		countryList.put("Tonga", resources.getMessage("label.un.Tonga", null, locale));
		countryList.put("TrinidadandTobago", resources.getMessage("label.un.TrinidadandTobago", null, locale));
		countryList.put("Tunisia", resources.getMessage("label.un.Tunisia", null, locale));
		countryList.put("Turkey", resources.getMessage("label.un.Turkey", null, locale));
		countryList.put("Turkmenistan", resources.getMessage("label.un.Turkmenistan", null, locale));
		countryList.put("Tuvalu", resources.getMessage("label.un.Tuvalu", null, locale));
		countryList.put("Uganda", resources.getMessage("label.un.Uganda", null, locale));
		countryList.put("Ukraine", resources.getMessage("label.un.Ukraine", null, locale));
		countryList.put("UnitedArabEmirates", resources.getMessage("label.un.UnitedArabEmirates", null, locale));
		countryList.put("UnitedKingdom", resources.getMessage("label.un.UnitedKingdom", null, locale));
		countryList.put("Tanzania", resources.getMessage("label.un.Tanzania", null, locale));
		countryList.put("UnitedStatesofAmerica", resources.getMessage("label.un.UnitedStatesofAmerica", null, locale));
		countryList.put("Uruguay", resources.getMessage("label.un.Uruguay", null, locale));
		countryList.put("Uzbekistan", resources.getMessage("label.un.Uzbekistan", null, locale));
		countryList.put("Vanuatu", resources.getMessage("label.un.Vanuatu", null, locale));
		countryList.put("Venezuela", resources.getMessage("label.un.Venezuela", null, locale));
		countryList.put("VietNam", resources.getMessage("label.un.VietNam", null, locale));
		countryList.put("Yemen", resources.getMessage("label.un.Yemen", null, locale));
		countryList.put("Zambia", resources.getMessage("label.un.Zambia", null, locale));
		countryList.put("Zimbabwe", resources.getMessage("label.un.Zimbabwe", null, locale));
		
		countryList = Tools.sortByComparator(countryList);
		return countryList;
	}
	
	@RequestMapping(value = "/euDGs", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Map<String, String> getListOfDGs(HttpServletRequest request) {
		Locale locale = new Locale(request.getParameter("lang"));
		
		Map<String, String> dgList = new HashMap<>();

		dgList.put("SG",resources.getMessage("label.dg.SG",null,locale));
		dgList.put("SJ",resources.getMessage("label.dg.SJ",null,locale));
		dgList.put("COMM",resources.getMessage("label.dg.COMM",null,locale));
		dgList.put("EPSC",resources.getMessage("label.dg.EPSC",null,locale));
		dgList.put("ECFIN",resources.getMessage("label.dg.ECFIN",null,locale));
		dgList.put("GROW",resources.getMessage("label.dg.GROW",null,locale));
		dgList.put("COMP",resources.getMessage("label.dg.COMP",null,locale));
		dgList.put("EMPL",resources.getMessage("label.dg.EMPL",null,locale));
		dgList.put("AGRI",resources.getMessage("label.dg.AGRI",null,locale));
		dgList.put("ENER",resources.getMessage("label.dg.ENER",null,locale));
		dgList.put("MOVE",resources.getMessage("label.dg.MOVE",null,locale));
		dgList.put("CLIMA",resources.getMessage("label.dg.CLIMA",null,locale));
		dgList.put("ENV",resources.getMessage("label.dg.ENV",null,locale));
		dgList.put("RTD",resources.getMessage("label.dg.RTD",null,locale));
		dgList.put("JRC",resources.getMessage("label.dg.JRC",null,locale));
		dgList.put("CNECT",resources.getMessage("label.dg.CNECT",null,locale));
		dgList.put("MARE",resources.getMessage("label.dg.MARE",null,locale));
		dgList.put("FISMA",resources.getMessage("label.dg.FISMA",null,locale));
		dgList.put("REGIO",resources.getMessage("label.dg.REGIO",null,locale));
		dgList.put("TAXUD",resources.getMessage("label.dg.TAXUD",null,locale));
		dgList.put("EAC",resources.getMessage("label.dg.EAC2",null,locale));
		dgList.put("SANTE",resources.getMessage("label.dg.SANTE",null,locale));
		dgList.put("HOME",resources.getMessage("label.dg.HOME",null,locale));
		dgList.put("JUST",resources.getMessage("label.dg.JUST",null,locale));
		dgList.put("FPI",resources.getMessage("label.dg.FPI",null,locale));
		dgList.put("TRADE",resources.getMessage("label.dg.TRADE",null,locale));
		dgList.put("NEAR",resources.getMessage("label.dg.NEAR",null,locale));
		dgList.put("DEVCO",resources.getMessage("label.dg.DEVCO",null,locale));
		dgList.put("ECHO",resources.getMessage("label.dg.ECHO",null,locale));
		dgList.put("ESTAT",resources.getMessage("label.dg.ESTAT",null,locale));
		dgList.put("HR",resources.getMessage("label.dg.HR",null,locale));
		dgList.put("DIGIT",resources.getMessage("label.dg.DIGIT",null,locale));
		dgList.put("BUDG",resources.getMessage("label.dg.BUDG",null,locale));
		dgList.put("IAS",resources.getMessage("label.dg.IAS",null,locale));
		dgList.put("OLAF",resources.getMessage("label.dg.OLAF",null,locale));
		dgList.put("SCIC",resources.getMessage("label.dg.SCIC",null,locale));
		dgList.put("DGT",resources.getMessage("label.dg.DGT",null,locale));
		dgList.put("OP",resources.getMessage("label.dg.OP",null,locale));
		dgList.put("OIB",resources.getMessage("label.dg.OIB",null,locale));
		dgList.put("PMO",resources.getMessage("label.dg.PMO",null,locale));
		dgList.put("OIL",resources.getMessage("label.dg.OIL",null,locale));
		dgList.put("EPSO",resources.getMessage("label.dg.EPSO",null,locale));
		dgList.put("EASME",resources.getMessage("label.dg.EASME",null,locale));
		dgList.put("EACEA",resources.getMessage("label.dg.EACEA",null,locale));
		dgList.put("CHAFEA",resources.getMessage("label.dg.CHAFEA",null,locale));
		dgList.put("INEA",resources.getMessage("label.dg.INEA",null,locale));
		dgList.put("ERCEA",resources.getMessage("label.dg.ERCEA",null,locale));
		dgList.put("REA",resources.getMessage("label.dg.REA",null,locale));		
		dgList.put("DPO",resources.getMessage("label.dg.DPO",null,locale));
		dgList.put("HAS",resources.getMessage("label.dg.HAS",null,locale));
		dgList.put("LIB",resources.getMessage("label.dg.LIB",null,locale));
		dgList.put("SRSS",resources.getMessage("label.dg.SRSS",null,locale));
		dgList.put("A50TF",resources.getMessage("label.dg.A50TF",null,locale));
			
		dgList = Tools.sortByComparator(dgList);
		return dgList;
	}
	
	@RequestMapping(value = "/config/{key}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String getConfigValue(@PathVariable String key, HttpServletRequest request) {
		String result = "";		
		if(key != null)
		{
			if(key.equals("captchaBypass"))
			{				
				result =  bypassCaptcha;				
			}
		}
		return result;
	}
}
