package com.ose.p.util;

public class KoEnUtil {
	private char[] enKoKeyMap;
	private String[] koEnKeyMap;

	private final ConsonantVowelUtil util;

	public KoEnUtil () {
		util = new ConsonantVowelUtil();
		initialize();
	}


	private void initializeEnKoKeyMap() {
		enKoKeyMap = new char['z' + 1];
		enKoKeyMap['a'] = 'ㅁ';
        enKoKeyMap['A'] = 'ㅁ';
        enKoKeyMap['b'] = 'ㅠ';
        enKoKeyMap['B'] = 'ㅠ';
        enKoKeyMap['c'] = 'ㅊ';
        enKoKeyMap['C'] = 'ㅊ';
        enKoKeyMap['d'] = 'ㅇ';
        enKoKeyMap['D'] = 'ㅇ';
        enKoKeyMap['e'] = 'ㄷ';
        enKoKeyMap['E'] = 'ㄸ';
        enKoKeyMap['f'] = 'ㄹ';
        enKoKeyMap['F'] = 'ㄹ';
        enKoKeyMap['g'] = 'ㅎ';
        enKoKeyMap['G'] = 'ㅎ';
        enKoKeyMap['h'] = 'ㅗ';
        enKoKeyMap['H'] = 'ㅗ';
        enKoKeyMap['i'] = 'ㅑ';
        enKoKeyMap['I'] = 'ㅑ';
        enKoKeyMap['j'] = 'ㅓ';
        enKoKeyMap['J'] = 'ㅓ';
        enKoKeyMap['k'] = 'ㅏ';
        enKoKeyMap['K'] = 'ㅏ';
        enKoKeyMap['l'] = 'ㅣ';
        enKoKeyMap['L'] = 'ㅣ';
        enKoKeyMap['m'] = 'ㅡ';
        enKoKeyMap['M'] = 'ㅡ';
        enKoKeyMap['n'] = 'ㅜ';
        enKoKeyMap['N'] = 'ㅜ';
        enKoKeyMap['o'] = 'ㅐ';
        enKoKeyMap['O'] = 'ㅒ';
        enKoKeyMap['p'] = 'ㅔ';
        enKoKeyMap['P'] = 'ㅖ';
        enKoKeyMap['q'] = 'ㅂ';
        enKoKeyMap['Q'] = 'ㅃ';
        enKoKeyMap['r'] = 'ㄱ';
        enKoKeyMap['R'] = 'ㄲ';
        enKoKeyMap['s'] = 'ㄴ';
        enKoKeyMap['S'] = 'ㄴ';
        enKoKeyMap['t'] = 'ㅅ';
        enKoKeyMap['T'] = 'ㅆ';
        enKoKeyMap['u'] = 'ㅕ';
        enKoKeyMap['U'] = 'ㅕ';
        enKoKeyMap['v'] = 'ㅍ';
        enKoKeyMap['V'] = 'ㅍ';
        enKoKeyMap['w'] = 'ㅈ';
        enKoKeyMap['W'] = 'ㅉ';
        enKoKeyMap['x'] = 'ㅌ';
        enKoKeyMap['X'] = 'ㅌ';
        enKoKeyMap['y'] = 'ㅛ';
        enKoKeyMap['Y'] = 'ㅛ';
        enKoKeyMap['z'] = 'ㅋ';
        enKoKeyMap['Z'] = 'ㅋ';
	}

	private void initializeKoEnKeyMap() {
		koEnKeyMap = new String['ㅣ' + 1];

		for(int i = 0; i < koEnKeyMap.length; i++) {
			koEnKeyMap[i] = String.valueOf((char) i);
		}

		koEnKeyMap['ㄱ'] = "r";
		koEnKeyMap['ㄲ'] = "R";
		koEnKeyMap['ㄴ'] = "s";
		koEnKeyMap['ㄷ'] = "e";
		koEnKeyMap['ㄸ'] = "E";
		koEnKeyMap['ㄹ'] = "f";
		koEnKeyMap['ㅁ'] = "a";
		koEnKeyMap['ㅂ'] = "q";
		koEnKeyMap['ㅃ'] = "Q";
		koEnKeyMap['ㅅ'] = "t";
		koEnKeyMap['ㅆ'] = "T";
		koEnKeyMap['ㅇ'] = "d";
		koEnKeyMap['ㅈ'] = "w";
		koEnKeyMap['ㅉ'] = "W";
		koEnKeyMap['ㅊ'] = "c";
		koEnKeyMap['ㅋ'] = "z";
		koEnKeyMap['ㅌ'] = "x";
		koEnKeyMap['ㅍ'] = "v";
		koEnKeyMap['ㅎ'] = "g";
		koEnKeyMap['ㅏ'] = "k";
		koEnKeyMap['ㅐ'] = "o";
		koEnKeyMap['ㅑ'] = "i";
		koEnKeyMap['ㅒ'] = "O";
		koEnKeyMap['ㅓ'] = "j";
		koEnKeyMap['ㅔ'] = "p";
		koEnKeyMap['ㅕ'] = "u";
		koEnKeyMap['ㅖ'] = "P";
		koEnKeyMap['ㅗ'] = "h";
		koEnKeyMap['ㅛ'] = "y";
		koEnKeyMap['ㅜ'] = "n";
		koEnKeyMap['ㅠ'] = "b";
		koEnKeyMap['ㅡ'] = "m";
		koEnKeyMap['ㅣ'] = "l";
	}

	private void initialize() {
		initializeEnKoKeyMap();
		initializeKoEnKeyMap();
	}

	public String convertEnToKo(String en) {
		StringBuilder builder = new StringBuilder();

		for(char c : en.toCharArray()) {
			if(isEnChar(c)) {
				builder.append(enKoKeyMap[c]);
			} else {
				builder.append(c);
			}
		}
		return util.compose(builder.toString());
	}

	public String convertKoToEn(String ko) {
		StringBuilder builder = new StringBuilder();

		for(char c : ko.toCharArray()) {
			if(isKoChar(c)) {
				builder.append(koEnKeyMap[c]);
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	private boolean isEnChar(char c) {
		return (c >= 'A' && c <= 'Z') || (c >='a' && c <='z');
	}

	private boolean isKoChar(char c) {
		return c >= 'ㄱ' && c <= 'ㅣ';
	}
}
