/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Map;

public class NativeSymbol extends IdScriptableObject {

    public static final String SPECIES_PROPERTY = "@@species";
    public static final String ITERATOR_PROPERTY = "@@iterator";
    public static final String TO_STRING_TAG_PROPERTY = "@@toStringTag";

    public static final String CLASS_NAME = "Symbol";

    private static final Object GLOBAL_SYMBOL_TABLE = new Object();

    private String description;

    public static void init(Scriptable scope, boolean sealed) {
        NativeSymbol obj = new NativeSymbol(Undefined.instance);
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    private NativeSymbol(Object arg) {
        if (Undefined.instance.equals(arg)) {
            description = null;
        } else {
            description = ScriptRuntime.toString(arg);
        }
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        super.fillConstructorProperties(ctor);
        ctor.defineProperty("iterator", ITERATOR_PROPERTY, DONTENUM | READONLY | PERMANENT);
        ctor.defineProperty("species", SPECIES_PROPERTY, DONTENUM | READONLY | PERMANENT);
        ctor.defineProperty("toStringTag", TO_STRING_TAG_PROPERTY, DONTENUM | READONLY | PERMANENT);
        addIdFunctionProperty(ctor, CLASS_NAME, ConstructorId_for, "for", 1);
        addIdFunctionProperty(ctor, CLASS_NAME, ConstructorId_keyFor, "keyFor", 1);
    }

    // #string_id_map#

    @Override
    protected int findPrototypeId(String s) {
        int id = 0;
//  #generated# Last update: 2015-06-07 10:40:05 EEST
        L0: { id = 0; String X = null;
            if (s.length()==11) { X="constructor";id=Id_constructor; }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
//  #/generated#
        return id;
    }

    private static final int
        ConstructorId_keyFor    = -2,
        ConstructorId_for       = -1,
        Id_constructor          = 1,
        MAX_PROTOTYPE_ID        = Id_constructor;

    // #/string_id_map#


    @Override
    protected void initPrototypeId(int id)
    {
        String s = null;
        int arity = -1;
        switch (id) {
            case Id_constructor:        arity = 1; s = "constructor"; break;
            default:                    super.initPrototypeId(id);
        }
        initPrototypeMethod(CLASS_NAME, id, s, arity);
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
            if (args.length > 0) {
                return new NativeSymbol(args[0]);
            } else {
                return new NativeSymbol(Undefined.instance);
            }
        default:
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
    }

    private Object js_for(Context cx, Scriptable scope, Object[] args) {
        String key = (args.length > 0 ? ScriptRuntime.toString(args[0]) : ScriptRuntime.toString(Undefined.instance));
        Map<String, NativeSymbol> table = getGlobalSymbolTable(scope);

        NativeSymbol ret = table.get(key);
        if (ret == null) {
            ret = (NativeSymbol)cx.newObject(scope, CLASS_NAME, args);
            table.put(key, ret);
        }
        return ret;
    }

    private Object js_keyFor(Context cx, Scriptable scope, Object[] args) {
        Object sym = (args.length > 0 ? args[0] : Undefined.instance);
        if (!(sym instanceof NativeSymbol)) {
            throw ScriptRuntime.throwCustomError(cx, scope, "TypeError", "Not a Symbol");
        }
        Map<String, NativeSymbol> table = getGlobalSymbolTable(scope);

        for (NativeSymbol s : table.values()) {
            if (s.equals(sym)) {
                return s;
            }
        }
        return Undefined.instance;
    }

    @Override
    public String getTypeOf() {
        return "symbol";
    }

    /**
     * ES6 defines the concept of a "global symbol table" which is "shared by all Code Realms."
     * In Rhino, we are defining this as being set on the "global scope." This way multiple Rhino
     * runtimes can exist at once that share the same global symbol table.
     */
    private Map<String, NativeSymbol> getGlobalSymbolTable(Scriptable scope) {
        ScriptableObject top = (ScriptableObject)ScriptableObject.getTopLevelScope(scope);
        Map<String, NativeSymbol> table =
            (Map<String, NativeSymbol>)top.getAssociatedValue(GLOBAL_SYMBOL_TABLE);
        if (table == null) {
            table = new HashMap<String, NativeSymbol>();
            top.associateValue(GLOBAL_SYMBOL_TABLE, table);
        }
        return table;
    }

}
