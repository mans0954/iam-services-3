/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.pswd.service;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.springframework.stereotype.Component;

/**
 * Generates a random string that is used to create a password.
 *
 * @author suneet
 */
@Component
public class PasswordGenerator {

    private static final Log log = LogFactory.getLog(PasswordGenerator.class);
    private static final char[] lowerChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] upperChars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] numericChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] specialChars = {'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
                                                ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};

    private static final String charset = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String specialCharset = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    private static final String numericCharset = "0123456789";
    private static final String upperCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerCharset = "abcdefghijklmnopqrstuvwxyz";
    private String[] args;
    private static final String ANY = "ANY";
    private static final String PWD_LEN = "PWD_LEN";
    private static final String NON_ALPHA_CHARS = "NON_ALPHA_CHARS";
    private static final String LOWERCASE_CHARS = "LOWERCASE_CHARS";
    private static final String UPPERCASE_CHARS = "UPPERCASE_CHARS";
    private static final String NUMERIC_CHARS = "NUMERIC_CHARS";
    private static final String LIMIT_NUM_REPEAT_CHAR = "LIMIT_NUM_REPEAT_CHAR";
    private static final String REJECT_CHARS_IN_PSWD = "REJECT_CHARS_IN_PSWD";
    private static final String ALPHA_CHARS = "ALPHA_CHARS";

    private static final int DEFAULT_PASSWORD_LENGTH = 12;

    public static String generatePassword(int length) {

        boolean foundRequiredChar = false;

        boolean lcase = false, ucase = false, numchar = false, specialchar = false;

        Random rand = new Random(UUID.randomUUID().getLeastSignificantBits());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {

            if (sb.length() >= (length - 4) && !foundRequiredChar) {
                // we have 4 chars to fill and need to make sure that we meet the password policy

                if (!specialchar) {
                    getMissingChar(rand, specialCharset, sb);
                    specialchar = true;
                    continue;
                }
                if (!numchar) {
                    getMissingChar(rand, numericCharset, sb);
                    numchar = true;
                    continue;
                }
                if (!ucase) {
                    getMissingChar(rand, upperCharset, sb);
                    ucase = true;
                    continue;
                }
                if (!lcase) {
                    getMissingChar(rand, lowerCharset, sb);
                    lcase = true;
                    continue;
                }

            }


            int pos = rand.nextInt(charset.length());

            char c = charset.charAt(pos);

            if (contain(lowerChars, c)) {
                lcase = true;
            }
            if (contain(upperChars, c)) {
                ucase = true;
            }
            if (contain(numericChars, c)) {
                numchar = true;
            }
            if (contain(specialChars, c)) {
                specialchar = true;
            }
            sb.append(c);

            if (lcase && ucase && numchar && specialchar) {
                foundRequiredChar = true;

            }
        }

        return sb.toString();
    }

    public static String generatePassword(Policy policy) {
        log.debug("Start password generation!");
        Random rand = new Random(UUID.randomUUID().getLeastSignificantBits());
        PolicyAttribute paLength = policy.getAttribute(PWD_LEN);
        PolicyAttribute paSpecialChars = policy.getAttribute(NON_ALPHA_CHARS);
        PolicyAttribute paLowerCase = policy.getAttribute(LOWERCASE_CHARS);
        PolicyAttribute paUpCase = policy.getAttribute(UPPERCASE_CHARS);
        PolicyAttribute paAlphaChars = policy.getAttribute(ALPHA_CHARS);
        PolicyAttribute paNumbers = policy.getAttribute(NUMERIC_CHARS);
        PolicyAttribute repeatLimit = policy.getAttribute(LIMIT_NUM_REPEAT_CHAR);
        PolicyAttribute notAllowedChars = policy.getAttribute(REJECT_CHARS_IN_PSWD);

        String notAllowedChar = notAllowedChars.getValue1() != null ? notAllowedChars.getValue1() : "";

        List<String> listLowerChars = getClearedGroupValues(lowerChars, notAllowedChar);
        List<String> listUpperChars = getClearedGroupValues(upperChars, notAllowedChar);
        List<String> listNumericChars = getClearedGroupValues(numericChars, notAllowedChar);
        List<String> listSpecialChars = getClearedGroupValues(specialChars, notAllowedChar);

        int intLengthMin = toInt(paLength.getValue1());
        Integer intLengthMax = toInteger(paLength.getValue2());

        int intSpecialMin = toInt(paSpecialChars.getValue1());
        Integer intSpecialMax = toInteger(paSpecialChars.getValue2());
        if (intSpecialMin>0 && toInt(intSpecialMax) == 0) {
            intLengthMax = null;
        }
        int intLowerMin = toInt(paLowerCase.getValue1());
        Integer intLowerMax = toInteger(paLowerCase.getValue2());
        if (intLowerMin>0 && toInt(intLowerMax) == 0) {
            intLowerMax = null;
        }
        int intUpperMin = toInt(paUpCase.getValue1());
        Integer intUpperMax = toInteger(paUpCase.getValue2());
        if (intUpperMin>0 && toInt(intUpperMax) == 0) {
            intUpperMax = null;
        }
        int intNumberMin = toInt(paNumbers.getValue1());
        Integer intNumberMax = toInteger(paNumbers.getValue2());
        if (intNumberMin>0 && toInt(intNumberMax) == 0) {
            intNumberMax = null;
        }
        int intAlphaMin = toInt(paAlphaChars.getValue1());
        Integer intAlphaMax = toInteger(paAlphaChars.getValue2());
        if (intAlphaMin>0 && toInt(intAlphaMax) == 0) {
            intAlphaMax = null;
        }

        if (intAlphaMin > intUpperMin + intLowerMin) {
            intUpperMin += rand.nextInt(1 + intAlphaMin - intUpperMin - intLowerMin);
            intLowerMin = intAlphaMin - intUpperMin;
        }

        if (intAlphaMax != null && (intLowerMax == null || intUpperMax == null
                || intAlphaMax < toInt(intLowerMax)+toInt(intUpperMax))) {
            boolean isNormalization = intLowerMax != null && intUpperMax != null;
            if (intUpperMax == null || isNormalization) {
                if (isNormalization) {
                    intUpperMax = intUpperMax - rand.nextInt(1 + intUpperMax + intLowerMax - intAlphaMax);
                    if (intUpperMax < intUpperMin) {
                        intUpperMax = intUpperMin;
                    }
                } else if (intLowerMax == null) {
                    intUpperMax = intUpperMin + rand.nextInt(1 + intAlphaMax - intUpperMin - intLowerMin);
                } else {
                    intUpperMax = intAlphaMax - intLowerMax;
                }
            }
            if (intLowerMax == null || isNormalization) {
                intLowerMax = intAlphaMax - intUpperMax;
                if (intLowerMax < intLowerMin) {
                    intLowerMax = intLowerMin;
                }
            }
        }

        intLengthMin = Math.max(intLengthMin, intSpecialMin+intLowerMin+intUpperMin+intNumberMin);
        if (intLengthMax == null) {
            if (intSpecialMax != null && intNumberMax != null && intLowerMax != null && intUpperMax != null) {
                intLengthMax = intSpecialMax + intNumberMax + intLowerMax + intUpperMax;
            } else {
                intLengthMax = Math.max(intLengthMin, DEFAULT_PASSWORD_LENGTH);
            }
        }

        if (intSpecialMax == null) { intSpecialMax = intLengthMax; }
        if (intNumberMax == null) { intNumberMax = intLengthMax; }
        if (intLowerMax == null) { intLowerMax = intLengthMax; }
        if (intUpperMax == null) { intUpperMax = intLengthMax; }

        Integer repeats = toInteger(repeatLimit.getValue1());
        log.debug("Fill initially!");
        List<String> passwordAsList = new ArrayList<>();
        List<String> specialAsList = getArrayOfGroup(intSpecialMin, intSpecialMax, notAllowedChar, repeats, listSpecialChars, rand);
        List<String> numericAsList = getArrayOfGroup(intNumberMin, intNumberMax, notAllowedChar, repeats, listNumericChars, rand);
        List<String> upperAsList = getArrayOfGroup(intUpperMin, intUpperMax, notAllowedChar, repeats, listUpperChars, rand);
        List<String> lowerAsList = getArrayOfGroup(intLowerMin, intLowerMax, notAllowedChar, repeats, listLowerChars, rand);
        log.debug("End Fill initially!");
        int used = specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size();
        log.debug("Calculate password length!");
        if (!(used >= intLengthMin && used <= toInt(intLengthMax))) {
            int passwordLength = 0;
            if (intLengthMin == 0 && (intLengthMax == null || intLengthMax == 0)) {
                passwordLength = DEFAULT_PASSWORD_LENGTH;
            } else if (intLengthMin != 0 && (intLengthMax == null || intLengthMax == 0 || intLengthMin == intLengthMax)) {
                // will be the smallest length
                passwordLength = intLengthMin;
            } else {
                do {
                    passwordLength = rand.nextInt(1 + intLengthMax - intLengthMin) + intLengthMin;
                } while (passwordLength == 0);
            }
            if (used < intLengthMin) {
                while (used != passwordLength) {
                    log.debug("Adding more chars!");
                    if (!addAtGroup(upperAsList, intUpperMax, notAllowedChar, repeats, listUpperChars, rand)) {
                        if (!addAtGroup(lowerAsList, intLowerMax, notAllowedChar, repeats, listLowerChars, rand))
                            if (!addAtGroup(numericAsList, intNumberMax, notAllowedChar, repeats, listNumericChars, rand)) {
                                addAtGroup(specialAsList, intSpecialMax, notAllowedChar, repeats, listSpecialChars, rand);
                            }
                    }
                    used = specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size();
                }
            } else if (intLengthMax != null && used > intLengthMax) {
                while (used != passwordLength) {
                    log.debug("Deleting of chars!");
                    int random = rand.nextInt(4);
                    boolean flag1 = true;
                    boolean flag2 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    switch (random) {
                        case 0:
                            if (deleteFromGroup(specialAsList, intSpecialMin, rand)) {
                                break;
                            }
                            flag1 = false;
                        case 1:
                            if (deleteFromGroup(upperAsList, intUpperMin, rand)) {
                                break;
                            }
                            flag2 = false;
                        case 2:
                            if (deleteFromGroup(numericAsList, intNumberMin, rand)) {
                                break;
                            }
                            flag3 = false;
                        case 3:
                            if (deleteFromGroup(lowerAsList, intLowerMin, rand)) {
                                break;
                            }
                            flag4 = false;
                        default:
                            break;
                    }
                    if (used > specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size()) {
                        used = specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size();
                    } else {
                        if (intLengthMax != null && specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size() <= toInt(intLengthMax)) {
                            passwordLength = used;
                        } else if (!flag4 && !flag3 && !flag2 && !flag1 && intLengthMax != null) {
                            log.warn("Can't do password less then " + (specialAsList.size() + upperAsList.size() + numericAsList.size() + lowerAsList.size()));
                            log.warn("Because sum of requirements more then max password length= " + intLengthMax);
                            log.warn("Skip max password length!");
                            break;
                        }
                    }
                }
            }
        }

        passwordAsList.addAll(specialAsList);
        passwordAsList.addAll(upperAsList);
        passwordAsList.addAll(numericAsList);
        passwordAsList.addAll(lowerAsList);

        StringBuilder sb = new StringBuilder();
        Collections.shuffle(passwordAsList);
        for (String s : passwordAsList) {
            sb.append(s);
        }
        log.debug("Finish password generation!");
        return sb.toString();
    }

    private static List<String> getClearedGroupValues(char[] fullGroup, String notAllow) {
        List<String> result = new ArrayList<>();
        for (char c : fullGroup) {
            if (!notAllow.contains(String.valueOf(c))) {
                result.add(String.valueOf(c));
            }
        }
        return result;
    }

    private static List<String> getArrayOfGroup(int minSimbolsCount, Integer maxSimbolCount, String notAllow, Integer repeatios, List<String> alphabetis, Random rand) {
        List<String> result = new ArrayList<>();
        int iteration = toInt(maxSimbolCount) > minSimbolsCount ? rand.nextInt(1 + toInt(maxSimbolCount) - minSimbolsCount) + minSimbolsCount : minSimbolsCount;
        for (int i = 0; i < iteration; i++) {
            addAtGroup(result, maxSimbolCount, notAllow, repeatios, alphabetis, rand);
        }
        return result;
    }

    private static boolean addAtGroup(List<String> result, Integer maxSimbolCount, String notAllow, Integer repeatios, List<String> alphabetis, Random rand) {
        if (maxSimbolCount == null || result.size() < toInt(maxSimbolCount)) {
            String s = String.valueOf(alphabetis.get(rand.nextInt(alphabetis.size())));
            while (!isAccessibleForRepeations(repeatios, s, result) || notAllow.contains(s)) {
                s = String.valueOf(alphabetis.get(rand.nextInt(alphabetis.size())));
            }
            result.add(s);
            return true;
        }
        return false;
    }

    private static boolean deleteFromGroup(List<String> result, int minSimbolsCount, Random rand) {
        boolean var = false;
        if (result.size() > minSimbolsCount) {
            result.remove(rand.nextInt(result.size()));
            var = true;
        }
        return var;
    }


    private static boolean isAccessibleForRepeations(Integer numOfRepeations, String value, List<String> result) {
        boolean res = true;
        int rep = 0;
        if (numOfRepeations != null) {
            for (String s : result) {
                if (value.equals(s)) {
                    rep++;
                }
            }
            if (rep > numOfRepeations) {
                res = false;
            }
        }
        return res;
    }

    private static Integer toInteger(String s) {
        Integer val = null;
        if (s != null) {
            try {
                val = Integer.parseInt(s);
            } catch (Exception e) {
                log.error("can't parse int! Incoming val= " + s);
            }
        }
        return val;
    }


    private static int toInt(String s) {
        int val = 0;
        if (s != null) {
            try {
                val = Integer.parseInt(s);
            } catch (Exception e) {
                log.error("can't parse int! Incoming val= " + s);
            }
        }
        return val;
    }


    private static int toInt(Integer s) {
        int val = 0;
        if (s != null) {
            val = s;
        }
        return val;
    }


    static private boolean contain(char[] referenceString, char c) {
        for (char ch : referenceString) {
            if (ch == c) {
                return true;

            }

        }
        return false;

    }

    static private void getMissingChar(Random rand, String charset, StringBuilder sb) {
        int p = rand.nextInt(charset.length());
        sb.append(charset.charAt(p));
    }
