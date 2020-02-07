package com.ec.survey.tools;

import com.ec.survey.model.Skin;
import com.ec.survey.model.survey.base.File;
import edu.vt.middleware.password.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

public class Tools {
	
	private static final String CURRENT_TOS_VERSION = "1";
	private static final String CURRENT_PS_VERSION = "1";
		
	public static boolean isFileEqual(File o1, File o2)
	{
		if (o1 == null && o2 == null) return true;
		if (o1 == null && o2 != null) return false;
		if (o2 == null && o1 != null) return false;
		
		return o1.getUid().equals(o2.getUid());
	}
	
	public static boolean isDoubleEqual(double x, double y)
	{
		final double EPSILON = 0.00001;
		return ((x >= y - EPSILON) && (x <= y + EPSILON));
	}
	
	public static boolean isEqualIgnoreEmptyString(Object o1, Object o2)
	{
		if (o1 == null && o2 != null && o2 instanceof String)
		{
			return ((String)o2).trim().length() == 0;
		}
		
		if (o2 == null && o1 != null && o1 instanceof String)
		{
			return ((String)o1).trim().length() == 0;
		}		
		
		return isEqual(o1, o2);
	}
	
	public static boolean isEqual(Object o1, Object o2)
	{
		if (o1 == null && o2 == null) return true;
		
		if (o1 == null && o2 != null && o2 instanceof Skin)
		{
			return ((Skin)o2).getId() == 1;
		}
		
		if (o2 == null && o1 != null && o1 instanceof Skin)
		{
			return ((Skin)o1).getId() == 1;
		}
		
		if (o1 == null && o2 != null) return false;
		if (o2 == null && o1 != null) return false;
		
		return o1.equals(o2);
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static boolean isUUID(String s){
	    return s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
	}
	
	public static Date getFollowingDay(Date d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	public static Date getPreviousDay(Date d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	
	public static Date addOneSecond(Date d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.SECOND, 1);
		return cal.getTime();
	}
	
	public static boolean isToday(Date date)
	{
		Calendar c = Calendar.getInstance();

	    // set the calendar to start of today
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);

	    Date today = c.getTime();

	    c.setTime(date);

	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);

	    Date dateSpecified = c.getTime();

	    if (dateSpecified.before(today)) {

	       return false;
	    } else if (dateSpecified.equals(today)) {

	       return true;
	    } else if (dateSpecified.after(today)) {

	        return false;
	    } 
	    
	    return false;
	}
	
	public static String hash(String input)
	{
		String result = DigestUtils.sha1Hex(input);
		
		for (int i = 0; i < 64000; i++)
		{
			result = DigestUtils.sha1Hex(result);
		}
		
		return result;
	}

	public static boolean isPasswordValid(String hashedPassword, String rawPassword) {
		return hash(rawPassword).equals(hashedPassword);
	}

	public static String newSalt() {
		final Random r = new SecureRandom();
		byte[] salt = new byte[32];
		r.nextBytes(salt);
		return Base64.encodeBase64String(salt); 		
	}

	public static String md5hash(String input) {
		return DigestUtils.md5Hex(input);
	}
	
	public static boolean isPasswordWeak(String password)
	{
		// password must be between 8 and 16 chars long
		LengthRule lengthRule = new LengthRule(8, 16);

		// control allowed characters
		CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
		// require at least 1 digit in passwords
		charRule.getRules().add(new DigitCharacterRule(1));
		// require at least 1 non-alphanumeric char
		charRule.getRules().add(new NonAlphanumericCharacterRule(1));
		charRule.setNumberOfCharacteristics(2);
	
		// group all rules together in a List
		List<Rule> ruleList = new ArrayList<>();
		ruleList.add(lengthRule);
		ruleList.add(charRule);
		
		PasswordValidator validator = new PasswordValidator(ruleList);
		PasswordData passwordData = new PasswordData(new Password(password));

		RuleResult result = validator.validate(passwordData);
		if (result.isValid()) {
		  return false;
		} else {		 
		  return true;
		}
	}

	public static boolean validUniqueCode(String uniqueCode) {
		try {
	        UUID.fromString(uniqueCode);
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}

	public static String encodeForLDAP(String input) {
		return ESAPI.encoder().encodeForLDAP(input);
	}
	
	
	public static String encodeForJavaScript(String input) {
		return ESAPI.encoder().encodeForJavaScript(input);
	}
	public static String escapeHTML(String input) {
		return ESAPI.encoder().decodeForHTML(input);
	}
	
	public static String filterHTML(String input) throws IntrusionException {
		//this is postponed to ticket ESURVEY-1626, please do not remove this line
		//return ESAPI.validator().getValidSafeHTML("input", input, 10000, true);
		return input;
	}
	public static String toUTF83Bytes(String input) {
		return input.replaceAll("[^\\u0019-\\uFFFF]", "\uFFFD");
	}
	
	public static Boolean containsNonUTF83Bytes(String input) {
		return input.matches(".*[^\\u0019-\\uFFFF].*");
	}

	public static Map<String, String> sortByComparator(Map<String, String> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, String>> list =
				new LinkedList<>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		list.sort(Comparator.comparing(o -> (o.getValue())));

		// Convert sorted map back to a Map
		Map<String, String> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static String repairXML(String input) {
		if (input == null || input.length() == 0) return input;
		return input.replace("–", "-");
	}

	public static Date parseDateString(String dateString, String pattern) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(pattern)
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
	            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
	            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
	            .toFormatter(); 
				
	    LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
	    Date date = Date.from(dateTime.atZone(ZoneId.of("CET")).toInstant());
		return date;
	}

	public static String formatDate(Date date, String pattern) {
		
		if (date == null) return null;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		
		Instant instant;
		if (date instanceof java.sql.Date)
		{
			instant = Instant.ofEpochMilli(date.getTime());
		} else {
			instant = date.toInstant();
		}
		
		LocalDateTime localDateTime = instant.atZone(ZoneId.of("CET")).toLocalDateTime();
		String formatDateTime = localDateTime.format(formatter);
		return formatDateTime;
	}
	
	public static String getCurrentToSVersion()
	{
		return CURRENT_TOS_VERSION;
	}
	
	public static String getCurrentPSVersion()
	{
		return CURRENT_PS_VERSION;
	}
}
