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
package org.jboss.picketlink.idm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jboss.jbossidmservlet.bean.GroupBean;
import org.jboss.jbossidmservlet.bean.UserBean;
import org.jboss.jbossidmservlet.processor.IdmProcessor;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityConfigurationException;
import org.picketlink.idm.common.exception.IdentityException;

/**
 *
 * @author vrockai
 */
public class IdmServlet extends HttpServlet {

    private static final long serialVersionUID = 33141L;
    //IdmProcessor idmProc;

    @Override
    public void init() throws ServletException {
        try {
            IdmProcessor idmProc = new IdmProcessor();
            idmProc.initializeDB();
        } catch (IdentityConfigurationException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        } catch (IdentityException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        }
    }

    private IdmProcessor getIdmProcessor(HttpServletRequest request) {
        IdmProcessor idmProc = null;

        idmProc = (IdmProcessor) request.getAttribute("idmProc");
        if (idmProc == null) {
            try {
                idmProc = new IdmProcessor();
                request.setAttribute("idmProc", idmProc);
            } catch (IdentityConfigurationException ex) {
                java.util.logging.Logger.getLogger(IdmServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IdentityException ex) {
                java.util.logging.Logger.getLogger(IdmServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return idmProc;
    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        IdmProcessor idmProc = getIdmProcessor(request);

        Collection<UserBean> userList = idmProc.getAllUsers();
        Collection<GroupBean> groupList = idmProc.getAllGroups();
        Collection<RoleType> roletypeList = idmProc.getAllRoletypes();

        request.getSession().setAttribute("userList", userList);
        request.getSession().setAttribute("groupList", groupList);

        request.getSession().setAttribute("roletypeList", roletypeList);
        request.getRequestDispatcher("main.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String method = request.getServletPath().substring(1);
        try {
            Method postMethod = getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
            postMethod.invoke(this, request, response);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        } catch (SecurityException ex) {
            Logger.getLogger(IdmServlet.class.getName()).error(ex);
        }

        doGet(request, response);
    }

    public Group createGroup(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String groupName = request.getParameter("groupId");
        Group newGroup = idmProc.createGroup(groupName);
        return newGroup;
    }

    public User createUser(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String userId = request.getParameter("userId");
        User user = idmProc.createUser(userId);
        return user;
    }

    public void deleteGroup(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String groupName = request.getParameter("groupId");
        idmProc.deleteGroup(groupName);

    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String userId = request.getParameter("userId");
        idmProc.deleteUser(userId);
    }

    public void assignGroup(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        idmProc.associateUser(userId, groupId);
    }

    public void deassignGroup(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        idmProc.deassociateUser(userId, groupId);
    }

    public void createAttribute(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String userId = request.getParameter("userId");
        String attName = request.getParameter("attName");
        String attVal = request.getParameter("attVal");
        idmProc.createAttribute(userId, attName, attVal);
    }

    public void deleteAttribute(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String userId = request.getParameter("userId");
        String attName = request.getParameter("attName");
        String attVal = request.getParameter("attVal");
        idmProc.deleteAttribute(userId, attName, attVal);
    }

    public void renameAttribute(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String userId = request.getParameter("userId");
        String attName = request.getParameter("attName");
        String attVal = request.getParameter("attVal");
        System.out.println(userId + "/" + attName + "/" + attVal);
        idmProc.renameAttribute(userId, attName, attVal);
    }

    public void createRoletype(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String roletypeId = request.getParameter("roletypeId");
        idmProc.createRoletype(roletypeId);
    }

    public void deleteRoletype(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String roletypeId = request.getParameter("roletypeId");
        idmProc.deleteRoletype(roletypeId);
    }

    public void associateRole(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String roletypeId = request.getParameter("roletypeId");
        String userId = request.getParameter("userId");
        String groupName = request.getParameter("groupName");
        idmProc.associateRole(roletypeId, userId, groupName);
    }

    public void deassociateRole(HttpServletRequest request, HttpServletResponse response) {
        IdmProcessor idmProc = getIdmProcessor(request);

        String roletypeId = request.getParameter("roletypeId");
        String userId = request.getParameter("userId");
        String groupName = request.getParameter("groupName");
        idmProc.deassociateRole(roletypeId, userId, groupName);
    }
}
