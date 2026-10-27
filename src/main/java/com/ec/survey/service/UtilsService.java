package com.ec.survey.service;

import com.ec.survey.model.KeyValue;
import com.ec.survey.model.Organisations;
import com.ec.survey.tools.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("utilsService")
public class UtilsService extends BasicService {

    @Autowired
    protected ECService ecService;

    @Resource(name = "ldapDBService")
    protected LdapDBService ldapDBService;

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

        List<KeyValue> domains = ldapDBService.getDomains(false, false, resources, locale);
        for  (KeyValue keyValue : domains) {
            if (!keyValue.getKey().equals("eu.europa.ec") && !keyValue.getKey().equalsIgnoreCase("external")) {
                result.getOtherEUIs().put(getCodeForDomain(keyValue.getKey()), resources.getMessage("domain." + keyValue.getKey(), null, keyValue.getValue(), locale));
            }
        }

        result.setOtherEUIs(Tools.sortByComparator(result.getOtherEUIs()));

        result.getNonEUIs().put("PUBLICADMINISTRATION", resources.getMessage("label.PublicAdministration", null, "Public Administration", locale));
        result.getNonEUIs().put("PRIVATEORGANISATION", resources.getMessage("label.PrivateOrganisation", null, "Private Organisation", locale));
        result.getNonEUIs().put("CITIZEN", resources.getMessage("label.CitizenOfTheEU", null, "Citizen of the EU", locale));
        result.getNonEUIs().put("OTHER", resources.getMessage("label.Other", null, "Other", locale));

        result.setNonEUIs(Tools.sortByComparator(result.getNonEUIs()));

        return result;
    }

    private String getCodeForDomain(String domain) {
        if (!domain.contains(".")) return domain.toUpperCase();
        return domain.substring(domain.lastIndexOf(".") + 1).toUpperCase();
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
