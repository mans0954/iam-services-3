package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.rule.NonAlphaNumericRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml"})
public class TestPasswordGenerator extends AbstractTestNGSpringContextTests {

    private static final String nonAlphaCharset = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    private static final String numericCharset = "0123456789";
    private static final String uppercaseCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowercaseCharset = "abcdefghijklmnopqrstuvwxyz";
    private static final String alphaCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static String[] charsets = {lowercaseCharset, uppercaseCharset, numericCharset, nonAlphaCharset, alphaCharset};
    private static final int IND_LOWERCASE = 0;
    private static final int IND_UPPERCASE = 1;
    private static final int IND_NUMERIC = 2;
    private static final int IND_NON_ALPHA = 3;
    private static final int IND_ALPHA = 4;

    private static final String PWD_LEN = "PWD_LEN";
    private static final String NON_ALPHA_CHARS = "NON_ALPHA_CHARS";
    private static final String LOWERCASE_CHARS = "LOWERCASE_CHARS";
    private static final String UPPERCASE_CHARS = "UPPERCASE_CHARS";
    private static final String NUMERIC_CHARS = "NUMERIC_CHARS";
    private static final String ALPHA_CHARS = "ALPHA_CHARS";
    private static final String LIMIT_NUM_REPEAT_CHAR = "LIMIT_NUM_REPEAT_CHAR";
    private static final String REJECT_CHARS_IN_PSWD = "REJECT_CHARS_IN_PSWD";

    private static final int TEST_ITERATIONS = 10;

    private static final String[] ATTRIBUTE_NAMES = new String[] {
        PWD_LEN, NON_ALPHA_CHARS, LOWERCASE_CHARS, UPPERCASE_CHARS, NUMERIC_CHARS,
        LIMIT_NUM_REPEAT_CHAR, REJECT_CHARS_IN_PSWD, ALPHA_CHARS
    };

    private static final Log LOG = LogFactory.getLog(TestPasswordGenerator.class);

    @BeforeClass
    protected void setUp() throws Exception {
    }

    @Test
    public void testGeneratePasswordByLength() {

        for(int len=1; len < 128; len*=2) {

            DuplicatesValidator duplicatesValidator = new DuplicatesValidator();

            for(int j=0; j < TEST_ITERATIONS; j++) {
                final String password = PasswordGenerator.generatePassword(len);
                LOG.info(String.format("Test time: %d%n", System.currentTimeMillis()));
                LOG.info(String.format("generatePassword(%d). Password: %s", len, password));
                Assert.assertNotNull(password);
                Assert.assertEquals(password.length(), len,
                        String.format("Password: '%s' did not match queried length: %d", password, len));

                duplicatesValidator.validatePassword(password);
            }
        }
    }

