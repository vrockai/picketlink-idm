<%@page import="org.jboss.jbossidmservlet.bean.UserBean"%>
<%@page import="org.jboss.jbossidmservlet.processor.IdmProcessor"%>
<%@page import="org.picketlink.idm.api.Group"%>
<%@page import="org.picketlink.idm.api.Role"%>
<%@page import="java.util.List"%>
<%@page import="org.picketlink.idm.api.IdentitySearchCriteria"%>
<%@page import="org.picketlink.idm.api.User"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.picketlink.idm.api.IdentitySession"%>
<%@page import="org.picketlink.idm.impl.configuration.IdentityConfigurationImpl"%>
<%@page import="org.picketlink.idm.api.IdentitySessionFactory"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/idm.tld" prefix="idm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Picketlink IDM webapp</title>
        <LINK REL=StyleSheet HREF="${pageContext.request.contextPath}/static/css/main.css" TYPE="text/css" MEDIA=screen>
        <link rel="icon" href="${pageContext.request.contextPath}/static/img/picketlink_icon_16x.png" type="image/PNG">
    </head>

    <body>
        <div class="main">
            <h1>Picketlink IDM standalone webapp</h1>
            <div class="user-div">
                <h2>User list:</h2>
                <form class="header" method="POST" action="createUser">
                    <label for="userId">new user name:</label><input type="text" name="userId" value="name"/>
                    <input type="submit" value="create new user" class="idm-button idm-adduser"/>
                </form>
                <ul class="user">
                    <c:forEach var="user" items="${userList}">
                        <li class="user">
                            <span class="idm-name">${user.userId}</span>
                            <span class="idm-toolbar">
                                <form  method="POST" action="deleteUser" class="op-form">
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <input type="submit" value="delete" class="idm-button idm-delbutton"/>
                                </form>
                                <form class="op-form" method="POST" action="deassignGroup">
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <select name="groupId">
                                        <c:forEach var="userGroup" items="${user.associatedGroups}">
                                            <option>${userGroup.name}</option>
                                        </c:forEach>
                                    </select>
                                    <input type="submit" value="deassign" class="idm-button idm-remove"/>
                                </form>
                                <form class="op-form" method="POST" action="assignGroup">
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <select name="groupId">
                                        <c:forEach var="group" items="${groupList}">
                                            <option value="${group.name}">${group.name}</option>
                                        </c:forEach>
                                    </select>
                                    <input type="submit" value="assign" class="idm-button idm-add"/>
                                </form>
                            </span>

                            <div class="att-div">
                                <form class="headeralt" method="POST" action="createAttribute">
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <label for="attName">name:</label><input type="text" name="attName" value="name"/>
                                    <label for="attVal">value:</label><input type="text" name="attVal" value="value"/>
                                    <input type="submit" value="add attribute" class="idm-button idm-adduser"/>
                                </form>
                                <ul class="attribute">
                                    <c:forEach var="attribute" items="${user.attributes}">
                                        <li><span class="idm-att-name">${attribute.name}</span>
                                            
                                            <form class="op-formalt" method="POST" action="renameAttribute">
                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                <input type="hidden" name="attName" value="${attribute.name}"/>
                                                <input type="text" name="attVal" value="${attribute.value}"/>
                                                <input type="submit" value="change value" class="idm-button idm-rename"/>
                                            </form>
                                                <span class="idm-toolbar">
                                            <form class="op-form"  method="POST" action="deleteAttribute" class="op-form">
                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                <input type="hidden" name="attName" value="${attribute.name}"/>
                                                <input type="submit" value="delete" class="idm-button idm-delbutton"/>
                                            </form>
                                                </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>

                        </li>
                    </c:forEach>
                </ul>
            </div>

            <h2>Roles:</h2>
            <div class="grp-div">
                <form class="header" method="POST" action="associateRole">
                    <label for="roletypeId">role type:</label>
                    <select name="roletypeId">
                        <c:forEach var="roletype" items="${roletypeList}">
                            <option value="${roletype.name}">${roletype.name}</option>
                        </c:forEach>
                    </select>
                    <label for="userId">user ID:</label>
                    <select name="userId">
                        <c:forEach var="user" items="${userList}">
                            <option value="${user.userId}">${user.userId}</option>
                        </c:forEach>
                    </select>
                    <label for="groupName">group name:</label>
                    <select name="groupName">
                        <c:forEach var="group" items="${groupList}">
                            <option value="${group.name}">${group.name}</option>
                        </c:forEach>
                    </select>
                    <input type="submit" value="associate role" class="idm-button idm-associate"/>
                </form>
                <ul class="alternate">
                    <c:forEach var="group" items="${groupList}">
                        <c:forEach var="role" items="${group.roleList}">
                            <li class="user">
                                <span class="bold">${role.user.id}</span>
                                is
                                <span class="bold">${role.roleType.name}</span>
                                in 
                                <span class="bold">${group.name}</span>                                
                                <span class="idm-toolbar">
                                    <form method="POST" action="deassociateRole" class="op-form">
                                        <input type="hidden" name="groupId" value="${group.name}"/>
                                        <input type="submit" value="delete" class="idm-button idm-delbutton"/>
                                    </form>
                                </span>
                            </li>
                        </c:forEach>
                    </c:forEach>
                </ul>
            </div>

            <h2>Group list:</h2>
            <div class="grp-div">
                <form class="header" method="POST" action="createGroup">
                    <label for="groupId">new group name:</label><input type="text" name="groupId" value="name"/>
                    <input type="submit" value="create new group" class="idm-button idm-addgrp"/>
                </form>
                <ul class="group alternate">
                    <c:forEach var="group" items="${groupList}">
                        <li>
                            <span class="idm-name">${group.name}</span>
                            <span class="idm-toolbar">
                                <form method="POST" action="deleteGroup" class="op-form">
                                    <input type="hidden" name="groupId" value="${group.name}"/>
                                    <input type="submit" value="delete" class="idm-button idm-delbutton"/>
                                </form>
                                    <!--
                                <form method="POST" action="RoleGroup" class="op-form">
                                    <label for="roleId">role:</label>
                                    <input type="hidden" name="groupId" value="${group.name}"/>
                                    <select name="userId">
                                        <c:forEach var="user" items="${userList}">
                                            <option value="${user.userId}">${user.userId}</option>
                                        </c:forEach>
                                    </select>
                                    <input type="submit" value="deassociate" class="idm-button idm-addgrp"/>
                                </form>
                                    -->
                            </span>
                        </li>
                    </c:forEach>
                </ul>
            </div>

            <h2>Role types:</h2>
            <div class="grp-div alternate">
                <form class="header" method="POST" action="createRoletype">
                    <label for="groupId">new role type:</label><input type="text" name="roletypeId" value="name"/>
                    <input type="submit" value="create new role type" class="idm-button idm-addgrp"/>
                </form>
                <ul class="group">
                    <c:forEach var="roletype" items="${roletypeList}">
                        <li>
                            <span class="idm-name">${roletype.name}</span>
                            <span class="idm-toolbar">
                                <form method="POST" action="deleteRoletype" class="op-form">
                                    <input type="hidden" name="roletypeId" value="${roletype.name}"/>
                                    <input type="submit" value="delete" class="idm-button idm-delbutton"/>
                                </form>
                            </span>
                        </li>
                    </c:forEach>
                </ul>
            </div>

        </div>
    </body>
</html>
