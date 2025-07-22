package com.ec.survey.service;

import com.ec.survey.model.Organisations;
import com.ec.survey.tools.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("utilsService")
public class UtilsService extends BasicService {

    @Autowired
    protected ECService ecService;

    @Transactional(readOnly = true)
    public Organisations getOrganisations(Locale locale) {
        Organisations result = new Organisations();

        String[] dgs = ecService.getDepartments(true, false);
        String[] aex = ecService.getDepartments(false, true);

        for (String code : dgs) {
            if (!code.contains(".")) {
                result.getDgs().put(code, resources.getMessage("label.dgnew." + code, null, code, locale));
            }
        }
        result.setDgs(Tools.sortByComparator(result.getDgs()));

        for (String code : aex) {
            if (!code.contains(".")) {
                result.getExecutiveAgencies().put(code, resources.getMessage("label.dgnew." + code, null, code, locale));
            }
        }
        result.setExecutiveAgencies(Tools.sortByComparator(result.getExecutiveAgencies()));

        result.getOtherEUIs().put("ACER",resources.getMessage("domain.eu.europa.acer", null, locale));
        result.getOtherEUIs().put("AMLA",resources.getMessage("domain.eu.europa.amla", null, locale));
        result.getOtherEUIs().put("ARTEMIS",resources.getMessage("domain.eu.europa.artemis", null, locale));
        result.getOtherEUIs().put("CLEANSKY",resources.getMessage("domain.eu.europa.cleansky", null, locale));
        result.getOtherEUIs().put("CDR",resources.getMessage("domain.eu.europa.cor", null, locale));
        result.getOtherEUIs().put("CPVO",resources.getMessage("domain.eu.europa.cpvo", null, locale));
        result.getOtherEUIs().put("CONSILIUM",resources.getMessage("domain.eu.europa.consilium", null, locale));
        result.getOtherEUIs().put("CS",resources.getMessage("domain.eu.europa.cs", null, locale));
        result.getOtherEUIs().put("CURIA",resources.getMessage("domain.eu.europa.curia", null, locale));
        result.getOtherEUIs().put("ECSEL",resources.getMessage("domain.eu.europa.ecsel", null, locale));
        result.getOtherEUIs().put("ENIAC",resources.getMessage("domain.eu.europa.eniac", null, locale));
        result.getOtherEUIs().put("EULISA",resources.getMessage("domain.eu.europa.eulisa", null, locale));
        result.getOtherEUIs().put("EUROJUST",resources.getMessage("domain.eu.europa.eurojust", null, locale));
        result.getOtherEUIs().put("OSHA",resources.getMessage("domain.eu.europa.osha", null, locale));
        result.getOtherEUIs().put("EASO",resources.getMessage("domain.eu.europa.easo", null, locale));
        result.getOtherEUIs().put("EASA",resources.getMessage("domain.eu.europa.easa", null, locale));
        result.getOtherEUIs().put("EBA",resources.getMessage("domain.eu.europa.eba", null, locale));
        result.getOtherEUIs().put("ECB",resources.getMessage("domain.eu.europa.ecb", null, locale));
        result.getOtherEUIs().put("ECCC",resources.getMessage("domain.eu.europa.eccc", null, locale));
        result.getOtherEUIs().put("ECDC",resources.getMessage("domain.eu.europa.ecdc", null, locale));
        result.getOtherEUIs().put("CEDEFOP",resources.getMessage("domain.eu.europa.cedefop", null, locale));
        result.getOtherEUIs().put("ECHA",resources.getMessage("domain.eu.europa.echa", null, locale));
        //list.put("EC",resources.getMessage("domain.eu.europa.ec", null, locale));
        result.getOtherEUIs().put("EUROPEANCOUNCIL",resources.getMessage("domain.eu.europa.european-council", null, locale)); // this might be wrong
        result.getOtherEUIs().put("ECA",resources.getMessage("domain.eu.europa.eca", null, locale));
        result.getOtherEUIs().put("EDPS",resources.getMessage("domain.eu.europa.edps", null, locale));
        result.getOtherEUIs().put("EDA",resources.getMessage("domain.eu.europa.eda", null, locale));
        result.getOtherEUIs().put("EESC",resources.getMessage("domain.eu.europa.eesc", null, locale));
        result.getOtherEUIs().put("EEA",resources.getMessage("domain.eu.europa.eea", null, locale));
        result.getOtherEUIs().put("EEAS",resources.getMessage("domain.eu.europa.eeas", null, locale));
        result.getOtherEUIs().put("EFCA",resources.getMessage("domain.eu.europa.efca", null, locale));
        result.getOtherEUIs().put("EFSA",resources.getMessage("domain.eu.europa.efsa", null, locale));
        result.getOtherEUIs().put("EUROFOUND",resources.getMessage("domain.eu.europa.eurofound", null, locale));
        result.getOtherEUIs().put("GSA",resources.getMessage("domain.eu.europa.gsa", null, locale));
        result.getOtherEUIs().put("EIGE",resources.getMessage("domain.eu.europa.eige", null, locale));
        result.getOtherEUIs().put("EIT",resources.getMessage("domain.eu.europa.eit", null, locale));
        result.getOtherEUIs().put("EIOPA",resources.getMessage("domain.eu.europa.eiopa", null, locale));
        result.getOtherEUIs().put("F4E",resources.getMessage("domain.eu.europa.f4e", null, locale));
        result.getOtherEUIs().put("EMSA",resources.getMessage("domain.eu.europa.emsa", null, locale));
        result.getOtherEUIs().put("EMA",resources.getMessage("domain.eu.europa.ema", null, locale));
        result.getOtherEUIs().put("EUDA",resources.getMessage("domain.eu.europa.euda", null, locale));
        result.getOtherEUIs().put("OMBUDSMAN",resources.getMessage("domain.eu.europa.ombudsman", null, locale));
        result.getOtherEUIs().put("EUROPARL",resources.getMessage("domain.eu.europa.europarl", null, locale));
        result.getOtherEUIs().put("CEPOL",resources.getMessage("domain.eu.europa.cepol", null, locale));
        result.getOtherEUIs().put("EUROPOL",resources.getMessage("domain.eu.europa.europol", null, locale));
        result.getOtherEUIs().put("ERA",resources.getMessage("domain.eu.europa.era", null, locale));
        result.getOtherEUIs().put("ESMA",resources.getMessage("domain.eu.europa.esma", null, locale));
        result.getOtherEUIs().put("ETF",resources.getMessage("domain.eu.europa.etf", null, locale));
        result.getOtherEUIs().put("FRA",resources.getMessage("domain.eu.europa.fra", null, locale));
        result.getOtherEUIs().put("EUI",resources.getMessage("domain.eu.europa.eui", null, locale));
        // external
        result.getOtherEUIs().put("FRONTEX",resources.getMessage("domain.eu.europa.frontex", null, locale));
        result.getOtherEUIs().put("IMI",resources.getMessage("domain.eu.europa.imi", null, locale));
        result.getOtherEUIs().put("FCH",resources.getMessage("domain.eu.europa.fch", null, locale));
        result.getOtherEUIs().put("OHIM",resources.getMessage("domain.eu.europa.ohim", null, locale));
        result.getOtherEUIs().put("SESAR",resources.getMessage("domain.eu.europa.sesar", null, locale));
        result.getOtherEUIs().put("BEREC",resources.getMessage("domain.eu.europa.berec", null, locale));
        result.getOtherEUIs().put("CDT",resources.getMessage("domain.eu.europa.cdt", null, locale));

        result.setOtherEUIs(Tools.sortByComparator(result.getOtherEUIs()));

        result.getNonEUIs().put("PUBLICADMINISTRATION", resources.getMessage("label.PublicAdministration", null, "Public Administration", locale));
        result.getNonEUIs().put("PRIVATEORGANISATION", resources.getMessage("label.PrivateOrganisation", null, "Private Organisation", locale));
        result.getNonEUIs().put("CITIZEN", resources.getMessage("label.CitizenOfTheEU", null, "Citizen of the EU", locale));
        result.getNonEUIs().put("OTHER", resources.getMessage("label.Other", null, "Other", locale));

        result.setNonEUIs(Tools.sortByComparator(result.getNonEUIs()));

        return result;
    }

    public List<String> getAllOrganisationCodes(Locale locale) {
        Organisations organisations = getOrganisations(locale);
        List<String> org = new ArrayList(organisations.getDgs().keySet());
        org.addAll(organisations.getExecutiveAgencies().keySet());
        org.addAll(organisations.getOtherEUIs().keySet());
        org.addAll(organisations.getNonEUIs().keySet());
        return org;
    }

    public Map<String, String> getAllOrganisations(Locale locale) {
        Organisations organisations = getOrganisations(locale);
        Map<String, String> org = organisations.getDgs();
        org.putAll(organisations.getExecutiveAgencies());
        org.putAll(organisations.getOtherEUIs());
        org.putAll(organisations.getNonEUIs());
        return org;
    }
}
