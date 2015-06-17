package org.openiam.authmanager.dao;

import java.util.List;

public interface AbstractDAO<T> {
	List<T> getList();
}
