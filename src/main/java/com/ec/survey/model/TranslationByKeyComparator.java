package com.ec.survey.model;

import java.util.Comparator;

public class TranslationByKeyComparator implements Comparator<Translation> {
	public int compare (Translation a, Translation b)
	{
		return a.getKey().compareTo(b.getKey());
	}
}
