/*
 * Copyright (C) 2006 Jeremy Haile
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 *
 * Free Software Foundation, Inc.
 * 59 Temple Place, Suite 330
 * Boston, MA 02111-1307
 * USA
 *
 * Or, you may view it online at
 * http://www.opensource.org/licenses/lgpl-license.php
 */

package org.jsecurity;

import org.jsecurity.authc.Authenticator;
import org.jsecurity.authz.AuthorizationException;
import org.jsecurity.authz.Authorizer;
import org.jsecurity.realm.Realm;

import java.security.Permission;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

/**
 * A <tt>SecurityManager</tt> is a convenience mechanism - it extends both the {@link Authenticator} and
 * {@link Authorizer} interfaces, thereby consolidating both behaviors into one.  This allows applications to
 * interact with a single component for most JSecurity operations should they choose to do so.
 *
 * @see DefaultSecurityManager
 *
 * @since 0.2
 * @author Jeremy Haile
 */
public interface SecurityManager extends Authenticator, Authorizer {

    /**
     * Retrieves a realm by its unique name.
     * @param realmName the unique name of the realm to be retrieved.
     * @return the realm associated with the given name.
     * @throws IllegalArgumentException if a realm with the given name is not found.
     */
    Realm getRealm( String realmName ) throws IllegalArgumentException;


    /**
     * Sets the realms that should be managed by this realm manager.
     * @param realms the realms that should be managed by this realm manager.
     */
    void setRealms( List<Realm> realms );


    /**
     * Returns all realms that are managed by this realm manager.
     * @return a list of realms that are managed by this realm manager.
     */
    List<Realm> getAllRealms();

    /**
     * Returns <tt>true</tt> if the subject with the id of <tt>subjectIdentifier</tt> has the role
     * with the id of <tt>roleIdentifier</tt>, <tt>false</tt> otherwise.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username, and the <tt>roleIdentifier</tt> is usually a <tt>Role</tt> entity's
     * primary key or a String role name.
     *
     * <p>The runtime values of the method arguments are specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check for role association (usually a user id or username).
     * @param roleIdentifier the application-specific identifier of the role to check for
     * association with a subject (usually a role id or role name ).
     * @return <tt>true</tt> if the subject with the id of <tt>subjectIdentifier</tt> has the role
     * with the id of <tt>roleIdentifier</tt>, <tt>false</tt> otherwise.
     */
    boolean hasRole( Principal subjectIdentifier, String roleIdentifier );

    /**
     * Checks to see if the roles with the given identifiers are associated with the subject (user)
     * with the given identifier and returns a boolean array indicating which
     * roles are associated with the given subject.
     *
     * <p>This is primarily a performance-enhancing method to help reduce the number of
     * {@link #hasRole} invocations over the wire in client/server systems.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username, and <tt>roleIdentifiers</tt> is usually a List of <tt>Role</tt>
     * entity primary keys or String role names.
     *
     * <p>The runtime values of the method arguments are specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check for role association (usually a user id or username).
     * @param roleIdentifiers the identifiers of the roles to check for.
     * @return an array of booleans whose indices correspond to the index of the
     * roles in the given identifiers.  A true value indicates the user has the
     * role at that index.  False indicates the user does not have the role.
     */
    boolean[] hasRoles( Principal subjectIdentifier, List<String> roleIdentifiers );

    /**
     * Returns <tt>true</tt> if the subject with the given <tt>subjectIdentifier</tt> has all the
     * roles with the given identifiers, <tt>false</tt> otherwise.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username, and <tt>roleIdentifiers</tt> is usually a List of <tt>Role</tt>
     * entity primary keys or a String role names.
     *
     * <p>The runtime values of the method arguments are specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check for role association (usually a user id or username).
     * @param roleIdentifiers the roles to be checked.
     * @return true if the user has all roles, false otherwise.
     */
    boolean hasAllRoles( Principal subjectIdentifier, Collection<String> roleIdentifiers );

