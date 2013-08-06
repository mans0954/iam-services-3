package org.openiam.connector.ldap.dirtype;

/**
 * Instantiate objects that directory/brand specific implementations.
 */
public class DirectorySpecificImplFactory {

public static Directory create( String dirType) {

    if (dirType == null) {
        // default
        return (new LdapV3());
    }

    if (dirType.equalsIgnoreCase(Directory.ACTIVE_DIRECTORY)) {
        return (new ActiveDirectoryImpl());
    }
    // default to the generic ldap adapter
    return (new LdapV3());

	}


}
