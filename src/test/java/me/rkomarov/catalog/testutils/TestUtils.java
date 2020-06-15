package me.rkomarov.catalog.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
public class TestUtils {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @SuppressWarnings("SqlWithoutWhere")
    public void clearTables() {
        entityManager.createNativeQuery("DELETE FROM product").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM section").executeUpdate();
    }

    @Transactional
    public void flushClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
