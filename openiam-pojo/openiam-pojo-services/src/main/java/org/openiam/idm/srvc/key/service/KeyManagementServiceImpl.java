package org.openiam.idm.srvc.key.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.dto.UserSecurityWrapper;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
//@Service
public class KeyManagementServiceImpl implements KeyManagementService {
    protected final Log log = LogFactory.getLog(this.getClass());
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final int FETCH_COUNT=1000;
    private String             jksFile;
    private String             jksPassword;
    private String             keyPassword;
    private Integer            iterationCount;
    private JksManager         jksManager;
    private Cryptor            cryptor;
    @Autowired
    private UserDAO            userDAO;
    @Autowired
    private LoginDAO           loginDAO;
    @Autowired
    private UserKeyDao         userKeyDao;
    @Autowired
    private PasswordHistoryDAO passwordHistoryDao;
    @Autowired
    private ManagedSysDAO      managedSysDAO;


    @PostConstruct
    public void init() {
        if(!StringUtils.hasText(this.jksFile)) {
            this.jksFile = JksManager.KEYSTORE_DEFAULT_LOCATION + JksManager.KEYSTORE_FILE_NAME;
        }
        if(!StringUtils.hasText(this.jksPassword)) {
            this.jksPassword = JksManager.KEYSTORE_DEFAULT_PASSWORD;
        }
        if(!StringUtils.hasText(this.keyPassword)) {
            throw new NullPointerException(
                    "The password for master key is required. This property cannot be read from properties files");
        }
        if(this.iterationCount == null) {
            jksManager = new JksManager(this.jksFile);
        } else {
            jksManager = new JksManager(this.jksFile, this.iterationCount);
        }
    }

    public void setJksFile(String jksFile) {
        this.jksFile = jksFile;
    }

    public void setJksPassword(String jksPassword) {
        this.jksPassword = jksPassword;
    }

    public void setIterationCount(Integer iterationCount) {
        this.iterationCount = iterationCount;
    }

    @Required
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public void setCryptor(Cryptor cryptor) {
        this.cryptor = cryptor;
    }

