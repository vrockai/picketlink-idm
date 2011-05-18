/*
 * (C) Copyright 2007 Hewlett-Packard Development Company, LP
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For more information: www.smartfrog.org
 */
package org.jboss.jbossidmservlet.processor;

import java.util.logging.Level;
import org.picketlink.idm.api.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jboss.jbossidmservlet.bean.GroupBean;
import org.jboss.jbossidmservlet.bean.UserBean;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.IdentityType;
import org.picketlink.idm.api.Role;
import org.picketlink.idm.api.RoleManager;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityConfigurationException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.SimpleAttribute;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.spi.model.IdentityObject;

/**
 *
 * @author vrockai
 */
public class IdmProcessor {

    private IdentitySessionFactory identitySessionFactory = null;
    private IdentitySession identitySession = null;
    private String GROUP = "GROUP";

    public IdmProcessor() throws IdentityConfigurationException, IdentityException {
        identitySessionFactory = new IdentityConfigurationImpl().configure("picketlink-config.xml").buildIdentitySessionFactory();
        init();
    }

    private void init() throws IdentityException {
        identitySession = identitySessionFactory.createIdentitySession("idm_realm");
    }

    public void initializeDB() {
        try {
            identitySession.beginTransaction();
            identitySession.getPersistenceManager().createUser("John Doe");
            User usr1 = identitySession.getPersistenceManager().createUser("Viliam Rockai");
            identitySession.getPersistenceManager().createUser("Prabhat Jha");
            identitySession.getPersistenceManager().createGroup("grupa1", GROUP);
            Group grp1 = identitySession.getPersistenceManager().createGroup("grupa2", GROUP);
            identitySession.getPersistenceManager().createGroup("pogrup", GROUP);
            identitySession.getRelationshipManager().associateUser(grp1, usr1);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).warn(ex);
        }

    }

    public Collection<Group> getAssignedGroups(String username) {
        Collection<Group> groups = new ArrayList<Group>();
        try {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().findUser(username);
            groups = identitySession.getRelationshipManager().findAssociatedGroups(user);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
        return groups;
    }

    public Collection<UserBean> getAllUsers() {

        Collection<UserBean> userBeanList = new ArrayList<UserBean>();
        Collection<User> users = new ArrayList<User>();
        try {
            identitySession.beginTransaction();
            users = identitySession.getPersistenceManager().findUser((IdentitySearchCriteria) null);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        for (User u : users) {
            UserBean ub = new UserBean(u);
            ub.setAttributes(getAttributes(u));
            userBeanList.add(ub);
            ub.setAssociatedGroups(getAssignedGroups(u.getId()));
        }

        return userBeanList;

    }

    private Collection<Attribute> getAttributes(IdentityType it) {
        Collection<Attribute> attList = new ArrayList<Attribute>();

        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();

            Map<String, Attribute> attributes = attManager.getAttributes(it);
            attList = attributes.values();

        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return attList;
    }

    public Collection<GroupBean> getAllGroups() {

        Collection<GroupBean> groups = new ArrayList<GroupBean>();
        try {

            identitySession.beginTransaction();
            Collection<Group> groupList = identitySession.getPersistenceManager().findGroup(GROUP, (IdentitySearchCriteria) null);

            for(Group g: groupList){
                GroupBean gb = new GroupBean(g);

                Collection<Role> rc = new ArrayList<Role>();
                try {
                    for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                        rc.addAll(identitySession.getRoleManager().findRoles(g, rt));
                    }
                } catch (FeatureNotSupportedException ex) {
                    java.util.logging.Logger.getLogger(IdmProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }

                gb.setRoleList(rc);
                groups.add(gb);
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
        return groups;
    }

    public User createUser(String username) {
        User user = null;
        try {
            identitySession.beginTransaction();
            user = identitySession.getPersistenceManager().createUser(username);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return user;
    }

    public Group createGroup(String groupname) {
        Group group = null;
        try {
            identitySession.beginTransaction();
            group = identitySession.getPersistenceManager().createGroup(groupname, GROUP);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return group;
    }

    public void deleteUser(String username) {

        try {
            identitySession.beginTransaction();
            identitySession.getPersistenceManager().removeUser(username, true);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void deleteGroup(String groupName) {

        try {
            identitySession.beginTransaction();

            Group group = identitySession.getPersistenceManager().findGroup(groupName, GROUP);

            identitySession.getPersistenceManager().removeGroup(group, true);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public User renameUser(String username, String newusername) {
        User user = null;
        try {
            identitySession.beginTransaction();
            user = identitySession.getPersistenceManager().findUser(username);
            IdentityObject io = (IdentityObject) user;

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return user;
    }

    public void associateUser(String userId, String groupId) {

        try {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().findUser(userId);
            Group group = identitySession.getPersistenceManager().findGroup(groupId, GROUP);
            if (!identitySession.getRelationshipManager().isAssociated(group, user)) {
                identitySession.getRelationshipManager().associateUser(group, user);
            }
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void deassociateUser(String userId, String groupId) {
        try {
            identitySession.beginTransaction();

            User user = identitySession.getPersistenceManager().findUser(userId);
            System.out.println(user.getId());
            Group group = identitySession.getPersistenceManager().findGroup(groupId, GROUP);
            System.out.println(groupId + "/" + group);

            identitySession.getRelationshipManager().disassociateUsers(Arrays.asList(group), Arrays.asList(user));
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void createAttribute(String userId, String attName, String attVal) {
        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();
            User user = identitySession.getPersistenceManager().findUser(userId);
            attManager.addAttribute(user, attName, attVal);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void renameAttribute(String userId, String attName, String attVal) {
        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();
            User user = identitySession.getPersistenceManager().findUser(userId);
            Attribute[] attribute = new Attribute[]{
                new SimpleAttribute(attName, attVal),};
            attManager.updateAttributes(user, attribute);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void deleteAttribute(String userId, String attName, String attVal) {
        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();
            User user = identitySession.getPersistenceManager().findUser(userId);
            String[] atts = {attName};
            attManager.removeAttributes(user, atts);
            identitySession.getTransaction().commit();
            identitySession.close();

        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public RoleType createRoletype(String name) {
        RoleType roletype = null;
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            roletype = roleManager.createRoleType(name);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
        return roletype;
    }

    public void deleteRoletype(String name) {
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            roleManager.removeRoleType(name);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public void associateRole(String roletype, String userId, String groupName) {
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();

            RoleType rt = identitySession.getRoleManager().getRoleType(roletype);
            User u = identitySession.getPersistenceManager().findUser(userId);
            Group g = identitySession.getPersistenceManager().findGroup(groupName, GROUP);
            roleManager.createRole(rt, u, g);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

    }

    public void deassociateRole(String roletype, String userId, String groupName) {
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            RoleType rt = identitySession.getRoleManager().getRoleType(roletype);
            User u = identitySession.getPersistenceManager().findUser(userId);
            Group g = identitySession.getPersistenceManager().findGroup(groupName, GROUP);
            roleManager.removeRole(rt, u, g);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }
    }

    public Collection<RoleType> getAllRoletypes() {
        Collection<RoleType> roletypeList = new ArrayList<RoleType>();
         try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            roletypeList = roleManager.findRoleTypes();
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return roletypeList;
    }

    public Collection<Role> getAllRoles(Group grp) {
         Collection<Role> roleList = new ArrayList<Role>();
         try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            roleList = roleManager.findRoles(GROUP, GROUP);
        } catch (IdentityException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        } catch (FeatureNotSupportedException ex) {
            Logger.getLogger(IdmProcessor.class.getName()).error(ex);
        }

        return roleList;
    }
}
