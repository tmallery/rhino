/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NativeSymbol extends IdScriptableObject {
    private static final long serialVersionUID = -589539749749830003L;

    public static final String CLASS_NAME = "Symbol";

    private static final Object GLOBAL_TABLE_KEY = new Object();

    public static final Key ITERATOR = new Key();
    public static final Key TO_STRING_TAG = new Key();
    public static final Key SPECIES = new Key();

    private final String description;
    private final Key key;

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSymbol obj = new NativeSymbol("", new Key());
        ScriptableObject ctor = obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);

        createStandardSymbol(cx, scope, ctor, "iterator", ITERATOR);
        createStandardSymbol(cx, scope, ctor, "species", SPECIES);
        createStandardSymbol(cx, scope, ctor, "toStringTag", TO_STRING_TAG);
    }

    public NativeSymbol(String desc, Key key) {
        this.description = desc;
        this.key = key;
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        super.fillConstructorProperties(ctor);
        addIdFunctionProperty(ctor, CLASS_NAME, ConstructorId_for, "for", 1);
        addIdFunctionProperty(ctor, CLASS_NAME, ConstructorId_keyFor, "keyFor", 1);
    }

    private static void createStandardSymbol(Context cx, Scriptable scope, ScriptableObject ctor, String name, Key key) {
        NativeSymbol sym = (NativeSymbol)cx.newObject(scope, CLASS_NAME, new Object[] { name, key });
        ctor.defineProperty(name, sym, DONTENUM | READONLY | PERMANENT);
    }

    // #string_id_map#

    @Override
    protected int findPrototypeId(String s) {
        int id = 0;
//  #generated# Last update: 2016-01-21 16:38:39 PST
        L0: { id = 0; String X = null;
            int s_length = s.length();
            if (s_length==8) { X="toString";id=Id_toString; }
            else if (s_length==11) { X="constructor";id=Id_constructor; }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
//  #/generated#
        return id;
    }

    @Override
    protected int findPrototypeId(Key key) {
        if (key == TO_STRING_TAG) {
            return Id_toStringTag;
        }
        return 0;
    }

    private static final int
        ConstructorId_keyFor    = -2,
        ConstructorId_for       = -1,
        Id_constructor          = 1,
        Id_toString             = 2,
        Id_toStringTag          = 3,
        MAX_PROTOTYPE_ID        = Id_toStringTag;

    // #/string_id_map#


    @Override
    protected void initPrototypeId(int id)
    {
        String s = null;
        int arity = -1;
        switch (id) {
        case Id_constructor:
            initPrototypeMethod(CLASS_NAME, id, "constructor", 1);
            break;
        case Id_toString:
            initPrototypeMethod(CLASS_NAME, id, "toString", 0);
            break;
        case Id_toStringTag:
            initPrototypeValue(id, TO_STRING_TAG, CLASS_NAME, DONTENUM | READONLY);
            break;
        default:
            super.initPrototypeId(id);
            break;
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CLASS_NAME)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
        case ConstructorId_for:
            return js_for(cx, scope, args);
        case ConstructorId_keyFor:
            return js_keyFor(cx, scope, args);

        case Id_constructor:
            if (thisObj == null) {
                return js_constructor(args);
            } else {
                return cx.newObject(scope, CLASS_NAME, args);
            }

        case Id_toString:
            return getSelf(thisObj).toString();
        default:
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
    }

    private NativeSymbol getSelf(Object thisObj) {
        try {
            return (NativeSymbol)thisObj;
        } catch (ClassCastException cce) {
            throw ScriptRuntime.typeError("Symbol expected");
        }
    }

    private Object js_constructor(Object[] args) {
        String desc = (args.length > 0 ? ScriptRuntime.toString(args[0]) : "");
        Key key = (args.length > 1 ? (Key)args[1] : new Key());
        return new NativeSymbol(desc, key);
    }

    private Object js_for(Context cx, Scriptable scope, Object[] args) {
        String name = (args.length > 0 ? ScriptRuntime.toString(args[0]) : ScriptRuntime.toString(Undefined.instance));

        Map<String, NativeSymbol> table = getGlobalMap();
        NativeSymbol ret = table.get(name);

        if (ret == null) {
            ret = (NativeSymbol)cx.newObject(scope, CLASS_NAME, new Object[] { name });
            table.put(name, ret);
        }
        return ret;
    }

    private Object js_keyFor(Context cx, Scriptable scope, Object[] args) {
        Object s = (args.length > 0 ? args[0] : Undefined.instance);
        if (!(s instanceof NativeSymbol)) {
            throw ScriptRuntime.throwCustomError(cx, scope, "TypeError", "Not a Symbol");
        }
        NativeSymbol sym = (NativeSymbol)s;

        Map<String, NativeSymbol> table = getGlobalMap();
        for (Map.Entry<String, NativeSymbol> e : table.entrySet()) {
            if (e.getValue().key == sym.key) {
                return e.getKey();
            }
        }
        return Undefined.instance;
    }

    @Override
    public String toString() {
        return "Symbol(" + description + ')';
    }

    @Override
    public String getTypeOf() {
        return "symbol";
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object x) {
        try {
            return key.equals(((NativeSymbol) x).key);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public Key getKey() {
        return key;
    }

    private Map<String, NativeSymbol> getGlobalMap() {
        ScriptableObject top = (ScriptableObject)getTopLevelScope(this);
        Map<String, NativeSymbol> map = (Map<String, NativeSymbol>)top.getAssociatedValue(GLOBAL_TABLE_KEY);
        if (map == null) {
            map = new HashMap<String, NativeSymbol>();
            top.associateValue(GLOBAL_TABLE_KEY, map);
        }
        return map;
    }

    /**
     *  This is a class used to create unique identifiers. It is always compared by identity, so that
     *  we can use it as a unique ID for a symbol.
     */
    public static final class Key
        implements Serializable
    {
        private static final long serialVersionUID = 4047763936785129774L;

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public boolean equals(Object x) {
            return (x == this);
        }
    }
}
