/*****************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package net.bioclipse.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that allows indication of a comma-separated list of
 * JUnit4 test classes that tests the annotated manager.
 * 
 * @author egonw
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface TestClasses {
    /**
     * Returns the comma-separated list of test methods.
     */
    String value();
}
