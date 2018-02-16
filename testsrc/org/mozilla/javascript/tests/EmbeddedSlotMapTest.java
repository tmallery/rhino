/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 *
 */
package org.mozilla.javascript.tests;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Scriptable;

/**
 * Additional tests for EmbeddedSlopMap
 * @author Thomas Mallery
 */
public class EmbeddedSlotMapTest extends TestCase {
    
    public void testGetPropertyNames() {
        assertEvaluates("success", "var result = 'failed';" +
        "var obj = new Object();" +
        "Object.defineProperty(obj, 'aProp', {configurable:true, get:function() {return 1;}, set:function(arg){ }});" +
        "Object.defineProperty(obj, 'aProp', {value:42});" +
		"if( Object.getOwnPropertyNames(obj).length == 1 ) { result = 'success'; } result;");
    }

    private void assertEvaluates(final Object expected, final String source) {
        final ContextAction action = new ContextAction() {
            public Object run(Context cx) {
                cx.setLanguageVersion(Context.VERSION_ES6);
                final Scriptable scope = cx.initStandardObjects();
                
                final Object rep = cx.evaluateString(scope, source, "test.js",
                        0, null);
                assertEquals(expected, rep);
                return null;
            }
        };
        Utils.runWithAllOptimizationLevels(action);
    }
 }
