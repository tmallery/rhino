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
    public static final String TYPE_NAME = "symbol";

    private static final Object GLOBAL_TABLE_KEY = new Object();
    private static final Object CONSTRUCTOR_SLOT = new Object();

    public static final Key ITERATOR = new Key();
    public static final Key TO_STRING_TAG = new Key();
    public static final Key SPECIES = new Key();
    public static final Key HAS_INSTANCE = new Key();
    public static final Key IS_CONCAT_SPREADABLE = new Key();
    public static final Key TO_PRIMITIVE = new Key();
    public static final Key MATCH = new Key();
    public static final Key REPLACE = new Key();
    public static final Key SEARCH = new Key();
    public static final Key SPLIT = new Key();
    public static final Key UNSCOPABLES = new Key();

    private final String description;
    private final Key key;
    private final NativeSymbol symbolData;

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSymbol obj = new NativeSymbol();
        ScriptableObject ctor = obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);

        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            createStandardSymbol(cx, scope, ctor, "iterator", ITERATOR);
            createStandardSymbol(cx, scope, ctor, "species", SPECIES);
            createStandardSymbol(cx, scope, ctor, "toStringTag", TO_STRING_TAG);
            createStandardSymbol(cx, scope, ctor, "hasInstance", HAS_INSTANCE);
            createStandardSymbol(cx, scope, ctor, "isConcatSpreadable", IS_CONCAT_SPREADABLE);
            createStandardSymbol(cx, scope, ctor, "toPrimitive", TO_PRIMITIVE);
            createStandardSymbol(cx, scope, ctor, "match", MATCH);
            createStandardSymbol(cx, scope, ctor, "replace", REPLACE);
            createStandardSymbol(cx, scope, ctor, "search", SEARCH);
            createStandardSymbol(cx, scope, ctor, "split", SPLIT);
            createStandardSymbol(cx, scope, ctor, "unscopables", UNSCOPABLES);
        } finally {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
        }
    }

    private NativeSymbol()
    {
        this.description = null;
        this.key = new Key();
        this.symbolData = null;
    }

    private NativeSymbol(String desc, Key key) {
        this.description = desc;
        this.key = key;
        this.symbolData = this;
    }

    public NativeSymbol(NativeSymbol s) {
        this.description = s.description;
        this.key = s.key;
        this.symbolData = s.symbolData;
    }

    /**
     * Use this when we need to create symbols internally because of the convoluted way we have to
     * construct them.
     */
    public static NativeSymbol construct(Context cx, Scriptable scope, Object[] args)
    {
        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            return (NativeSymbol)cx.newObject(scope, CLASS_NAME, args);
        } finally {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
        }
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

    private static void createStandardSymbol(Context cx, Scriptable scope, ScriptableObject ctor, String name, Key key)
    {
        Scriptable sym = cx.newObject(scope, CLASS_NAME, new Object[] { name, key });
        ctor.defineProperty(name, sym, DONTENUM | READONLY | PERMANENT);
    }

    // #string_id_map#

    @Override
    protected int findPrototypeId(String s) {
        int id = 0;
//  #generated# Last update: 2016-01-26 16:39:41 PST
        L0: { id = 0; String X = null;
            int s_length = s.length();
            if (s_length==7) { X="valueOf";id=Id_valueOf; }
            else if (s_length==8) { X="toString";id=Id_toString; }
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
            return SymbolId_toStringTag;
        } else if (key == TO_PRIMITIVE) {
            return SymbolId_toPrimitive;
        }
        return 0;
    }

    private static final int
        ConstructorId_keyFor    = -2,
        ConstructorId_for       = -1,
        Id_constructor          = 1,
        Id_toString             = 2,
        Id_valueOf              = 4,
        SymbolId_toStringTag    = 3,
        SymbolId_toPrimitive    = 5,
        MAX_PROTOTYPE_ID        = SymbolId_toPrimitive;

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
        case Id_valueOf:
            initPrototypeMethod(CLASS_NAME, id, "valueOf", 0);
            break;
        case SymbolId_toStringTag:
            initPrototypeValue(id, TO_STRING_TAG, CLASS_NAME, DONTENUM | READONLY);
            break;
        case SymbolId_toPrimitive:
            initPrototypeMethod(CLASS_NAME, id, TO_PRIMITIVE, "Symbol.toPrimitive", 1);
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
                if (cx.getThreadLocal(CONSTRUCTOR_SLOT) == null) {
                    // We should never get to this via "new".
                    throw ScriptRuntime.typeError0("msg.no.symbol.new");
                } else {
                    // Unless we are being called by our own internal "new"
                    return js_constructor(args);
                }
            } else {
                return construct(cx, scope, args);
            }

        case Id_toString:
            return getSelf(thisObj).toString();
        case Id_valueOf:
        case SymbolId_toPrimitive:
            return getSelf(thisObj).js_valueOf();
        default:
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
    }

    private NativeSymbol getSelf(Object thisObj) {
        try {
            return (NativeSymbol)thisObj;
        } catch (ClassCastException cce) {
            throw ScriptRuntime.typeError1("msg.invalid.type", thisObj.getClass().getName());
        }
    }

    private static NativeSymbol js_constructor(Object[] args) {
        String desc;
        if (args.length > 0) {
            if (Undefined.instance.equals(args[0])) {
                desc = "";
            } else {
                desc = ScriptRuntime.toString(args[0]);
            }
        } else {
            desc = "";
        }

        Key key = (args.length > 1 ? (Key)args[1] : new Key());
        return new NativeSymbol(desc, key);
    }

    private Object js_valueOf() {
        // In the case that "Object()" was called we actually have a different "internal slot"
        return symbolData;
    }

    private Object js_for(Context cx, Scriptable scope, Object[] args) {
        String name = (args.length > 0 ? ScriptRuntime.toString(args[0]) : ScriptRuntime.toString(Undefined.instance));

        Map<String, NativeSymbol> table = getGlobalMap();
        NativeSymbol ret = table.get(name);

        if (ret == null) {
            ret = construct(cx, scope, new Object[]{name});
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

    // Symbol objects have a special property that one cannot add properties.

    @Override
    public void put(String name, Scriptable start, Object value)
    {
        if (!isSymbol()) {
            super.put(name, start, value);
        } else if (Context.getCurrentContext().isStrictMode()) {
            ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value)
    {
        if (!isSymbol()) {
            super.put(index, start, value);
        } else if (Context.getCurrentContext().isStrictMode()) {
            ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    @Override
    public void put(NativeSymbol.Key key, SymbolScriptable start, Object value)
    {
        if (!isSymbol()) {
            super.put(key, start, value);
        } else if (Context.getCurrentContext().isStrictMode()) {
            ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    /**
     * Object() on a Symbol constructs an object which is NOT a symbol, but which has an "internal data slot
     * that is. Furthermore, such an object has the Symbol prototype so this particular object is still used.
     * Account for that here: an "Object" that was created from a Symbol has a different value of the slot.
     */
    public boolean isSymbol()
    {
        return (symbolData == this);
    }

    @Override
    public String getTypeOf()
    {
        return (isSymbol() ? TYPE_NAME : super.getTypeOf());
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

    public String getDescription()
    {
        return description;
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
