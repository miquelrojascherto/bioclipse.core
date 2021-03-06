/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Jonathan Alvarsson
 *
 ******************************************************************************/
package net.bioclipse.ui.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.managers.business.GuiAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Controls programmatic access to the Bioclipse graphical user
 * interface (GUI).
 *
 * @author masak
 *
 */
@PublishedClass(value = "Controls access to Bioclipse UI.")
@TestClasses(
    "net.bioclipse.ui.business.tests.UIManagerTest," +
    "net.bioclipse.ui.business.tests.UIManagerPluginTest"
)
public interface IUIManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Opens a file in an editor.")
    @TestMethods("testOpen_String")
    @GuiAction
    public void open(String filePath);

    @Recorded
    @GuiAction
    public void open(IFile file);

    @GuiAction
    public void open(String filePath, String editor) throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "IFile file, String editor",
        methodSummary = "Opens a file in an editor specified by ID or alias." )
    @GuiAction
    public void open(IFile file, String editor) throws BioclipseException;

    @Recorded
    @GuiAction
    @PublishedMethod(
        params = "IBioObject bioObject, String editor",
        methodSummary = "Opens a file in the designated editor, identified " +
        		            "by editor id or shortname." )
    public void open( final IBioObject bioObject, final String editor)
                throws BioclipseException;

    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Deletes a file.")
    @TestMethods("testSaveAndRemove_String")
    public void remove(String filePath);

    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void remove(IFile file);

    @Recorded
    @PublishedMethod(
        params = "String filePath, InputStream content",
        methodSummary = "Save the content of the InputStream to the given path."
    )
    @TestMethods("testSaveAndRemove_String")
    public void save(String filePath, InputStream toWrite);

    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void save( IFile file, 
                      InputStream toWrite,
                      Runnable callBackFunction );

    @Recorded
    @TestMethods("testExists_IFile")
    public boolean fileExists(IFile file);

    @Recorded
    @PublishedMethod(
        params = "String filePath",
        methodSummary = "Determines if the given file exists in the workspace"
    )
    @TestMethods("testExists_String")
    public boolean fileExists(String filePath);

    @Recorded
    @GuiAction
    @PublishedMethod(
        params = "IBioObject bioObject",
        methodSummary = "Opens the IBioObject in its preferred editor"
    )
    void open( IBioObject bioObject ) 
         throws BioclipseException, CoreException, IOException;

    @Recorded
    @GuiAction
    @PublishedMethod(
        methodSummary = "Returns a list of available editors"
    )
    public void getEditors() throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "String path",
        methodSummary = "Creates an empty file at the specified path"
    )
    public void newFile( String path) throws CoreException, BioclipseException;

    @Recorded
    @PublishedMethod(methodSummary = "Closes the active editor")
    public void closeActiveEditor();

    @Recorded
    @PublishedMethod( params="String path",
                      methodSummary="Closes all editors working on a file" )
    public void closeEditor(String path);

    public void closeEditor(IFile file);

    @Recorded
    @GuiAction
    public void revealAndSelect( IFile file ) throws BioclipseException;

    @Recorded
    @GuiAction
    @PublishedMethod( params="String path",
                      methodSummary="Reveals a file in Navigator" )
    public void revealAndSelect( String path ) throws BioclipseException;

    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Refresh resource given by path" )
    public void refresh(String path) throws BioclipseException;

    @Recorded
    @PublishedMethod( params="String feature",
                      methodSummary="Assert a feature is installed." )
    public void assertInstalled( String feature ) throws BioclipseException;

    @Recorded
    @PublishedMethod( methodSummary="Get a list of the installed features." )
    public List<String> getInstalledFeatures();
}
