package com.ec.survey.tools;

public class Numbering {
	
	private int n1 = 0;
	private int n2 = 0;
	private int n3 = 0;
	private int n4 = 0;
	private int n5 = 0;
	
	public int getN1() {
		return n1;
	}
	public void setN1(int n1) {
		this.n1 = n1;
		n3 = 0;
		n2 = 0;
		n4 = 0;
		n5 = 0;
	}
	
	public int getN2() {
		return n2;
	}
	public void setN2(int n2) {
		this.n2 = n2;
		n3 = 0;
		n4 = 0;
		n5 = 0;
	}
	
	public int getN3() {
		return n3;
	}
	public void setN3(int n3) {
		this.n3 = n3;
		n4 = 0;
		n5 = 0;
	}
	
	public int getN4() {
		return n4;
	}
	public void setN4(int n4) {
		this.n4 = n4;
		n5 = 0;
	}
	
	public int getN5() {
		return n5;
	}
	public void setN5(int n5) {
		this.n5 = n5;
	}
	
	public String getCounter(int type, int level)
	{
		String result = "";
		
		if (type == 1)
		{
			result += n1;
			
			if (level > 1)
			{
				if (n2 == 0) n2 = 1;
				result += "." + n2;
			}
			
			if (level > 2)
			{
				if (n3 == 0) n3 = 1;
				result += "." + n3;
			}
			
			if (level > 3)
			{
				if (n4 == 0) n4 = 1;
				result += "." + n4;
			}
			
			if (level > 4)
			{
				if (n5 == 0) n5 = 1;
				result += "." + n5;
			}
		}
		
		if (type == 2)
		{
			result += getSmallLetter(n1);
			
			if (level > 1)
			{
				result += "." + getSmallLetter(n2);
			}
			
			if (level > 2)
			{
				result += "." + getSmallLetter(n3);
			}
			
			if (level > 3)
			{
				result += "." + getSmallLetter(n4);
			}
			
			if (level > 4)
			{
				result += "." + getSmallLetter(n5);
			}		
			
		}
		
		if (type == 3)
		{
			result += getBigLetter(n1);
			
			if (level > 1)
			{
				result += "." + getBigLetter(n2);
			}
			
			if (level > 2)
			{
				result += "." + getBigLetter(n3);
			}
			
			if (level > 3)
			{
				result += "." + getBigLetter(n4);
			}
			
			if (level > 4)
			{
				result += "." + getBigLetter(n5);
			}		
		}
		
		
		return result;
	}
	
	public static String getSmallLetter(int pcounter)
	{
		//1 -> a
		int counter = pcounter -1; //we start with 0 and not 1
		int prefix = counter / 26;
		counter = counter % 26;
		String sprefix = "";
		if (prefix > 0) sprefix = "abcdefghijklmnopqrstuvwxyz".substring(prefix-1,prefix);
		if (counter == 25)
		{
			return sprefix + "z";
		}
		return sprefix + "abcdefghijklmnopqrstuvwxyz".substring(counter,counter+1);	
	}
	
	public static String getBigLetter(int pcounter)
	{
		int counter = pcounter -1; //we start with 0 and not 1
		int prefix = counter / 26;
		counter = counter % 26;
		String sprefix = "";
		if (prefix > 0) sprefix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(prefix-1,prefix);
		if (counter == 25)
		{
			return sprefix + "Z";
		}
		return sprefix + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(counter,counter+1);	
	}
	

}
