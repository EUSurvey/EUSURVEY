package com.ec.survey.tools;

public class MathUtils {
	public static Integer[] computeUpperAndLowerMedian(Integer[] values) {
		
		int medianIndexLower;
		int medianIndexUpper;
		int length = values.length;
		
		if (length == 1) {
			medianIndexLower = values[0];
			medianIndexUpper = medianIndexLower;
		} else {
			int median_index = length / 2;
			
			if (length % 2 == 0) {
				medianIndexLower = values[median_index-1];
				medianIndexUpper = values[median_index];
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
}
