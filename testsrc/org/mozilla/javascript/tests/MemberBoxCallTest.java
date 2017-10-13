/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.tests;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.*;

public class MemberBoxCallTest {

    Scriptable scope;

    @Test
    public void testgetOwnPropertyDescriptor() {
		Context cx = Context.enter();
		try {
			assertNotNull(evaluate(cx, "var hostObj = new AnnotatedHostObject(); hostObj;"));
			Object valueProperty = evaluate(cx, "var valueProperty = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(hostObj), \"foo\"); valueProperty;");
			assertNotNull(valueProperty);
			Object result = evaluate(cx, 
				"var result = 'valueProperty: ' + valueProperty;" +
				"if( valueProperty && valueProperty.get && valueProperty.set ) {" +
					"try { " +
						"valueProperty.set.call(hostObj, 'superVal');" +
						"result = valueProperty.get.call(hostObj);" +
					"} catch(e) { result=('failed due to ' + e); }" +
				"}"+
				"result;");
			assertEquals(result, "superVal");
        } finally {
            Context.exit();
        }
    }

    private Object evaluate(Context cx, String str) {
        return cx.evaluateString(scope, str, "<testsrc>", 0, null);
    }


    @Before
    public void init() throws Exception {
        Context cx = Context.enter();
        try {
            scope = cx.initStandardObjects();
            ScriptableObject.defineClass(scope, AnnotatedHostObject.class);
        } finally {
            Context.exit();
        }
    }

    public static class AnnotatedHostObject extends ScriptableObject {

        String foo, bar = "bar";

        public AnnotatedHostObject() {}

        @Override
        public String getClassName() {
            return "AnnotatedHostObject";
        }

        @JSConstructor
        public void jsConstructorMethod() {
            put("initialized", this, Boolean.TRUE);
        }

        @JSFunction
        public Object instanceFunction() {
            return "instanceFunction";
        }

        @JSFunction("namedFunction")
        public Object someFunctionName() {
            return "namedFunction";
        }

        @JSStaticFunction
        public static Object staticFunction() {
            return "staticFunction";
        }

        @JSStaticFunction("namedStaticFunction")
        public static Object someStaticFunctionName() {
            return "namedStaticFunction";
        }

        @JSGetter
        public String getFoo() {
            return foo;
        }

        @JSSetter
        public void setFoo(String foo) {
            this.foo = foo.toUpperCase();
        }

        @JSGetter("bar")
        public String getMyBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar.toUpperCase();
        }
    }
}
