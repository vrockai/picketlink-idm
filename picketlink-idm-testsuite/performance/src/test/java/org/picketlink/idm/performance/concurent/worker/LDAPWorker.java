/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package org.picketlink.idm.performance.concurent.worker;

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Test;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;

import org.jboss.portal.test.framework.embedded.DSConfig;
import org.jboss.unit.api.pojo.annotations.Parameter;

import org.picketlink.idm.ldap.cases.LDAPIdentityStoreTests;
import org.picketlink.idm.test.support.opends.OpenDSService;
/**
*
* @author vrockai
*/
public class LDAPWorker extends PerformanceWorker {
LDAPIdentityStoreTests LDAPtests;

    public LDAPWorker(LDAPIdentityStoreTests LDAPtests, int op_number, TestMethod testMethod) {
        super(op_number);
        this.testType = testMethod;
        this.LDAPtests = LDAPtests;
    }

    public enum TestMethod {

        CREATE_GPR, CREATE_USR, CREATE_CRED, CRITERIA, FIND_IDENTITY, RELATIONSHIPS, REMOVE_GRP, REMOVE_USR
    }

    protected TestMethod testType;

    public LDAPWorker(IdentitySessionFactory identitySessionFactory) {
        super(identitySessionFactory);
    }

    public LDAPWorker(IdentitySessionFactory identitySessionFactory, int size) {
        super(identitySessionFactory, size);
    }

    public LDAPWorker(IdentitySessionFactory identitySessionFactory, int size, TestMethod testType) {
        super(identitySessionFactory, size);
        this.testType = testType;
    }

    @Override
    public void run() {

        Collection<Exception> exList = new ArrayList<Exception>();

        try {

            timeStart = System.currentTimeMillis();

             switch (testType) {
                case CREATE_GPR:
                    LDAPtests.testCreateGroup(USER_NUM, worker_id);

                    break;
                case CREATE_USR:
                     LDAPtests.testCreateUser(USER_NUM, worker_id);
                    break;
                case CREATE_CRED:
                     LDAPtests.testCredentials(USER_NUM, worker_id);
                    break;
                case CRITERIA:
                     LDAPtests.testCriteria(USER_NUM, worker_id);
                    break;
                case FIND_IDENTITY:
                     LDAPtests.testFindIdentityObject(USER_NUM, worker_id);
                    break;
                case RELATIONSHIPS:
                    LDAPtests.testRelationships(USER_NUM, worker_id);
                    break;
                case REMOVE_GRP:
                     LDAPtests.testRemoveGroup(USER_NUM, worker_id);
                    break;
                case REMOVE_USR:
                     LDAPtests.testRemoveUser(USER_NUM, worker_id);
                    break;

                default:
                    break;
            }

            timeEnd = System.currentTimeMillis();
        } catch (Exception ex) {
            exList.add(ex);
        }

        if (exList.size() > 0) {
            this.eList = exList;
        }
    }

}
