/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript;

/**
 * This interface is a mix-in that adds support for Symbol-keyed properties to an interface. It is intended
 * to be mixed in with Scriptable for any object that can support properties that are keyed using a
 * Symbol. ScriptableObject implements this interface so in practice most types of objects will be
 * able to support it as well.
 *
 * @since 1.7.8
 */

public interface SymbolScriptable
    extends Scriptable
{
    /**
     * Returns true if the property index is defined. This method is not part of the
     * "Scriptable" interface because it is restricted by where it may be used.
     *
     * @param key a Symbol that identifies the property
     * @param start the object in which the lookup began
     * @return true if and only if the property was found in the object
     * @since 1.7.8
     */
    boolean has(NativeSymbol.Key key, SymbolScriptable start);

    /**
     * Returns the value of the named property or NOT_FOUND.
     *
     * If the property was created using defineProperty, the
     * appropriate getter method is called.
     *
     * @param symbol the symbol that references the property
     * @param start the object in which the lookup began
     * @return the value of the property (may be null), or NOT_FOUND
     * @since 1.7.8
     */
    Object get(NativeSymbol.Key symbol, SymbolScriptable start);

    /**
     * Sets the value of the named property, creating it if need be.
     *
     * If the property was created using defineProperty, the
     * appropriate setter method is called. <p>
     *
     * If the property's attributes include READONLY, no action is
     * taken.
     * This method will actually set the property in the start
     * object.<p>
     *
     * Unlike the other forms of "put," this method is not part of the Scriptable interface.
     * We did this to avoid breaking compatibility, and also because symbol-keyed properties
     * have a slightly different contract in that they can only be referenced by array index
     * and not by name.
     *
     * @param key the symbol that references the property
     * @param start the object whose property is being set
     * @param value value to set the property to
     * @since 1.7.8
     */
    void put(NativeSymbol.Key key, SymbolScriptable start, Object value);

    /**
     * Removes a named property from the object.
     *
     * If the property is not found, or it has the PERMANENT attribute,
     * no action is taken.
     *
     * @param key the symbol's value for the property
     * @since 1.7.8
     */
    void delete(NativeSymbol.Key key);

    /**
     * Returns an array of ids for the properties of the object.
     *
     * <p>Any properties with the attribute DONTENUM are not listed.
     * Properties with Symbol keys are listed.<p>
     *
     * @return an array of java.lang.Objects with an entry for every
     * listed property. Properties accessed via an integer index will
     * have a corresponding
     * Integer entry in the returned array. Properties accessed by
     * a String will have a String entry in the returned array.
     */
    public Object[] getIdsWithSymbols();

}