//TEst generator
//    public static void main(String[] agrs) {
//        Policy p = new Policy();
//        p.setPolicyAttributes(new HashSet<PolicyAttribute>());
//        PolicyAttribute paLength = new PolicyAttribute();
//        paLength.setName(PWD_LEN);
//        paLength.setValue1("20");
//        paLength.setValue2("30");
//        PolicyAttribute paSpecialChars = new PolicyAttribute();
//        paSpecialChars.setName(NON_ALPHA_CHARS);
//        paSpecialChars.setValue1("5");
//        paSpecialChars.setValue2("20");
//
//        PolicyAttribute paLowerCase = new PolicyAttribute();
//        paLowerCase.setName(LOWERCASE_CHARS);
//        paLowerCase.setValue1("5");
//        paLowerCase.setValue2("20");
//
//        PolicyAttribute paUpCase = new PolicyAttribute();
//        paUpCase.setName(UPPERCASE_CHARS);
//        paUpCase.setValue1("5");
//        paUpCase.setValue2("20");
//
//        PolicyAttribute paNumbers = new PolicyAttribute();
//        paNumbers.setName(NUMERIC_CHARS);
//        paNumbers.setValue1("1");
//        paNumbers.setValue2("20");
//
//        PolicyAttribute repeatLimit = new PolicyAttribute();
//        repeatLimit.setName(LIMIT_NUM_REPEAT_CHAR);
//        repeatLimit.setValue1("0");
//
//        PolicyAttribute notAllowedChars = new PolicyAttribute();
//        notAllowedChars.setName(REJECT_CHARS_IN_PSWD);
//        notAllowedChars.setValue1("<Aabc\\gGXx$@!>");
//
//        p.getPolicyAttributes().add(paLength);
//        p.getPolicyAttributes().add(paSpecialChars);
//        p.getPolicyAttributes().add(paLowerCase);
//        p.getPolicyAttributes().add(paUpCase);
//        p.getPolicyAttributes().add(paNumbers);
//        p.getPolicyAttributes().add(repeatLimit);
//        p.getPolicyAttributes().add(notAllowedChars);
//        for (int i = 0; i < 500; i++) {
//            String pass = generatePassword(p);
//            System.out.println(String.format("L=%s, Password=%s", pass.length(), pass));
//        }
//    }


}
