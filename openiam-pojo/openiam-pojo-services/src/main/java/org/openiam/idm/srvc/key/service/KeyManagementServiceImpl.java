package org.openiam.idm.srvc.key.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.DozerBeanMapper;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.base.AbstractBaseService;
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
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by: Alexander Duckardt Date: 09.10.12
 * 
 * Depends on dozer, b/c audit log check requires dozer to be present, when checking if key management
 * was ever run or not.
 */
@Service("keyManagementService")
//@DependsOn(value={"dto2entityDeepDozerMapper", "dto2entityShallowDozerMapper"})
public class KeyManagementServiceImpl extends AbstractBaseService implements KeyManagementService, InitializingBean {
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
    
    @Autowired
    private HazelcastConfiguration hazelcastConfiguration;

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
    
    private JKSEntryListener jksListener = null;
    
    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() throws Exception {
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
        
        final IMap<String, byte[]> keyMap = hazelcastConfiguration.getMap("keyManagementCache");
        jksListener = new JKSEntryListener();
        keyMap.addEntryListener(jksListener, "jksFileKey", true);
        
        final File file = new File(jksFile);
        if(file.exists()) {
        	keyMap.put("jksFileKey", FileUtils.readFileToByteArray(file));
        } else {
        	final byte[] key = keyMap.get("jksFileKey");
        	jksListener.addOrUpdate(key);
        }
    }
    
    private boolean hasKeyManagementToolBeenRun() {
		return (userKeyDao.countAll().intValue() > 0);
	}
    
    private class JKSEntryListener implements EntryListener<String, byte[]> {
    	
    	void addOrUpdate(final byte[] value) {
    		log.warn("Received add or update JKS event");
    		if(value != null && value.length > 0) {
	    		try {
					final File file = new File(jksFile);
					boolean updateFile = false;
					if(!file.exists()) {
						file.createNewFile();
						log.warn("jks file does not exists - creating it due to cache event");
						updateFile = true;
					} else {
						final byte[] fileBytes = FileUtils.readFileToByteArray(file);
						if(!Arrays.equals(fileBytes, value)) {
							log.warn("jks file does not exists - creating it an updated jks file on another node");
							updateFile = true;
						}
					}
					if(updateFile) {
						FileUtils.writeByteArrayToFile(file, value);
					}
				} catch(Throwable e) {
					throw new RuntimeException("Could not create or update key", e);
				}
    		}
    	}

		@Override
		public void entryAdded(EntryEvent<String, byte[]> event) {
			log.warn("entryAdded() event called for jks key");
			addOrUpdate(event.getValue());
		}

		@Override
		public void entryUpdated(EntryEvent<String, byte[]> event) {
			log.warn("entryUpdated() event called for jks key");
			addOrUpdate(event.getValue());
		}
		

		@Override
		public void entryRemoved(EntryEvent<String, byte[]> event) {
			// TODO Auto-generated method stub
		}

		@Override
		public void entryEvicted(EntryEvent<String, byte[]> event) {
			// TODO Auto-generated method stub

		}
    	
    }

    @Override
    public byte[] getSystemUserKey(String keyName) throws EncryptionException {
        return getUserKey(systemUserId, keyName);
    }

