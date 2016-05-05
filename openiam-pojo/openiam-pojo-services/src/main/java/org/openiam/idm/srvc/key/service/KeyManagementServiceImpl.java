package org.openiam.idm.srvc.key.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.dto.UserSecurityWrapper;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.UserIdentityAnswerDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import java.util.*;

/**
 * Created by: Alexander Duckardt Date: 09.10.12
 */
@Service("keyManagementService")
public class KeyManagementServiceImpl implements KeyManagementService, ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final int FETCH_COUNT = 1000;
    @Value("${iam.jks.path}")
    private String jksFile;
    @Value("${iam.jks.password}")
    private String jksPassword;
    @Value("${iam.jks.key.password}")
    private String keyPassword;
    @Value("${iam.jks.cookie.key.password}")
    private String cookieKeyPassword;
    @Value("${iam.jks.common.key.password}")
    private String commonKeyPassword;
    private Integer iterationCount;
    private JksManager jksManager;

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Value("${org.openiam.userkeys.cache.enabled.on.init}")
    private Boolean initCacheOnInit;



    @Autowired
    private Cryptor cryptor;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginDAO loginDAO;
    @Autowired
    private UserKeyDao userKeyDao;
    @Autowired
    private PasswordHistoryDAO passwordHistoryDao;
    @Autowired
    private ManagedSysDAO managedSysDAO;
    @Autowired
    private UserIdentityAnswerDAO userIdentityAnswerDAO;
    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;


    private ApplicationContext ac;

    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(this.jksFile)) {
            this.jksFile = JksManager.KEYSTORE_DEFAULT_LOCATION + JksManager.KEYSTORE_FILE_NAME;
        }
        if (!StringUtils.hasText(this.jksPassword)) {
            this.jksPassword = JksManager.KEYSTORE_DEFAULT_PASSWORD;
        }
        if (!StringUtils.hasText(this.cookieKeyPassword)) {
            this.cookieKeyPassword = JksManager.KEYSTORE_DEFAULT_PASSWORD;
        }
        if (!StringUtils.hasText(this.keyPassword)) {
            throw new NullPointerException("The password for master key is required. This property cannot be read from properties files");
        }
        if (this.iterationCount == null) {
            jksManager = new JksManager(this.jksFile);
        } else {
            jksManager = new JksManager(this.jksFile, this.iterationCount);
        }
        if(initCacheOnInit){
            cacheUserKeys();
        }

    }


    private void cacheUserKeys() {
        long userCount = userManager.getTotalNumberOfUsers();
        int from = 0;
        int maxSize = 1000;
        KeyManagementService proxyService = getProxyService();
        try {
            while (from < userCount){
                log.info(String.format("CacheUserKeys: Fetching from %s, size: %s", from, maxSize));
                List<String> userIds = userManager.getUserIDs(from, maxSize);
                log.info(String.format("CacheUserKeys: Fetched from %s, size: %s.  Caching keys...", from, maxSize));
                if(CollectionUtils.isNotEmpty(userIds)){
                    List<UserKey> userKeys = proxyService.getByUserIdsKeyName(userIds, KeyName.password.name());
                    if(CollectionUtils.isNotEmpty(userKeys)){
                        for(UserKey userKey: userKeys){
                            proxyService.getUserKey(userKey);
                        }
                    }



                }
                from += maxSize;
            }
        } catch (EncryptionException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getSystemUserKey(String keyName) throws EncryptionException {
        return getProxyService().getUserKey(systemUserId, keyName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserKey> getByUserIdsKeyName(List<String> userIds, String keyName) {
        return userKeyDao.getByUserIdsKeyName(userIds, keyName);
    }


    @Override
    @Cacheable(value = "userkeys", key = "{ #userId, #keyName}")
    @Transactional(readOnly = true)
    public byte[] getUserKey(String userId, String keyName) throws EncryptionException {
        System.out.println(String.format("==== GET USER KEY FOR PWD: {userID:%s, keyName:%s} ", userId, keyName));
        try {
            UserKey uk = userKeyDao.getByUserIdKeyName(userId, keyName);
            return this.getUserKey(uk);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EncryptionException(e);
        }
    }

    @Override
    @Cacheable(value = "userkeys", key = "{ #uk.userId, #uk.name}")
    public byte[] getUserKey(UserKey uk) throws EncryptionException {
        System.out.println(String.format("==== GET USER KEY FOR PWD: {userID:%s, keyName:%s} ", uk.getUserId(), uk.getName()));
        if (uk == null) {
            return null;
        }

        byte[] masterKey = new byte[0];
        try {
            masterKey = getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
            if (masterKey == null || masterKey.length == 0) {
                throw new IllegalAccessException("Cannot get master key to decrypt user keys");
            }
            return jksManager.decodeKey(this.decrypt(masterKey, uk.getKey()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EncryptionException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "userkeys", allEntries = true)
    })
    public void initKeyManagement() throws Exception{
        this.generateMasterKey();
        this.generateCookieKey();
        this.generateCommonKey();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "userkeys", allEntries = true)
    })
    public void generateMasterKey() throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Start generating new master key...");
            log.debug("Loading user data ...");
        }

        HashMap<String, UserSecurityWrapper> userSecurityMap = getSecurityMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();
        if(log.isDebugEnabled()) {
            log.debug("Try to get old salt ...");
        }
        byte[] oldMasterKey = this.getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        if (oldMasterKey != null && oldMasterKey.length > 0) {
            if(log.isDebugEnabled()) {
                log.debug("OLD MASTER KEY IS: " + jksManager.encodeKey(oldMasterKey));
                log.debug("Decrypting user data ...");
            }
            decryptData(oldMasterKey, userSecurityMap);
            if(log.isDebugEnabled()) {
                log.debug("Decrypting user data finished successfully");
            }
        } else {
            if(log.isDebugEnabled()) {
                log.debug("OLD MASTER KEY IS NULL");
            }
        }
        if(log.isDebugEnabled()) {
            log.debug(" Generation of new master key ...");
        }
//        jksManager.generateMasterKey(this.jksPassword.toCharArray(), this.keyPassword.toCharArray());
//
//        byte[] masterKey = this.getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        byte[] masterKey = generateJKSKey(this.keyPassword, JksManager.KEYSTORE_ALIAS);
        if (masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }
        if(log.isDebugEnabled()) {
            log.debug("NEW MASTER KEY IS: " + jksManager.encodeKey(masterKey));

            log.debug("Ecrypting user data ...");
        }
        encryptData(masterKey, userSecurityMap, newUserKeyList);
        if(log.isDebugEnabled()) {
            log.debug("Ecrypting user data finished successfully");


            log.debug("Refreshing user keys...");
        }
        userKeyDao.deleteAll();
        addUserKeys(newUserKeyList);
        if(log.isDebugEnabled()) {
            log.debug("End generating new master key...");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateUserKeys(String userId) throws Exception {
        UserEntity userEntity = userDAO.findById(userId);
        if (userEntity != null) {
            return generateUserKeys(userEntity);
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateUserKeys(UserEntity user) throws Exception {
        byte[] masterKey = getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        if (masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }
        List<UserEntity> userList = new ArrayList<UserEntity>();
        userList.add(user);
//        List<LoginEntity> lgList = loginDAO.findUser(user.getId());
        List<LoginEntity> lgList = user.getPrincipalList();
//        List<UserKey> keyList = userKeyDao.getByUserId(user.getId());
//        UserIdentityAnswerEntity example = new UserIdentityAnswerEntity();
//        example.setUserId(user.getId());
//        List<UserIdentityAnswerEntity> answersList = userIdentityAnswerDAO.getByExample(example);

        List<PasswordHistoryEntity> pwdList = new ArrayList<PasswordHistoryEntity>();
        for (LoginEntity lg : lgList) {
//            pwdList.addAll(passwordHistoryDao.getPasswordHistoryByLoginId(lg.getLoginId(), 0, Integer.MAX_VALUE));
            pwdList.addAll(lg.getPasswordHistory());
        }
        List<ManagedSysEntity> managedSysList = null;
        if(systemUserId.equals(user.getId())){
            managedSysList = getManagedSysMap().get(user.getId());
        }

        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        UserSecurityWrapper usw = new UserSecurityWrapper();
        usw.setUserId(user.getId());
        usw.setLoginList(lgList);
        usw.setManagedSysList(managedSysList);
        usw.setPasswordHistoryList(pwdList);
//        usw.setUserKeyList(keyList);
//        usw.setUserIdentityAnswerList(answersList);

        encryptUserData(masterKey, usw, newUserKeyList);
        // replace user key for given user. DON'T NECESSARY TO DELETE DUE TO IT IS CALLED FOR NEW USER
//        userKeyDao.deleteByUserId(user.getId());
        addUserKeys(newUserKeyList);
        return 2L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void migrateData(String oldSecretKey) throws Exception {

        HashMap<String, UserSecurityWrapper> userSecurityMap = getSecurityMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        jksManager.generateMasterKey(this.jksPassword.toCharArray(), this.keyPassword.toCharArray());

        byte[] masterKey = this.getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        if (masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot generate master key to encrypt user keys");
        }

        if (userSecurityMap != null && !userSecurityMap.isEmpty()) {
            for (String userId : userSecurityMap.keySet()) {
                // decrypt user data
                if (!"0001".equals(userId)) {
                    decryptOldData(jksManager.decodeKey(oldSecretKey), userSecurityMap.get(userId));
                  //  decryptSecurityDataForUser(jksManager.decodeKey(oldSecretKey), userSecurityMap.get(userId));
                }
                // reencrypt user data
                encryptUserData(masterKey, userSecurityMap.get(userId), newUserKeyList);

            }
        }
        // replace user key for given user
        userKeyDao.deleteAll();
        addUserKeys(newUserKeyList);
    }

    private void decryptOldData(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        decryptUserPasswords(key, userSecurityWrapper);
    }

    @Override
    public byte[] getCookieKey() throws Exception {
        byte[] key = this.getPrimaryKey(JksManager.KEYSTORE_COOKIE_ALIAS, this.cookieKeyPassword);
        if (key == null || key.length == 0) {
            return generateCookieKey();
        }
        return key;
    }

    @Override
    public byte[] generateCookieKey() throws Exception {
        return generateJKSKey(this.cookieKeyPassword, JksManager.KEYSTORE_COOKIE_ALIAS);
    }

    @Override
    public byte[] getCommonKey() throws Exception {
        byte[] key = this.getPrimaryKey(JksManager.KEYSTORE_COMMON_ALIAS, this.commonKeyPassword);
        if (key == null || key.length == 0) {
            return generateCommonKey();
        }
        return key;
    }

    @Override
    public byte[] generateCommonKey() throws Exception {
        return generateJKSKey(this.commonKeyPassword, JksManager.KEYSTORE_COMMON_ALIAS);
    }

    @Override
    public String encryptData(String data)throws Exception{
        return encryptData(null, data);
    }
    @Override
    public String decryptData(String encryptedData)throws Exception{
        return decryptData(null, encryptedData);
    }
    @Override
    public String encryptData(String userId, String data)throws Exception{
        byte[] dataKey = null;
        if(StringUtils.hasText(userId)){
            dataKey = getProxyService().getUserKey(userId, KeyName.dataKey.name());
        } else {
            dataKey = this.getCommonKey();
        }

        if(dataKey==null)
            throw new Exception("Cannot encrypt data, due to invalid secret key");
        if(!StringUtils.hasText(data))
            return data;
        return this.encrypt(dataKey, data);
    }

    @Override
    public String decryptData(String userId, String encryptedData)throws Exception{
        byte[] dataKey = null;
        if(StringUtils.hasText(userId)){
            dataKey = getProxyService().getUserKey(userId, KeyName.dataKey.name());
        } else {
            dataKey = this.getCommonKey();
        }

        if(dataKey==null)
            throw new Exception("Cannot decrypt data, due to invalid secret key");
        if(!StringUtils.hasText(encryptedData))
           return encryptedData;
        return this.decrypt(dataKey, encryptedData);
    }
    @Override
    public String encrypt(String userId, KeyName keyName, String data)throws Exception{
        return encrypt(getProxyService().getUserKey(userId, keyName.name()), data);
    }
    @Override
    public String encrypt(byte[] key, String data)throws Exception{
        if(key!=null && key.length>0){
            return cryptor.encrypt(key, data);
        }
        if(log.isDebugEnabled()) {
            log.debug("Data Key is null. Skipping ecryption...");
        }
        return null;
    }
    @Override
    public String decrypt(String userId, KeyName keyName, String encryptedData)throws Exception{
        return decrypt(getProxyService().getUserKey(userId, keyName.name()), encryptedData);
    }
    @Override
    public String decrypt(byte[] key, String encryptedData)throws Exception{
        if(key!=null && key.length>0){
            return cryptor.decrypt(key, encryptedData);
        }
        if(log.isDebugEnabled()) {
            log.debug("Data Key is null. Skipping decryption...");
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptData(byte[] masterKey, HashMap<String, UserSecurityWrapper> userSecurityMap, List<UserKey> newUserKeyList) throws Exception {
        if (userSecurityMap != null && !userSecurityMap.isEmpty()) {
            for (String userId : userSecurityMap.keySet()) {
                encryptUserData(masterKey, userSecurityMap.get(userId), newUserKeyList);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptUserData(byte[] masterKey, UserSecurityWrapper userSecurityWrapper, List<UserKey> newUserKeyList) throws Exception {
        byte[] pwdKey = jksManager.getNewPrivateKey();
        byte[] tokenKey = jksManager.getNewPrivateKey();
        byte[] dataKey = jksManager.getNewPrivateKey();
        byte[] answerKey = jksManager.getNewPrivateKey();
        // create new USER KEYS
        UserKey uk = new UserKey();
        uk.setKey(this.encrypt(masterKey, jksManager.encodeKey(pwdKey)));
        uk.setName(KeyName.password.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setKey(this.encrypt(masterKey, jksManager.encodeKey(tokenKey)));
        uk.setName(KeyName.token.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setKey(this.encrypt(masterKey, jksManager.encodeKey(dataKey)));
        uk.setName(KeyName.dataKey.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setKey(this.encrypt(masterKey, jksManager.encodeKey(answerKey)));
        uk.setName(KeyName.challengeResponse.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        // log.debug("NEW USER KEYS ARE: [USER_ID: " + user.getUserId() +
        // "; PWD_KEY: " + jksManager.encodeKey(pwdKey) + "; TKN_KEY: "
        // + jksManager.encodeKey(tokenKey) + "]");

        // encrypt user data
        encryptUserPasswords(pwdKey, userSecurityWrapper);
        encryptUserChallengeResponses(answerKey, userSecurityWrapper);


        // log.debug("Printing ecrypted data...");
        // printUserData(user, pwdHistoryMap, managedSysMap);
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptUserChallengeResponses(byte[] key, UserSecurityWrapper userSecurityWrapper)throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Encrypt user ChallengeResponses ...");
        }
        if (userSecurityWrapper.getUserIdentityAnswerList() != null && !userSecurityWrapper.getUserIdentityAnswerList().isEmpty()) {
            for (UserIdentityAnswerEntity answer : userSecurityWrapper.getUserIdentityAnswerList()) {
                if(StringUtils.hasText(answer.getQuestionAnswer()))
                    answer.setQuestionAnswer(this.encrypt(key, answer.getQuestionAnswer()));
                    answer.setIsEncrypted(true);
                    userIdentityAnswerDAO.update(answer);
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Encrypt user ChallengeResponses FINISHED...");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptUserPasswords(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Encrypt user passwords ...");
        }
        if (userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for (LoginEntity login : userSecurityWrapper.getLoginList()) {
                if(StringUtils.hasText(login.getPassword())){
                    login.setPassword(this.encrypt(key, login.getPassword()));
                    loginDAO.update(login);
                }
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Encrypt user passwords history...");
        }
        if (userSecurityWrapper.getPasswordHistoryList() != null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()) {
            for (PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                if(StringUtils.hasText(pwd.getPassword())){
                    pwd.setPassword(this.encrypt(key, pwd.getPassword()));
                    passwordHistoryDao.update(pwd);
                }
            }
        }

        if (userSecurityWrapper.getManagedSysList() != null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            if(log.isDebugEnabled()) {
                log.debug("Encrypt manages sys...");
            }
            for (ManagedSysEntity ms : userSecurityWrapper.getManagedSysList()) {
                if (ms.getPswd() != null) {
                    ms.setPswd(this.encrypt(key, ms.getPswd()));
                }
                managedSysDAO.save(ms);
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Encrypt user passwords FINISHED...");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptData(byte[] masterKey, HashMap<String, UserSecurityWrapper> userSecurityMap) throws Exception {
        if (userSecurityMap != null && !userSecurityMap.isEmpty()) {
            for (String userId : userSecurityMap.keySet()) {
                decryptUserData(masterKey, userSecurityMap.get(userId));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptUserData(byte[] masterKey, UserSecurityWrapper userSecurityWrapper) throws Exception {
        if (userSecurityWrapper.getUserKeyList() != null && !userSecurityWrapper.getUserKeyList().isEmpty()) {
            // the keys exist, it is necessary to refresh them
            if (masterKey == null || masterKey.length == 0) {
                throw new NullPointerException("Cannot read master key for decrypting user keys.");
            }
            decryptSecurityDataForUser(masterKey, userSecurityWrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptSecurityDataForUser(byte[] masterKey, UserSecurityWrapper userSecurityWrapper) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user data ...");
        }
        for (UserKey uk : userSecurityWrapper.getUserKeyList()) {
            byte[] key = jksManager.decodeKey(this.decrypt(masterKey, uk.getKey()));
            if (KeyName.password.name().equals(uk.getName())) {
                decryptUserPasswords(key, userSecurityWrapper);
            } else if (KeyName.challengeResponse.name().equals(uk.getName())) {
                decryptUserChallengeResponse(key, userSecurityWrapper);
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user data FINISHED...");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptUserChallengeResponse(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user ChallengeResponses ...");
        }
        if (userSecurityWrapper.getUserIdentityAnswerList() != null && !userSecurityWrapper.getUserIdentityAnswerList().isEmpty()) {
            for (UserIdentityAnswerEntity answer : userSecurityWrapper.getUserIdentityAnswerList()) {
                if(answer.getIsEncrypted()){
                    if(StringUtils.hasText(answer.getQuestionAnswer()))
                        answer.setQuestionAnswer(this.decrypt(key, answer.getQuestionAnswer()));
                }
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user ChallengeResponses FINISHED...");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptUserPasswords(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user passwords ...");
        }
        if (userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for (LoginEntity login : userSecurityWrapper.getLoginList()) {
                if(StringUtils.hasText(login.getPassword()))
                    login.setPassword(this.decrypt(key, login.getPassword()));
            }
        }
        if (userSecurityWrapper.getPasswordHistoryList() != null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()) {
            if(log.isDebugEnabled()) {
                log.debug("Decrypting user passwords history ...");
            }
            for (PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                if(StringUtils.hasText(pwd.getPassword()))
                    pwd.setPassword(this.decrypt(key, pwd.getPassword()));
            }
        }

        if (userSecurityWrapper.getManagedSysList() != null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            if(log.isDebugEnabled()) {
                log.debug("Decrypting manages sys ...");
            }
            for (ManagedSysEntity ms : userSecurityWrapper.getManagedSysList()) {
                if (ms.getPswd() != null) {
                    ms.setPswd(this.decrypt(key, ms.getPswd()));
                }
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Decrypting user passwords FINISHED...");
        }
    }

    private void printUserData(User user, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSysDto>> managedSysMap) {
        StringBuilder msg = new StringBuilder();
        msg.append("LOGIN LIST FOR USER_ID(" + user.getId() + ") [\n");
        if (user.getPrincipalList() != null && !user.getPrincipalList().isEmpty()) {
            for (Login login : user.getPrincipalList()) {
                msg.append("\t{login:" + login.getLogin() + ";password:" + login.getPassword() + "}\n");
            }
        }
        msg.append("]");
        log.debug(msg.toString());
        msg.setLength(0);

        msg.append("PWD HISTORY LIST FOR USER_ID(" + user.getId() + ") [\n");
        if (pwdHistoryMap.containsKey(user.getId())) {
            for (PasswordHistory pwd : pwdHistoryMap.get(user.getId())) {
                msg.append("\tpassword:" + pwd.getPassword() + "\n");
            }
        }
        msg.append("]");
        log.debug(msg.toString());
        msg.setLength(0);

        msg.append("MANAGED SYS LIST FOR USER_ID(" + user.getId() + ") [\n");
        if (managedSysMap.containsKey(user.getId())) {
            for (ManagedSysDto ms : managedSysMap.get(user.getId())) {
                if (ms.getPswd() != null) {
                    msg.append("\tpassword:" + ms.getPswd() + "\n");
                } else {
                    msg.append("\tpassword: NULL\n");
                }
            }
        }
        msg.append("]");
        log.debug(msg.toString());
        msg.setLength(0);
    }

    @Transactional
    private void addUserKeys(List<UserKey> newUserKeyList) {
        if (newUserKeyList != null && !newUserKeyList.isEmpty()) {
            for (UserKey uk : newUserKeyList) {
                userKeyDao.save(uk);
                addToCache(uk);
            }
        }
    }
    //  precache users keys to avoid database hit
    private void addToCache(UserKey uk){
        try {
            byte[] key = getProxyService().getUserKey(uk);
        } catch (EncryptionException e) {
            if(log.isWarnEnabled()){
                log.warn("Cannot add to cache");
            }
        }
    }

    private byte[] getPrimaryKey(String alias, String keyPassword) throws Exception {
        return jksManager.getPrimaryKeyFromJKS(alias, jksPassword.toCharArray(), keyPassword.toCharArray());
    }

    private byte[] generateJKSKey(String keyPassword, String keystoreAlias) throws Exception {
        jksManager.generatePrimaryKey(this.jksPassword.toCharArray(), keyPassword.toCharArray(), keystoreAlias);
        return this.getPrimaryKey(keystoreAlias, keyPassword);
    }

    private HashMap<String, UserSecurityWrapper> getSecurityMap() throws Exception {
        HashMap<String, UserSecurityWrapper> result = new HashMap<String, UserSecurityWrapper>();

        Long userCount = userDAO.getUserCount();
        if (userCount != null && userCount > 0) {
            long fetchedDataCount = 0l;
            Set<String> userIdList = new HashSet<String>();
            int pageNum = 0;
            do {
                userIdList.addAll(userDAO.getUserIdList(pageNum * FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long) userIdList.size();
                pageNum++;
            } while (fetchedDataCount < userCount);

            userIdList.add(systemUserId);

            HashMap<String, List<ManagedSysEntity>> managedSysMap = getManagedSysMap();
            HashMap<String, List<LoginEntity>> loginMap = getLoginMap();
            HashMap<String, List<UserKey>> userKeyMap = getUserKeyMap();
            HashMap<String, List<PasswordHistoryEntity>> pwdHistoryMap = getPasswordHistoryMap(loginMap);
            HashMap<String, List<UserIdentityAnswerEntity>> identityAnswerMap = getIdentityAnswerMap();

            for (String userId : userIdList) {
                UserSecurityWrapper usw = new UserSecurityWrapper();
                usw.setUserId(userId);
                usw.setLoginList(loginMap.get(userId));
                usw.setManagedSysList(managedSysMap.get(userId));
                usw.setPasswordHistoryList(pwdHistoryMap.get(userId));
                usw.setUserKeyList(userKeyMap.get(userId));
                usw.setUserIdentityAnswerList(identityAnswerMap.get(userId));

                result.put(userId, usw);
            }
        }
        return result;
    }

    private HashMap<String, List<UserKey>> getUserKeyMap() throws Exception {
        HashMap<String, List<UserKey>> keyMap = new HashMap<String, List<UserKey>>();

        Long keyCount = userKeyDao.countAll();
        if (keyCount != null && keyCount > 0) {
            long fetchedDataCount = 0l;
            Set<UserKey> keyList = new HashSet<UserKey>();
            int pageNum = 0;
            do {
                keyList.addAll(userKeyDao.getSublist(pageNum * FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long) keyList.size();
                pageNum++;
            } while (fetchedDataCount < keyCount);

            for (UserKey key : keyList) {
                if (!keyMap.containsKey(key.getUserId())) {
                    keyMap.put(key.getUserId(), new ArrayList<UserKey>());
                }
                keyMap.get(key.getUserId()).add(key);
            }
        }
        return keyMap;
    }

    private HashMap<String, List<LoginEntity>> getLoginMap() {
        HashMap<String, List<LoginEntity>> lgMap = new HashMap<String, List<LoginEntity>>();

        Long lgCount = loginDAO.getLoginCount();
        if (lgCount != null && lgCount > 0) {
            long fetchedDataCount = 0l;
            Set<LoginEntity> loginList = new HashSet<LoginEntity>();
            int pageNum = 0;
            do {
                loginList.addAll(loginDAO.getLoginSublist(pageNum * FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long) loginList.size();
                pageNum++;
            } while (fetchedDataCount < lgCount);

            for (LoginEntity lg : loginList) {
                if (!lgMap.containsKey(lg.getUserId())) {
                    lgMap.put(lg.getUserId(), new ArrayList<LoginEntity>());
                }
                lgMap.get(lg.getUserId()).add(lg);
            }
        }
        return lgMap;
    }

    private HashMap<String, List<UserIdentityAnswerEntity>> getIdentityAnswerMap() {
        HashMap<String, List<UserIdentityAnswerEntity>> answMap = new HashMap<String, List<UserIdentityAnswerEntity>>();
        Long answCount = userIdentityAnswerDAO.countAll();
        if (answCount != null && answCount > 0) {
            long fetchedDataCount = 0l;
            Set<UserIdentityAnswerEntity> answerList = new HashSet<UserIdentityAnswerEntity>();
            int pageNum = 0;
            do {
                answerList.addAll(userIdentityAnswerDAO.getByExample(new UserIdentityAnswerEntity(),
                                                                     pageNum * FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long) answerList.size();
                pageNum++;
            } while (fetchedDataCount < answCount);

            for (UserIdentityAnswerEntity answ : answerList) {
                if (!answMap.containsKey(answ.getUserId())) {
                    answMap.put(answ.getUserId(), new ArrayList<UserIdentityAnswerEntity>());
                }
                answMap.get(answ.getUserId()).add(answ);
            }
        }
        return answMap;
    }

    private HashMap<String, List<ManagedSysEntity>> getManagedSysMap() {
        HashMap<String, List<ManagedSysEntity>> managedSysMap = new HashMap<String, List<ManagedSysEntity>>();
        List<ManagedSysEntity> mngSysList = managedSysDAO.findAllManagedSys();
        if (mngSysList != null && !mngSysList.isEmpty()) {
            for (ManagedSysEntity ms : mngSysList) {
                if (!managedSysMap.containsKey(systemUserId)) {
                    managedSysMap.put(systemUserId, new ArrayList<ManagedSysEntity>());
                }
                managedSysMap.get(systemUserId).add(ms);
            }
        }
        return managedSysMap;
    }

    private HashMap<String, List<PasswordHistoryEntity>> getPasswordHistoryMap(final HashMap<String, List<LoginEntity>> loginMap) {
        final HashMap<String, List<PasswordHistoryEntity>> pwdHistoryMap = new HashMap<String, List<PasswordHistoryEntity>>();

        if (loginMap != null && !loginMap.isEmpty()) {

            final Long pwdCount = passwordHistoryDao.getCount();
            if (pwdCount != null && pwdCount > 0) {
                long fetchedDataCount = 0l;
                final Set<PasswordHistoryEntity> pwdList = new HashSet<PasswordHistoryEntity>();
                final HashMap<String, List<PasswordHistoryEntity>> pwdMap = new HashMap<String, List<PasswordHistoryEntity>>();

                int pageNum = 0;
                do {
                    pwdList.addAll(passwordHistoryDao.getSublist(pageNum * FETCH_COUNT, FETCH_COUNT));
                    fetchedDataCount = (long) pwdList.size();
                    pageNum++;
                } while (fetchedDataCount < pwdCount);

                for (final PasswordHistoryEntity ph : pwdList) {
                    if (!pwdMap.containsKey(ph.getLogin().getLoginId())) {
                        pwdMap.put(ph.getLogin().getLoginId(), new ArrayList<PasswordHistoryEntity>());
                    }
                    pwdMap.get(ph.getLogin().getLoginId()).add(ph);
                }

                // map to userId
                for (String userId : loginMap.keySet()) {
                    for (LoginEntity lg : loginMap.get(userId)) {
                        if (pwdMap.containsKey(lg.getLoginId())) {
                            if (!pwdHistoryMap.containsKey(userId)) {
                                pwdHistoryMap.put(userId, new ArrayList<PasswordHistoryEntity>());
                            }
                            pwdHistoryMap.get(userId).addAll(pwdMap.get(lg.getLoginId()));
                        }
                    }
                }
            }
        }
        return pwdHistoryMap;
    }

    private KeyManagementService getProxyService() {
        KeyManagementService service = (KeyManagementService)ac.getBean("keyManagementService");
        return service;
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateKeysForUserList(List<String> userIds)throws Exception{
        byte[] masterKey = getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        if (masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }

        HashMap<String, List<LoginEntity>> loginMap = getLoginMap(userIds);
        HashMap<String, List<UserKey>> userKeyMap = getUserKeyMap(userIds);


        HashMap<String, UserSecurityWrapper> userSecurityMap = new HashMap<String, UserSecurityWrapper>();
        HashMap<String, UserSecurityWrapper> userSecurityMapToDecript = new HashMap<String, UserSecurityWrapper>();

        for (String userId : userIds) {
            UserSecurityWrapper usw = new UserSecurityWrapper();
            usw.setUserId(userId);
            usw.setLoginList(loginMap.get(userId));
            usw.setUserKeyList(userKeyMap.get(userId));
            userSecurityMap.put(userId, usw);
        }

        List<UserKey> newUserKeyList = new ArrayList<>();

        for (String userId : userSecurityMap.keySet()) {
            List<UserKey> userKeyList = userSecurityMap.get(userId).getUserKeyList();
            if(CollectionUtils.isNotEmpty(userKeyList)) {
                decryptUserData(masterKey, userSecurityMap.get(userId));
            }
        }

        encryptData(masterKey, userSecurityMap, newUserKeyList);
        for (String userId : userSecurityMap.keySet()) {
            userKeyDao.deleteByUserId(userId);
        }
        addUserKeys(newUserKeyList);
    }


    private HashMap<String, List<LoginEntity>> getLoginMap(List<String> userIds) {
        HashMap<String, List<LoginEntity>> lgMap = new HashMap<String, List<LoginEntity>>();

        List<LoginEntity> loginList = loginDAO.findByUserIds(userIds, null);
        if (CollectionUtils.isNotEmpty(loginList)) {
            for (LoginEntity lg : loginList) {
                if (!lgMap.containsKey(lg.getUserId())) {
                    lgMap.put(lg.getUserId(), new ArrayList<LoginEntity>());
                }
                lgMap.get(lg.getUserId()).add(lg);
            }
        }
        return lgMap;
    }
    private HashMap<String, List<UserKey>> getUserKeyMap(List<String> userIds) throws Exception {
        HashMap<String, List<UserKey>> keyMap = new HashMap<String, List<UserKey>>();

        List<UserKey> userKeyList= userKeyDao.getByUserIdsKeyName(userIds, null);
        if (CollectionUtils.isNotEmpty(userKeyList)) {

            for (UserKey key : userKeyList) {
                if (!keyMap.containsKey(key.getUserId())) {
                    keyMap.put(key.getUserId(), new ArrayList<UserKey>());
                }
                keyMap.get(key.getUserId()).add(key);
            }
        }
        return keyMap;
    }
}