    /**
     * Returns <tt>true</tt> if the subject with the given <tt>subjectIdentifier</tt> is
     * permitted to perform an action or access a resource summarized by the specified permission.
     *
     * <p>More specifically, this method should determine if any <tt>Permission</tt>s associated
     * with the subject {@link java.security.Permission#implies(java.security.Permission) imply} the
     * specified permission.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username.  The runtime value of the <tt>subjectIdentifier</tt>
     * is specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check (usually a user id or username).
     * @param permission the permission that is being checked.
     * @return true if the user associated with this context is permitted, false otherwise.
     */
    boolean isPermitted( Principal subjectIdentifier, Permission permission );

    /**
     * Checks a collection of permissions to see if they are associated with the subject with
     * the given <tt>subjectIdentifier</tt> and and returns a boolean array indicating which
     * permissions are associated with the subject.
     *
     * <p>More specifically, this method should determine if each <tt>Permission</tt> in
     * the array is {@link Permission#implies(java.security.Permission) implied by} permissions
     * already associated with the subject.
     *
     * <p>This is primarily a performance-enhancing method to help reduce the number of
     * {@link #isPermitted} invocations over the wire in client/server systems.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username.  The runtime value of the <tt>subjectIdentifier</tt>
     * is specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check (usually a user id or username).
     * @param permissions the permissions to check for.
     * @return an array of booleans whose indices correspond to the index of the
     * permissions in the given list.  A true value at an index indicates the user is permitted for
     * for the associated <tt>Permission</tt> object in the list.  A false value at an index
     * indicates otherwise.
     */
    boolean[] isPermitted( Principal subjectIdentifier, List<Permission> permissions );

    /**
     * Checks if the the subject with the given <tt>subjectIdentifier</tt> implies all the
     * specified permissions.
     *
     * <p>More specifically, this method should determine if <em>all</em> of the given
     * <tt>Permission</tt>s are {@link Permission#implies(java.security.Permission) implied by}
     * permissions already associated with the subject.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username.  The runtime value of the <tt>subjectIdentifier</tt>
     * is specific to the application and
     * determined by the application's JSecurity configuration.
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check (usually a user id or username).
     * @param permissions the permissions to be checked.
     * @return true if the user has all permissions, false otherwise.
     */
    boolean isPermittedAll( Principal subjectIdentifier, Collection<Permission> permissions );


    /**
     * A convenience method to ensure a subject (and/or user)
     * {@link Permission#implies(java.security.Permission) implies} the specified <tt>Permission</tt>.
     * If the subject's exisiting associated permissions do not
     * {@link Permission#implies(java.security.Permission) imply} the given permission,
     * an {@link org.jsecurity.authz.AuthorizationException} will be thrown.
     *
     * <p>In most systems, the <tt>subjectIdentifier</tt> is usually a <tt>Principal</tt>
     * representing a <tt>User</tt> database primary
     * key or a String username.  The runtime value of the <tt>subjectIdentifier</tt>
     * is specific to the application and
     * determined by the application's JSecurity configuration.
     *
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check (usually a user id or username).
     * @param permission the permission to check.
     * @throws org.jsecurity.authz.AuthorizationException if the user does not have the permission.
     */
    void checkPermission( Principal subjectIdentifier, Permission permission ) throws AuthorizationException;


    /**
     * A convenience method to ensure a subject (and/or user)
     * {@link Permission#implies(java.security.Permission) implies} <em>all</em> of the
     * specified <tt>Permission</tt>s.
     * If the subject's exisiting associated permissions do not
     * {@link Permission#implies(java.security.Permission) imply} <em>all</em> of the given permissions,
     * an {@link org.jsecurity.authz.AuthorizationException} will be thrown.
     * @param subjectIdentifier a <tt>Principal</tt> representing the application-specific identifier
     * for the subject to check (usually a user id or username).
     * @param permissions the permissions to check.
     * @throws AuthorizationException if the user does not have all of the given
     * permissions.
     */
    void checkPermissions( Principal subjectIdentifier, Collection<Permission> permissions ) throws AuthorizationException;    

}