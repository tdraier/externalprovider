/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2012 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */

package org.jahia.services.content.impl.external;

import org.jahia.services.content.JCRSessionFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JCR session implementation for VFS provider.
 * @author toto
 * Date: Apr 23, 2008
 * Time: 11:56:11 AM
 */
public class ExternalSessionImpl implements Session {
    private ExternalRepositoryImpl repository;
    private ExternalWorkspaceImpl workspace;
    private Credentials credentials;
    private Map<String, ExternalData> changedData = new HashMap<String, ExternalData>();
    private Map<String, ExternalData> deletedData = new HashMap<String, ExternalData>();

    public ExternalSessionImpl(ExternalRepositoryImpl repository, Credentials credentials) {
        this.repository = repository;
        this.workspace = new ExternalWorkspaceImpl(this);
        this.credentials = credentials;
    }

    public ExternalRepositoryImpl getRepository() {
        return repository;
    }

    public String getUserID() {
        return ((SimpleCredentials) credentials).getUserID();
    }

    public Object getAttribute(String s) {
        return null;
    }

    public String[] getAttributeNames() {
        return new String[0];
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Session impersonate(Credentials credentials) throws LoginException, RepositoryException {
        return this;
    }

    public Node getRootNode() throws RepositoryException {
        ExternalData rootFileObject = repository.getDataSource().getItemByPath("/");
        return new ExternalNodeImpl(rootFileObject, this);
    }

    public Node getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
        if (!repository.getDataSource().isSupportsUuid()) {
            throw new ItemNotFoundException("This repository does not support UUID as identifiers");
        }
        Node n = new ExternalNodeImpl(repository.getDataSource().getItemByIdentifier(uuid), this);
        if (deletedData.containsKey(n.getPath())) {
            throw new ItemNotFoundException("This node has been deleted");
        }
        return n;
    }

    public Item getItem(String path) throws PathNotFoundException, RepositoryException {
        if (deletedData.containsKey(path)) {
            throw new PathNotFoundException("This node has been deleted");
        }
        ExternalData object = repository.getDataSource().getItemByPath(path);
        return new ExternalNodeImpl(object, this);
    }

    public boolean itemExists(String path) throws RepositoryException {
        if (deletedData.containsKey(path)) {
            throw new PathNotFoundException("This node has been deleted");
        }
        try {
            ExternalData object = repository.getDataSource().getItemByPath(path);
        } catch (PathNotFoundException fse) {
            return false;
        }
        return false;
    }

    public void move(String source, String dest) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
    }

    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
        for(ExternalData data: changedData.values()) {
            repository.getDataSource().saveItem(data);
        }
        changedData.clear();
        for (String path : deletedData.keySet()) {
            repository.getDataSource().removeItemByPath(path);
        }
        deletedData.clear();
    }

    public void refresh(boolean b) throws RepositoryException {
        if (!b) {
            deletedData.clear();
            changedData.clear();
        }
    }

    public boolean hasPendingChanges() throws RepositoryException {
        return false;
    }

    public ExternalValueFactoryImpl getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new ExternalValueFactoryImpl();
    }

    public void checkPermission(String s, String s1) throws AccessControlException, RepositoryException {

    }

    public ContentHandler getImportContentHandler(String s, int i) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException {
        return null;
    }

    public void importXML(String s, InputStream inputStream, int i) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException {

    }

    public void exportSystemView(String s, ContentHandler contentHandler, boolean b, boolean b1) throws PathNotFoundException, SAXException, RepositoryException {

    }

    public void exportSystemView(String s, OutputStream outputStream, boolean b, boolean b1) throws IOException, PathNotFoundException, RepositoryException {

    }

    public void exportDocumentView(String s, ContentHandler contentHandler, boolean b, boolean b1) throws PathNotFoundException, SAXException, RepositoryException {

    }

    public void exportDocumentView(String s, OutputStream outputStream, boolean b, boolean b1) throws IOException, PathNotFoundException, RepositoryException {

    }

    public void setNamespacePrefix(String s, String s1) throws NamespaceException, RepositoryException {

    }

    public String[] getNamespacePrefixes() throws RepositoryException {
        return JCRSessionFactory.getInstance().getNamespaceRegistry().getPrefixes();
    }

    public String getNamespaceURI(String s) throws NamespaceException, RepositoryException {
        return JCRSessionFactory.getInstance().getNamespaceRegistry().getURI(s);
    }

    public String getNamespacePrefix(String s) throws NamespaceException, RepositoryException {
        return JCRSessionFactory.getInstance().getNamespaceRegistry().getPrefix(s);
    }

    public void logout() {

    }

    public boolean isLive() {
        return false;
    }

    public void addLockToken(String s) {

    }

    public String[] getLockTokens() {
        return new String[0];
    }

    public void removeLockToken(String s) {

    }

    public Map<String, ExternalData> getChangedData() {
        return changedData;
    }

    public Map<String, ExternalData> getDeletedData() {
        return deletedData;
    }

    public Node getNodeByIdentifier(String id) throws ItemNotFoundException, RepositoryException {
        return getNodeByUUID(id);
    }

    public Node getNode(String absPath) throws PathNotFoundException, RepositoryException {
        return (Node) getItem(absPath);
    }

    public Property getProperty(String absPath) throws PathNotFoundException, RepositoryException {
        return (Property) getItem(absPath);
    }

    public boolean nodeExists(String absPath) throws RepositoryException {
        return itemExists(absPath);
    }

    public boolean propertyExists(String absPath) throws RepositoryException {
        return itemExists(absPath);
    }

    public void removeItem(String absPath) throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
        getItem(absPath).remove();
    }

    public boolean hasPermission(String absPath, String actions) throws RepositoryException {
        // TODO implement me
        return false;
    }

    public boolean hasCapability(String s, Object o, Object[] objects) throws RepositoryException {
        // TODO implement me
        return false;
    }

    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        return repository.getAccessControlManager();
    }

    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        return null;
    }
}