    @Override
    public byte[] getUserKey(String userId, String keyName) throws EncryptionException {
        byte[] masterKey = new byte[0];
        try {
            masterKey = getMasterKey(JksManager.KEYSTORE_ALIAS);

            if(masterKey == null || masterKey.length == 0) {
                throw new IllegalAccessException("Cannot get master key to decrypt user keys");
            }

            UserKey uk = userKeyDao.getByUserIdKeyName(userId, keyName);
            if(uk == null) {
                return null;
            }

            return jksManager.decodeKey(cryptor.decrypt(masterKey, uk.getKey()));
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw new EncryptionException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateMasterKey() throws Exception {
        log.warn("Start generating new master key...");

        log.warn("Loading user data ...");

        HashMap<String, UserSecurityWrapper> userSecurityMap = getSecurityMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        log.warn("Try to get old salt ...");
        byte[] oldMasterKey = this.getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(oldMasterKey != null && oldMasterKey.length > 0) {
            log.warn("OLD MASTER KEY IS: " + jksManager.encodeKey(oldMasterKey));
            log.warn("Decrypting user data ...");
            decryptData(oldMasterKey, userSecurityMap);
            log.warn("Decrypting user data finished successfully");
        } else {
            log.warn("OLD MASTER KEY IS NULL");
        }


        log.warn(" Generation of new master key ...");
        jksManager.generatePrimaryKey(this.jksPassword.toCharArray(), this.keyPassword.toCharArray());

        byte[] masterKey = this.getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(masterKey == null || masterKey.length == 0) {
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
        if(userEntity != null) {
            return generateUserKeys(userEntity);
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateUserKeys(UserEntity user) throws Exception {
        byte[] masterKey = getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }
        List<UserEntity> userList = new ArrayList<UserEntity>();
        userList.add(user);
        List<LoginEntity> lgList = loginDAO.findUser(user.getUserId());
        List<UserKey> keyList = userKeyDao.getByUserId(user.getUserId());
        List<PasswordHistoryEntity> pwdList = new ArrayList<PasswordHistoryEntity>();
        for (LoginEntity lg: lgList){
            pwdList.addAll(passwordHistoryDao.getPasswordHistoryByLoginId(lg.getLoginId(), 0, Integer.MAX_VALUE));
        }
        HashMap<String, List<ManagedSys>> managedSysMap = getManagedSysMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();



        UserSecurityWrapper usw = new UserSecurityWrapper();
        usw.setUserId(user.getUserId());
        usw.setLoginList(lgList);
        usw.setManagedSysList(managedSysMap.get(user.getUserId()));
        usw.setPasswordHistoryList(pwdList);
        usw.setUserKeyList(keyList);

        encryptUserData(masterKey, usw, newUserKeyList);
        // replace user key for given user
        userKeyDao.deleteByUserId(user.getUserId());
        addUserKeys(newUserKeyList);
        return 2L;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void migrateData(String oldSecretKey)throws Exception{

        HashMap<String, UserSecurityWrapper> userSecurityMap = getSecurityMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        jksManager.generatePrimaryKey(this.jksPassword.toCharArray(), this.keyPassword.toCharArray());

        byte[] masterKey = this.getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot generate master key to encrypt user keys");
        }

        if(userSecurityMap!=null && !userSecurityMap.isEmpty()){
            for(String userId : userSecurityMap.keySet()){
                // decrypt user data
                if(!"0001".equals(userId)) {
                    decryptSecurityDataForUser(jksManager.decodeKey(oldSecretKey), userSecurityMap.get(userId));
                }
                // reencrypt user data
                encryptUserData(masterKey, userSecurityMap.get(userId), newUserKeyList);

            }
        }
        // replace user key for given user
        userKeyDao.deleteAll();
        addUserKeys(newUserKeyList);
    }

    @Transactional(rollbackFor = Exception.class)
    private void encryptData(byte[] masterKey, HashMap<String, UserSecurityWrapper> userSecurityMap, List<UserKey> newUserKeyList) throws Exception {
        if(userSecurityMap != null && !userSecurityMap.isEmpty()) {
            for(String userId : userSecurityMap.keySet()) {
                encryptUserData(masterKey, userSecurityMap.get(userId), newUserKeyList);
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void encryptUserData(byte[] masterKey, UserSecurityWrapper userSecurityWrapper, List<UserKey> newUserKeyList) throws Exception {
        byte[] pwdKey = jksManager.getNewPrivateKey();
        byte[] tokenKey = jksManager.getNewPrivateKey();
        // create new USER KEYS
        UserKey uk = new UserKey();
        uk.setKey(cryptor.encrypt(masterKey, jksManager.encodeKey(pwdKey)));
        uk.setName(KeyName.password.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setKey(cryptor.encrypt(masterKey, jksManager.encodeKey(tokenKey)));
        uk.setName(KeyName.token.name());
        uk.setUserId(userSecurityWrapper.getUserId());
        newUserKeyList.add(uk);

//        log.warn("NEW USER KEYS ARE: [USER_ID: " + user.getUserId() + "; PWD_KEY: " + jksManager.encodeKey(pwdKey) + "; TKN_KEY: "
//                 + jksManager.encodeKey(tokenKey) + "]");

        // encrypt user data
        log.warn("Encrypt user passwords ...");
        if(userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for(LoginEntity login : userSecurityWrapper.getLoginList()) {
                login.setPassword(cryptor.encrypt(pwdKey, login.getPassword()));
                loginDAO.update(login);
            }
        }
        log.warn("Encrypt user passwords history...");
        if(userSecurityWrapper.getPasswordHistoryList() != null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()) {
            for(PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                pwd.setPassword(cryptor.encrypt(pwdKey, pwd.getPassword()));
                passwordHistoryDao.update(pwd);
            }
        }

        if(userSecurityWrapper.getManagedSysList() != null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            log.warn("Encrypt manages sys...");
            for(ManagedSys ms : userSecurityWrapper.getManagedSysList()) {
                if(ms.getPswd() != null) {
                    ms.setPswd(cryptor.encrypt(pwdKey, ms.getPswd()));
                }
                managedSysDAO.update(ms);
            }
        }
        log.warn("Encrypt user data FINISHED...");
//        log.warn("Printing ecrypted data...");
//        printUserData(user, pwdHistoryMap, managedSysMap);
    }

    @Transactional(rollbackFor = Exception.class)
    private void decryptData(byte[] masterKey, HashMap<String, UserSecurityWrapper> userSecurityMap)throws Exception  {
        if(userSecurityMap != null && !userSecurityMap.isEmpty()) {
            for(String userId : userSecurityMap.keySet()) {
                decryptUserData(masterKey, userSecurityMap.get(userId));
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void decryptUserData(byte[] masterKey, UserSecurityWrapper userSecurityWrapper)throws Exception  {
        if(userSecurityWrapper.getUserKeyList() != null && !userSecurityWrapper.getUserKeyList().isEmpty()) {
            // the keys exist, it is necessary to refresh them
            if(masterKey == null || masterKey.length == 0) {
                throw new NullPointerException("Cannot read master key for decrypting user keys.");
            }
            // try to decrypt each key
            String pwdKey = null;
            String tokenKey = null;
            for(UserKey uk : userSecurityWrapper.getUserKeyList()) {
                String key = uk.getKey();
                if(KeyName.password.name().equals(uk.getName())) {
                    pwdKey = cryptor.decrypt(masterKey, key);
                } else if(KeyName.token.name().equals(uk.getName())) {
                    tokenKey = cryptor.decrypt(masterKey, key);
                }
            }
            log.warn("OLD USER KEYS ARE: [USER_ID: " + userSecurityWrapper.getUserId() + "; PWD_KEY: " + pwdKey + "; TKN_KEY: "
                     + tokenKey + "]");
            // decypt user data with keys
            decryptSecurityDataForUser(jksManager.decodeKey(pwdKey), userSecurityWrapper);


//            log.warn("Printing decrypted data...");
//            printUserData(user, pwdHistoryMap, managedSysMap);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void decryptSecurityDataForUser(byte[] key, UserSecurityWrapper userSecurityWrapper) throws Exception{
        log.warn("Decrypting user passwords ...");
        if(userSecurityWrapper.getLoginList() != null && !userSecurityWrapper.getLoginList().isEmpty()) {
            for(LoginEntity login : userSecurityWrapper.getLoginList()) {
                login.setPassword(cryptor.decrypt(key, login.getPassword()));
            }
        }
        if(userSecurityWrapper.getPasswordHistoryList()!=null && !userSecurityWrapper.getPasswordHistoryList().isEmpty()){
            log.warn("Decrypting user passwords history ...");
            for(PasswordHistoryEntity pwd : userSecurityWrapper.getPasswordHistoryList()) {
                pwd.setPassword(cryptor.decrypt(key, pwd.getPassword()));
            }
        }

        if(userSecurityWrapper.getManagedSysList()!=null && !userSecurityWrapper.getManagedSysList().isEmpty()) {
            log.warn("Decrypting manages sys ...");
            for(ManagedSys ms : userSecurityWrapper.getManagedSysList()) {
                if(ms.getPswd() != null) {
                    ms.setPswd(cryptor.decrypt(key, ms.getPswd()));
                }
            }
        }
        log.warn("Decrypting user data FINISHED...");
    }

    private void printUserData(User user, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap) {
        StringBuilder msg = new StringBuilder();
        msg.append("LOGIN LIST FOR USER_ID("+user.getUserId()+") [\n");
        if(user.getPrincipalList() != null && !user.getPrincipalList().isEmpty()) {
            for(Login login : user.getPrincipalList()) {
                msg.append("\t{login:"+login.getLogin()+";password:"+login.getPassword()+"}\n");
            }
        }
        msg.append("]");
        log.warn(msg.toString());
        msg.setLength(0);

        msg.append("PWD HISTORY LIST FOR USER_ID("+user.getUserId()+") [\n");
        if(pwdHistoryMap.containsKey(user.getUserId())) {
            for(PasswordHistory pwd : pwdHistoryMap.get(user.getUserId())) {
                msg.append("\tpassword:"+pwd.getPassword()+"\n");
            }
        }
        msg.append("]");
        log.warn(msg.toString());
        msg.setLength(0);

        msg.append("MANAGED SYS LIST FOR USER_ID("+user.getUserId()+") [\n");
        if(managedSysMap.containsKey(user.getUserId())) {
            for(ManagedSys ms : managedSysMap.get(user.getUserId())) {
                if(ms.getPswd() != null) {
                    msg.append("\tpassword:"+ms.getPswd()+"\n");
                } else{
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
        if(newUserKeyList!=null && !newUserKeyList.isEmpty()){
            for(UserKey uk : newUserKeyList){
                userKeyDao.save(uk);
            }
        }
    }

    private byte[] getMasterKey(String alias) throws Exception {
        return jksManager.getPrimaryKeyFromJKS(alias, jksPassword.toCharArray(), keyPassword.toCharArray());
    }


    private HashMap<String,UserSecurityWrapper> getSecurityMap() throws Exception {
        HashMap<String,UserSecurityWrapper> result= new HashMap<String, UserSecurityWrapper>();

        Long userCount = userDAO.getUserCount();
        if(userCount!=null && userCount>0){
            long fetchedDataCount = 0l;
            Set<String> userIdList = new HashSet<String>();
            int pageNum=0;
            do{
                userIdList.addAll(userDAO.getUserIdList(pageNum*FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long)userIdList.size();
                pageNum++;
            }while(fetchedDataCount<userCount);

            HashMap<String, List<ManagedSys>> managedSysMap = getManagedSysMap();
            HashMap<String, List<LoginEntity>> loginMap = getLoginMap();
            HashMap<String, List<UserKey>> userKeyMap = getUserKeyMap();
            HashMap<String, List<PasswordHistoryEntity>> pwdHistoryMap = getPasswordHistoryMap(loginMap);

            for (String userId : userIdList){
                UserSecurityWrapper usw = new UserSecurityWrapper();
                usw.setUserId(userId);
                usw.setLoginList(loginMap.get(userId));
                usw.setManagedSysList(managedSysMap.get(userId));
                usw.setPasswordHistoryList(pwdHistoryMap.get(userId));
                usw.setUserKeyList(userKeyMap.get(userId));

                result.put(userId, usw);
            }
        }
        return result;
    }

    private HashMap<String, List<UserKey>> getUserKeyMap() throws Exception {
        HashMap<String, List<UserKey>> keyMap = new HashMap<String, List<UserKey>>();

        Long keyCount = userKeyDao.countAll();
        if(keyCount!=null && keyCount>0){
            long fetchedDataCount = 0l;
            Set<UserKey> keyList = new HashSet<UserKey>();
            int pageNum=0;
            do{
                keyList.addAll(userKeyDao.getSublist(pageNum * FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long)keyList.size();
                pageNum++;
            }while(fetchedDataCount<keyCount);

            for(UserKey key : keyList) {
                if(!keyMap.containsKey(key.getUserId())) {
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
        if(lgCount!=null && lgCount>0){
            long fetchedDataCount = 0l;
            Set<LoginEntity> loginList = new HashSet<LoginEntity>();
            int pageNum=0;
            do{
                loginList.addAll(loginDAO.getLoginSublist(pageNum*FETCH_COUNT, FETCH_COUNT));
                fetchedDataCount = (long)loginList.size();
                pageNum++;
            }while(fetchedDataCount<lgCount);

            for(LoginEntity lg : loginList) {
                if(!lgMap.containsKey(lg.getUserId())) {
                    lgMap.put(lg.getUserId(), new ArrayList<LoginEntity>());
                }
                lgMap.get(lg.getUserId()).add(lg);
            }
        }
        return lgMap;
    }

    private HashMap<String, List<ManagedSys>> getManagedSysMap() {
        HashMap<String, List<ManagedSys>> managedSysMap = new HashMap<String, List<ManagedSys>>();
        List<ManagedSys> mngSysList = managedSysDAO.findAllManagedSys();
        if(mngSysList != null && !mngSysList.isEmpty()) {
            for(ManagedSys ms : mngSysList) {
                if(!managedSysMap.containsKey(ms.getUserId())) {
                    managedSysMap.put(ms.getUserId(), new ArrayList<ManagedSys>());
                }
                managedSysMap.get(ms.getUserId()).add(ms);
            }
        }
        return managedSysMap;
    }

    private HashMap<String, List<PasswordHistoryEntity>> getPasswordHistoryMap(final HashMap<String, List<LoginEntity>> loginMap) {
        final HashMap<String, List<PasswordHistoryEntity>> pwdHistoryMap = new HashMap<String, List<PasswordHistoryEntity>>();

        if(loginMap != null && !loginMap.isEmpty()) {

            final Long pwdCount = passwordHistoryDao.getCount();
            if(pwdCount!=null && pwdCount>0){
                long fetchedDataCount = 0l;
                final Set<PasswordHistoryEntity> pwdList = new HashSet<PasswordHistoryEntity>();
                final HashMap<String, List<PasswordHistoryEntity>> pwdMap = new HashMap<String, List<PasswordHistoryEntity>>();

                int pageNum=0;
                do{
                    pwdList.addAll(passwordHistoryDao.getSublist(pageNum * FETCH_COUNT, FETCH_COUNT));
                    fetchedDataCount = (long)pwdList .size();
                    pageNum++;
                }while(fetchedDataCount<pwdCount);

                for(final PasswordHistoryEntity ph : pwdList) {
                    if(!pwdMap.containsKey(ph.getLoginId())) {
                        pwdMap.put(ph.getLoginId(), new ArrayList<PasswordHistoryEntity>());
                    }
                    pwdMap.get(ph.getLoginId()).add(ph);
                }

                // map to userId
                for (String userId:loginMap.keySet()){
                    for(LoginEntity lg: loginMap.get(userId)){
                        if(pwdMap.containsKey(lg.getLoginId())){
                            if(!pwdHistoryMap.containsKey(userId)) {
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
}
