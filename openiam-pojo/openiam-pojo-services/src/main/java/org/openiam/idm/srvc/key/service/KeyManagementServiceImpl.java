package org.openiam.idm.srvc.key.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
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
    @Transactional
    public void generateMasterKey() throws Exception {
        log.warn("Start generating new master key...");

        log.warn("Loading user data ...");
        List<User> userList = userDAO.getAllWithSecurityInfo();
        HashMap<String, List<PasswordHistory>> pwdHistoryMap = getPasswordHistoryMap(userList);
        HashMap<String, List<ManagedSys>> managedSysMap = getManagedSysMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        byte[] oldMasterKey = this.getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(oldMasterKey != null && oldMasterKey.length > 0) {
            log.warn("OLD MASTER KEY IS: " + jksManager.encodeKey(oldMasterKey));
            log.warn("Decrypting user data ...");
            decryptData(oldMasterKey, userList, pwdHistoryMap, managedSysMap);
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
        encryptData(masterKey, userList, pwdHistoryMap, managedSysMap, newUserKeyList);

        log.warn("Refreshing user keys...");
        userKeyDao.deleteAll();
        addUserKeys(newUserKeyList);
        log.warn("End generating new master key...");
    }

    @Override
    @Transactional
    public Long generateUserKeys(String userId) throws Exception {
        User user = userDAO.getWithSecurityInfo(userId);
        if(user != null) {
            return generateUserKeys(user);
        }
        return 0L;
    }

    @Override
    @Transactional
    public Long generateUserKeys(User user) throws Exception {
        byte[] masterKey = getMasterKey(JksManager.KEYSTORE_ALIAS);
        if(masterKey == null || masterKey.length == 0) {
            throw new NullPointerException("Cannot get master key to encrypt user keys");
        }
        List<User> userList = new ArrayList<User>();
        userList.add(user);

        HashMap<String, List<PasswordHistory>> pwdHistoryMap = getPasswordHistoryMap(userList);
        HashMap<String, List<ManagedSys>> managedSysMap = getManagedSysMap();
        List<UserKey> newUserKeyList = new ArrayList<UserKey>();

        encryptUserData(user, masterKey, pwdHistoryMap, managedSysMap, newUserKeyList);
        // replace user key for given user
        userKeyDao.deleteByUserId(user.getUserId());
        addUserKeys(newUserKeyList);
        return 2L;
    }

    @Transactional
    private void encryptData(byte[] masterKey, List<User> userList, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap, List<UserKey> newUserKeyList) throws Exception {
        if(userList != null && !userList.isEmpty()) {
            for(User user : userList) {
                encryptUserData(user, masterKey, pwdHistoryMap, managedSysMap, newUserKeyList);
            }
        }
    }
    @Transactional
    private void encryptUserData(User user, byte[] masterKey, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap, List<UserKey> newUserKeyList) throws Exception {
        byte[] pwdKey = jksManager.getNewPrivateKey();
        byte[] tokenKey = jksManager.getNewPrivateKey();
        // create new USER KEYS
        UserKey uk = new UserKey();
        uk.setKey(cryptor.encrypt(masterKey, jksManager.encodeKey(pwdKey)));
        uk.setName(KeyName.password.name());
        uk.setUserId(user.getUserId());
        newUserKeyList.add(uk);

        uk = new UserKey();
        uk.setKey(cryptor.encrypt(masterKey, jksManager.encodeKey(tokenKey)));
        uk.setName(KeyName.token.name());
        uk.setUserId(user.getUserId());
        newUserKeyList.add(uk);

        log.warn("NEW USER KEYS ARE: [USER_ID: " + user.getUserId() + "; PWD_KEY: " + jksManager.encodeKey(pwdKey) + "; TKN_KEY: "
                 + jksManager.encodeKey(tokenKey) + "]");

        // encrypt user data
        if(user.getPrincipalList() != null && !user.getPrincipalList().isEmpty()) {
            Set<Login> loginSet = new HashSet<Login>(user.getPrincipalList());
            for(Login login : loginSet) {
                login.setPassword(cryptor.encrypt(pwdKey, login.getPassword()));
                loginDAO.update(login);
            }
        }

        if(pwdHistoryMap.containsKey(user.getUserId())) {
            for(PasswordHistory pwd : pwdHistoryMap.get(user.getUserId())) {
                pwd.setPassword(cryptor.encrypt(pwdKey, pwd.getPassword()));
                passwordHistoryDao.update(pwd);
            }
        }
        if(managedSysMap.containsKey(user.getUserId())) {
            for(ManagedSys ms : managedSysMap.get(user.getUserId())) {
                if(ms.getPswd() != null) {
                    ms.setPswd(cryptor.encrypt(pwdKey, ms.getPswd()));
                }
                managedSysDAO.update(ms);
            }
        }
        log.warn("Printing ecrypted data...");
        printUserData(user, pwdHistoryMap, managedSysMap);
    }

    @Transactional
    private void decryptData(byte[] masterKey, List<User> userList, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap)throws Exception  {
        if(userList != null && !userList.isEmpty()) {
            for(User user : userList) {
                decryptUserData(user, masterKey, pwdHistoryMap, managedSysMap);
            }
        }
    }
    @Transactional
    private void decryptUserData(User user, byte[] masterKey, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap)throws Exception  {
        if(user.getUserKeys() != null && !user.getUserKeys().isEmpty()) {
            // the keys exist, it is necessary to refresh them
            if(masterKey == null || masterKey.length == 0) {
                throw new NullPointerException("Cannot read master key for decrypting user keys.");
            }
            // try to decrypt each key
            String pwdKey = null;
            String tokenKey = null;
            for(UserKey uk : user.getUserKeys()) {
                String key = uk.getKey();
                if(KeyName.password.name().equals(uk.getName())) {
                    pwdKey = cryptor.decrypt(masterKey, key);
                } else if(KeyName.token.name().equals(uk.getName())) {
                    tokenKey = cryptor.decrypt(masterKey, key);
                }
            }
            log.warn("OLD USER KEYS ARE: [USER_ID: " + user.getUserId() + "; PWD_KEY: " + pwdKey + "; TKN_KEY: "
                     + tokenKey + "]");
            // decypt user data with keys

            if(user.getPrincipalList() != null && !user.getPrincipalList().isEmpty()) {
                Set<Login> loginSet = new HashSet<Login>(user.getPrincipalList());
                for(Login login : loginSet) {
                    login.setPassword(cryptor.decrypt(jksManager.decodeKey(pwdKey), login.getPassword()));
                }
                user.setPrincipalList(new ArrayList<Login>(loginSet));
            }

            if(pwdHistoryMap.containsKey(user.getUserId())) {
                for(PasswordHistory pwd : pwdHistoryMap.get(user.getUserId())) {
                    pwd.setPassword(cryptor.decrypt(jksManager.decodeKey(pwdKey), pwd.getPassword()));
                }
            }
            if(managedSysMap.containsKey(user.getUserId())) {
                for(ManagedSys ms : managedSysMap.get(user.getUserId())) {
                    if(ms.getPswd() != null) {
                        ms.setPswd(cryptor.decrypt(jksManager.decodeKey(pwdKey), ms.getPswd()));
                    }
                }
            }
            log.warn("Printing decrypted data...");
            printUserData(user, pwdHistoryMap, managedSysMap);
        }
    }

    private void printUserData(User user, HashMap<String, List<PasswordHistory>> pwdHistoryMap, HashMap<String, List<ManagedSys>> managedSysMap) {
        StringBuilder msg = new StringBuilder();
        msg.append("LOGIN LIST FOR USER_ID("+user.getUserId()+") [\n");
        if(user.getPrincipalList() != null && !user.getPrincipalList().isEmpty()) {
            for(Login login : user.getPrincipalList()) {
                msg.append("\t{login:"+login.getId().getLogin()+";password:"+login.getPassword()+"}\n");
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

    private HashMap<String, List<PasswordHistory>> getPasswordHistoryMap(List<User> userList) {
        HashMap<String, List<PasswordHistory>> pwdHistoryMap = new HashMap<String, List<PasswordHistory>>();
        if(userList != null && !userList.isEmpty()) {
            for(User usr : userList) {
                if(usr.getPrincipalList() != null && !usr.getPrincipalList().isEmpty()) {
                    for(Login login : usr.getPrincipalList()) {
                        List<PasswordHistory> historyList = passwordHistoryDao
                                .findAllPasswordHistoryByPrincipal(login.getId().getDomainId(),
                                                                   login.getId().getLogin(),
                                                                   login.getId().getManagedSysId());
                        if(historyList != null && !historyList.isEmpty()) {
                            if(!pwdHistoryMap.containsKey(usr.getUserId())) {
                                pwdHistoryMap.put(usr.getUserId(), new ArrayList<PasswordHistory>());
                            }
                            pwdHistoryMap.get(usr.getUserId()).addAll(historyList);
                        }
                    }
                }
            }
        }
        return pwdHistoryMap;
    }
}