    @Test
    public void testPwdLenPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  64,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy);
        }
    }

    @Test
    public void testLowercaseCharsPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  20,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, 10, 20);
            policy.setAttribute(LOWERCASE_CHARS, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy);
        }
    }

    @Test
    public void testUppercaseCharsPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  20,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, 10, 20);
            policy.setAttribute(UPPERCASE_CHARS, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy);
        }
    }

    @Test
    public void testUpperPolicyBound() {
        final Integer[] PWD_LEN_PAIRS = {4,5,3,4,  0,null,3,6,  0,null,3,null};
        for (int index=0; index < PWD_LEN_PAIRS.length; index += 4) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            policy.setAttribute(UPPERCASE_CHARS, PWD_LEN_PAIRS[index+2], PWD_LEN_PAIRS[index+3]);
            int uppercaseStats = 0;
            for (int j = 0; j < 100; j++) {
                final String password = PasswordGenerator.generatePassword(policy);
                LOG.info(String.format("%s. Password: %s", policy, password));
                int[] stats = policy.calcCharsStat(password);
                uppercaseStats = Math.max(uppercaseStats, stats[IND_UPPERCASE]);
            }
            Integer upperBound = PWD_LEN_PAIRS[index+3];
            if (upperBound != null && upperBound > 0) {
                Assert.assertEquals(uppercaseStats, (int)upperBound, "Generator never hits upper bound of the policy");
            } else {
                Assert.assertTrue(uppercaseStats > PWD_LEN_PAIRS[index + 2], "Generator never exceeds lower bound of the policy");
            }
        }
    }

    @Test
    public void testNumericCharsPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  20,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, 10, 20);
            policy.setAttribute(NUMERIC_CHARS, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy);
        }
    }

    @Test
    public void testAlphaCharsPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  20,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, 10, 20);
            policy.setAttribute(ALPHA_CHARS, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy);
        }
    }

    @Test
    public void testNonAlphaCharsPolicy() {

        final Integer[] PWD_LEN_PAIRS = {0,10,  10,null,  5,10,  10,10,  20,null,  0,1};

        for (int index=0; index < PWD_LEN_PAIRS.length; index += 2) {
            TestPolicy policy = new TestPolicy();
            policy.setAttribute(PWD_LEN, 10, 20);
            policy.setAttribute(NON_ALPHA_CHARS, PWD_LEN_PAIRS[index], PWD_LEN_PAIRS[index+1]);
            runPasswordGeneratorTests(policy, 200);
        }
    }

    @Test
    public void testPolicyCombinations() {

        TestPolicy policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 10, 20);
        policy.setAttribute(LOWERCASE_CHARS, 3, null);
        policy.setAttribute(UPPERCASE_CHARS, 3, null);
        policy.setAttribute(NON_ALPHA_CHARS, 3, null);
        runPasswordGeneratorTests(policy);

        policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 10, 20);
        policy.setAttribute(LOWERCASE_CHARS, 3, null);
        policy.setAttribute(UPPERCASE_CHARS, 3, 5);
        policy.setAttribute(NON_ALPHA_CHARS, 3, 7);
        runPasswordGeneratorTests(policy);

        policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 10, 20);
        policy.setAttribute(LOWERCASE_CHARS, 0, 4);
        policy.setAttribute(UPPERCASE_CHARS, 0, 5);
        policy.setAttribute(NON_ALPHA_CHARS, 3, null);
        runPasswordGeneratorTests(policy);

        policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 10, 20);
        policy.setAttribute(LOWERCASE_CHARS, 0, 5);
        policy.setAttribute(UPPERCASE_CHARS, 0, 5);
        policy.setAttribute(NON_ALPHA_CHARS, 0, 5);
        policy.setAttribute(ALPHA_CHARS, 0, 8);
        runPasswordGeneratorTests(policy);

        policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 0, 20);
        policy.setAttribute(LOWERCASE_CHARS, 2, null);
        policy.setAttribute(UPPERCASE_CHARS, 2, null);
        policy.setAttribute(NUMERIC_CHARS, 1, null);
        policy.setAttribute(NON_ALPHA_CHARS, 1, null);
        policy.setAttribute(ALPHA_CHARS, 6, null);
        runPasswordGeneratorTests(policy);

        policy = new TestPolicy();
        policy.setAttribute(PWD_LEN, 8, null);
        policy.setAttribute(LOWERCASE_CHARS, 2, 5);
        policy.setAttribute(UPPERCASE_CHARS, 2, 5);
        policy.setAttribute(NUMERIC_CHARS, 2, null);
        policy.setAttribute(NON_ALPHA_CHARS, 2, null);
        policy.setAttribute(ALPHA_CHARS, 6, 8);
        runPasswordGeneratorTests(policy);
    }

    private void runPasswordGeneratorTests(TestPolicy policy) {
        runPasswordGeneratorTests(policy, TEST_ITERATIONS);
    }

    private void runPasswordGeneratorTests(TestPolicy policy, int iterations) {

        DuplicatesValidator duplicatesValidator = new DuplicatesValidator();

        for (int j = 0; j < iterations; j++) {
            final String password = PasswordGenerator.generatePassword(policy);
            LOG.info(String.format("%s. Password: %s", policy, password));
            policy.validatePassword(password);

            duplicatesValidator.validatePassword(password);
        }
    }

    // Helper Policy extension
    static class TestPolicy extends Policy {

        public TestPolicy() {
            for (final String attrName : ATTRIBUTE_NAMES) {
                PolicyAttribute attr = new PolicyAttribute();
                attr.setName(attrName);
                addPolicyAttribute(attr);
            }
        }

        public void setAttribute(String attrName, Integer value1, Integer value2) {
            PolicyAttribute attr = getAttribute(attrName);
            if (attr == null) {
                attr = new PolicyAttribute();
                attr.setName(attrName);
            }
            attr.setValue1(value1 == null ? null : String.valueOf(value1));
            attr.setValue2(value2 == null ? null : String.valueOf(value2));
            addPolicyAttribute(attr);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("Policy [");
            for (final PolicyAttribute attr : this.getPolicyAttributes()) {
                if (!isAttributeEmpty(attr)) {
                    builder.append(attr.getName())
                            .append("(").append(attr.getValue1())
                            .append(",").append(attr.getValue2()).append(") ");
                }
            }
            builder.append("]");
            return builder.toString();
        }

        public void validatePassword(final String password) {

            Assert.assertNotNull(password, "Password was not generated");

            int[] stats = calcCharsStat(password);
            validateRange(password, password.length(), getAttribute(PWD_LEN));
            validateRange(password, stats[IND_LOWERCASE], getAttribute(LOWERCASE_CHARS));
            validateRange(password, stats[IND_UPPERCASE], getAttribute(UPPERCASE_CHARS));
            validateRange(password, stats[IND_NUMERIC], getAttribute(NUMERIC_CHARS));
            validateRange(password, stats[IND_NON_ALPHA], getAttribute(NON_ALPHA_CHARS));
            validateRange(password, stats[IND_ALPHA], getAttribute(ALPHA_CHARS));
        }

        private void validateRange(String password, int value, PolicyAttribute attr) {

            if (isAttributeEmpty(attr)) {
                return;
            }

            int minValue = Integer.valueOf(attr.getValue1());
            Integer maxValue = toInteger(attr.getValue2());
            final String policyName = attr.getName();

            if (minValue > 0) {
                Assert.assertTrue(value >= minValue,
                        String.format("Password: '%s' has not enough %s. Limits are [%d, %d]", password, policyName, minValue, maxValue));
            }

            if (maxValue != null) {
                Assert.assertTrue(value <= maxValue,
                        String.format("Password: '%s' has to many %s. Limits are [%d, %d]", password, policyName, minValue, maxValue));
            }

        }

        private Integer toInteger(String s) {
            return (s != null) ? Integer.parseInt(s) : null;
        }

        private boolean isAttributeEmpty(PolicyAttribute attr) {
            return (attr == null
                    || (StringUtils.isEmpty(attr.getValue1())
                    && StringUtils.isEmpty(attr.getValue2())));
        }

        public int[] calcCharsStat(String password) {
            int[] stats = new int[charsets.length];
            Arrays.fill(stats, 0);
            for (char ch : password.toCharArray()) {
                for(int i=0; i<charsets.length; i++) {
                    if (charsets[i].indexOf(ch) != -1) {
                        ++stats[i];
                    }
                }
            }
            return stats;
        }
    }

    private class DuplicatesValidator {

        private String lastPassword = null;

        public void validatePassword(String password) {
            if (lastPassword != null && password.length() > 3) {
                // validate password is not repeating
                Assert.assertNotEquals(password, lastPassword, "Generated password duplicates the previous one");
            }
            lastPassword = password;
        }
    }
}