    @Override
    public byte[] getUserKey(String userId, String keyName) throws EncryptionException {

        byte[] masterKey = new byte[0];
        try {
            masterKey = getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);

            if (masterKey == null || masterKey.length == 0) {
                throw new IllegalAccessException("Cannot get master key to decrypt user keys");
            }

            UserKey uk = userKeyDao.getByUserIdKeyName(userId, keyName);
            if (uk == null) {
                return null;
            }

            return jksManager.decodeKey(this.decrypt(masterKey, uk.getValue()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EncryptionException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void initKeyManagement() throws Exception{
        this.generateMasterKey();
        this.generateCookieKey();
        this.generateCommonKey();
        
        /* notifies other nodes that the JKS file has been created/modified */
        final IMap<String, byte[]> keyMap = hazelcastConfiguration.getMap("keyManagementCache");
        final byte[] fileTypes = FileUtils.readFileToByteArray(new File(jksFile));
        keyMap.put("jksFileKey", fileTypes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateMasterKey() throws Exception {
        log.warn("Start generating new master key...");

        log.warn("Loading user data ...");

        HashMap<String, UserSecurityWrapper> userSecurityMap = getSecurityMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        log.warn("Try to get old salt ...");
        byte[] oldMasterKey = this.getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        if (oldMasterKey != null && oldMasterKey.length > 0) {
            log.warn("OLD MASTER KEY IS: " + jksManager.encodeKey(oldMasterKey));
            log.warn("Decrypting user data ...");
            decryptData(oldMasterKey, userSecurityMap);
            log.warn("Decrypting user data finished successfully");
        } else {
            log.warn("OLD MASTER KEY IS NULL");
        }

        log.warn(" Generation of new master key ...");
//        jksManager.generateMasterKey(this.jksPassword.toCharArray(), this.keyPassword.toCharArray());
//
//        byte[] masterKey = this.getPrimaryKey(JksManager.KEYSTORE_ALIAS, this.keyPassword);
        byte[] masterKey = generateJKSKey(this.keyPassword, JksManager.KEYSTORE_ALIAS);
        if (masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }

        log.warn("NEW MASTER KEY IS: " + jksManager.encodeKey(masterKey));

        log.warn("Ecrypting user data ...");
        encryptData(masterKey, userSecurityMap, newUserKeyList);
        log.warn("Ecrypting user data finished successfully");

        log.warn("Refreshing user keys...");
        userKeyDao.deleteAll();
        addUserKeys(newUserKeyList);
        log.warn("End generating new master key...");
        
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
        List<LoginEntity> lgList = loginDAO.findUser(user.getId());
        List<UserKey> keyList = userKeyDao.getByUserId(user.getId());
        UserIdentityAnswerEntity example = new UserIdentityAnswerEntity();
        example.setUserId(user.getId());
        List<UserIdentityAnswerEntity> answersList = userIdentityAnswerDAO.getByExample(example);

        List<PasswordHistoryEntity> pwdList = new ArrayList<PasswordHistoryEntity>();
        for (LoginEntity lg : lgList) {
            pwdList.addAll(passwordHistoryDao.getPasswordHistoryByLoginId(lg.getId(), 0, Integer.MAX_VALUE));
        }
        HashMap<String, List<ManagedSysEntity>> managedSysMap = getManagedSysMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        UserSecurityWrapper usw = new UserSecurityWrapper();
        usw.setUserId(user.getId());
        usw.setLoginList(lgList);
        usw.setManagedSysList(managedSysMap.get(user.getId()));
        usw.setPasswordHistoryList(pwdList);
        usw.setUserKeyList(keyList);
        usw.setUserIdentityAnswerList(answersList);

        encryptUserData(masterKey, usw, newUserKeyList);
        // replace user key for given user
        userKeyDao.deleteByUserId(user.getId());
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
            dataKey = getUserKey(userId, KeyName.dataKey.name());
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
            dataKey = getUserKey(userId, KeyName.dataKey.name());
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
        byte[] dataKey = getUserKey(userId, keyName.name());
        if(dataKey!=null && dataKey.length>0){
            return encrypt(dataKey, data);
        }
        log.warn("Data Key is null. Skipping ecryption...");
        return null;
    }
    @Override
    public String encrypt(byte[] key, String data)throws Exception{
        return cryptor.encrypt(key, data);
    }
    @Override
    public String decrypt(String userId, KeyName keyName, String encryptedData)throws Exception{
        byte[] dataKey = getUserKey(userId, keyName.name());
        if(dataKey!=null && dataKey.length>0){
            return decrypt(dataKey, encryptedData);
        }
        log.warn("Data Key is null. Skipping decryption...");
        return null;
    }
    @Override
    public String decrypt(byte[] key, String encryptedData)throws Exception{
        return cryptor.decrypt(key, encryptedData);
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
        uk.setValue(this.encrypt(masterKey, jksManager.encodeKey(pwdKey)));
        uk.setName(KeyName.password.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setValue(this.encrypt(masterKey, jksManager.encodeKey(tokenKey)));
        uk.setName(KeyName.token.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setValue(this.encrypt(masterKey, jksManager.encodeKey(dataKey)));
        uk.setName(KeyName.dataKey.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setValue(this.encrypt(masterKey, jksManager.encodeKey(answerKey)));
        uk.setName(KeyName.challengeResponse.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        // log.warn("NEW USER KEYS ARE: [USER_ID: " + user.getUserId() +
        // "; PWD_KEY: " + jksManager.encodeKey(pwdKey) + "; TKN_KEY: "
        // + jksManager.encodeKey(tokenKey) + "]");

        // encrypt user data
        encryptUserPasswords(pwdKey, userSecurityWrapper);
        encryptUserChallengeResponses(answerKey, userSecurityWrapper);


        // log.warn("Printing ecrypted data...");
        // printUserData(user, pwdHistoryMap, managedSysMap);
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptUserChallengeResponses(byte[] key, UserSecurityWrapper userSecurityWrapper)throws Exception {
        log.warn("Encrypt user ChallengeResponses ...");
        if (userSecurityWrapper.getUserIdentityAnswerList() != null && !userSecurityWrapper.getUserIdentityAnswerList().isEmpty()) {
            for (UserIdentityAnswerEntity answer : userSecurityWrapper.getUserIdentityAnswerList()) {
                if(StringUtils.hasText(answer.getQuestionAnswer()))
                    answer.setQuestionAnswer(this.encrypt(key, answer.getQuestionAnswer()));
                    answer.setIsEncrypted(true);
                    userIdentityAnswerDAO.update(answer);
            }
        }
        log.warn("Encrypt user ChallengeResponses FINISHED...");
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptUserPasswords(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        log.warn("Encrypt user passwords ...");
        if (userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for (LoginEntity login : userSecurityWrapper.getLoginList()) {
                if(StringUtils.hasText(login.getPassword())){
                    login.setPassword(this.encrypt(key, login.getPassword()));
                    loginDAO.update(login);
                }
            }
        }
        log.warn("Encrypt user passwords history...");
        if (userSecurityWrapper.getPasswordHistoryList() != null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()) {
            for (PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                if(StringUtils.hasText(pwd.getPassword())){
                    pwd.setPassword(this.encrypt(key, pwd.getPassword()));
                    passwordHistoryDao.update(pwd);
                }
            }
        }

        if (userSecurityWrapper.getManagedSysList() != null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            log.warn("Encrypt manages sys...");
            for (ManagedSysEntity ms : userSecurityWrapper.getManagedSysList()) {
                if (ms.getPswd() != null) {
                    ms.setPswd(this.encrypt(key, ms.getPswd()));
                }
                managedSysDAO.save(ms);
            }
        }
        log.warn("Encrypt user passwords FINISHED...");
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
        log.warn("Decrypting user data ...");
        for (UserKey uk : userSecurityWrapper.getUserKeyList()) {
            byte[] key = jksManager.decodeKey(this.decrypt(masterKey, uk.getValue()));
            if (KeyName.password.name().equals(uk.getName())) {
                decryptUserPasswords(key, userSecurityWrapper);
            } else if (KeyName.challengeResponse.name().equals(uk.getName())) {
                decryptUserChallengeResponse(key, userSecurityWrapper);
            }
        }
        log.warn("Decrypting user data FINISHED...");
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptUserChallengeResponse(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        log.warn("Decrypting user ChallengeResponses ...");
        if (userSecurityWrapper.getUserIdentityAnswerList() != null && !userSecurityWrapper.getUserIdentityAnswerList().isEmpty()) {
            for (UserIdentityAnswerEntity answer : userSecurityWrapper.getUserIdentityAnswerList()) {
                if(answer.getIsEncrypted()){
                    if(StringUtils.hasText(answer.getQuestionAnswer()))
                        answer.setQuestionAnswer(this.decrypt(key, answer.getQuestionAnswer()));
                }
            }
        }
        log.warn("Decrypting user ChallengeResponses FINISHED...");
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptUserPasswords(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception {
        log.warn("Decrypting user passwords ...");
        if (userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for (LoginEntity login : userSecurityWrapper.getLoginList()) {
                if(StringUtils.hasText(login.getPassword()))
                    login.setPassword(this.decrypt(key, login.getPassword()));
            }
        }
        if (userSecurityWrapper.getPasswordHistoryList() != null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()) {
            log.warn("Decrypting user passwords history ...");
            for (PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                if(StringUtils.hasText(pwd.getPassword()))
                    pwd.setPassword(this.decrypt(key, pwd.getPassword()));
            }
        }

        if (userSecurityWrapper.getManagedSysList() != null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            log.warn("Decrypting manages sys ...");
            for (ManagedSysEntity ms : userSecurityWrapper.getManagedSysList()) {
                if (ms.getPswd() != null) {
                    ms.setPswd(this.decrypt(key, ms.getPswd()));
                }
            }
        }
        log.warn("Decrypting user passwords FINISHED...");
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
        log.warn(msg.toString());
        msg.setLength(0);

        msg.append("PWD HISTORY LIST FOR USER_ID(" + user.getId() + ") [\n");
        if (pwdHistoryMap.containsKey(user.getId())) {
            for (PasswordHistory pwd : pwdHistoryMap.get(user.getId())) {
                msg.append("\tpassword:" + pwd.getPassword() + "\n");
            }
        }
        msg.append("]");
        log.warn(msg.toString());
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
        log.warn(msg.toString());
        msg.setLength(0);
    }

    @Transactional
    private void addUserKeys(List<UserKey> newUserKeyList) {
        if (newUserKeyList != null && !newUserKeyList.isEmpty()) {
            for (UserKey uk : newUserKeyList) {
                userKeyDao.save(uk);
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
                    if (!pwdMap.containsKey(ph.getLogin().getId())) {
                        pwdMap.put(ph.getLogin().getId(), new ArrayList<PasswordHistoryEntity>());
                    }
                    pwdMap.get(ph.getLogin().getId()).add(ph);
                }

                // map to userId
                for (String userId : loginMap.keySet()) {
                    for (LoginEntity lg : loginMap.get(userId)) {
                        if (pwdMap.containsKey(lg.getId())) {
                            if (!pwdHistoryMap.containsKey(userId)) {
                                pwdHistoryMap.put(userId, new ArrayList<PasswordHistoryEntity>());
                            }
                            pwdHistoryMap.get(userId).addAll(pwdMap.get(lg.getId()));
                        }
                    }
                }
            }
        }
        return pwdHistoryMap;
    }

	@Override
	public void afterPropertiesSet() throws Exception {

        /* we're in Spring 4 world, so any initialization needs to be wrapped in a transaction */
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
		        if(!hasKeyManagementToolBeenRun()) {
		        	try {
						initKeyManagement();
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
		        } else {
		        	log.warn("Key management was already setup.  Doing nothing.  This message is normal; it will show on every server startup");
		        }
		        return null;
            }
        });
	}
}
