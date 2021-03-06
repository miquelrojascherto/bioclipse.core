/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

public class JsAction {
    private String command;
    private Hook postCommandHook;
    
    public JsAction(String command, Hook postCommandHook) {
        
        this.command = command;
        this.postCommandHook = postCommandHook;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void runPostCommandHook(Object result) {
        postCommandHook.run(result);
    }
}
