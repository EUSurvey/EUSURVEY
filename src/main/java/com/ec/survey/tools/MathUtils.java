package com.ec.survey.tools;

import java.util.Arrays;

public class MathUtils {
	public static Integer[] computeMedianIndices(Integer[] values) {
		
		int medianIndexLower;
		int medianIndexUpper;
		int length = values.length;
		
		if (length == 1) {
			medianIndexLower = values[0];
			medianIndexUpper = medianIndexLower;
		} else {
			int median_index = length / 2;
			
			if (length % 2 == 0) {
				double lowermedian = values[median_index-1];
				double uppermedian = values[median_index];
				double median = (lowermedian + uppermedian)/2.0;
				medianIndexLower = (int)(Math.floor(median));
				medianIndexUpper = (int)(Math.ceil(median));
			} else {
				medianIndexLower = values[median_index];
				medianIndexUpper = medianIndexLower;
			}
		}
		
		Integer[] result = new Integer[2];
		result[0] = medianIndexLower;
		result[1] = medianIndexUpper;
				
		return result;
	}

	public static Double computeMedian(Double[] values) {
		Arrays.sort(values);

		int length = values.length;
		
		if (length == 1) {
			return values[0];
		}
		
		int medianIndex = length / 2;
		
		if (length % 2 != 0) {
			return values[medianIndex];
		}
		
		double lowermedian = values[medianIndex-1];
		double uppermedian = values[medianIndex];
		return (lowermedian + uppermedian) / 2.0;	
	}
}
