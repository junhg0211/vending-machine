package org.shtelo.sch.vending_project.util;

/**
 * 한국어 조사 관련 유틸리티입니다.
 */
public class Josa {
    private static final int GA = 0xAC00;

    /**
     * 글자에 받침이 있는지 확인합니다.
     * @param letter 받침이 있는지 확인할 한글 글자
     * @return 받침 존재에 대한 여부
     */
    private static boolean hasBatchim(char letter) {
        int value = letter - GA;
        return value % 28 != 0;
    }

    /**
     * 단어에 대한 목적격 조사 을/를을 반환합니다.
     * @param string 목적격 조사가 붙을 단어
     * @return 목적격 조사
     */
    public static char eulReul(String string) {
        if (hasBatchim(string.charAt(string.length() - 1))) {
            return '을';
        } else {
            return '를';
        }
    }
}
