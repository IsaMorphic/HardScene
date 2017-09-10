package me.skorrloregaming.hardscene;

import java.util.ArrayList;

public class SwearData {

	private ArrayList<Character> swearWordsChar0 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar1 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar2 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar3 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar4 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar5 = new ArrayList<Character>();
	private ArrayList<Character> swearWordsChar6 = new ArrayList<Character>();

	public ArrayList<Character> get(int length) {
		if (length == 0)
			return swearWordsChar0;
		if (length == 1)
			return swearWordsChar1;
		if (length == 2)
			return swearWordsChar2;
		if (length == 3)
			return swearWordsChar3;
		if (length == 4)
			return swearWordsChar4;
		if (length == 5)
			return swearWordsChar5;
		if (length == 6)
			return swearWordsChar6;
		return new ArrayList<Character>();
	}

	public void fill(String swearWord) {
		char[] chars = swearWord.toCharArray();
		for (int i = 0; i < chars.length; i++)
			get(i).add(chars[i]);
	}

}
