/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *
 ******************************************************************************/
package net.bioclipse.ui.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.scripting.ui.business.IJsConsoleManager;
import net.bioclipse.ui.business.describer.EditorDetermination;
import net.bioclipse.ui.business.describer.IBioObjectDescriber;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Contains general methods for interacting with the Bioclipse graphical
 * user interface (GUI).
 *
 * @author masak
 */
public class UIManager implements IUIManager {

    public String getNamespace() {
        return "ui";
    }

    public void remove( IFile file ) {
        //TODO: jonalv use real progressmonitor
        try {
            file.delete(true, new NullProgressMonitor());
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void open( final IFile file ) {

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    IDE.openEditor(page, file);
                } catch (PartInitException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void remove( String filePath ) {
        remove(ResourcePathTransformer.getInstance().transform( filePath ));
    }

    public void open( String filePath ) {
        open(ResourcePathTransformer.getInstance().transform( filePath ));
    }

    public void open( final IBioObject bioObject, final String editorId) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                   IEditorInput input = new IEditorInput() {

                    public boolean exists() {

                        return true;
                    }

                    public ImageDescriptor getImageDescriptor() {

                       return ImageDescriptor.getMissingImageDescriptor();
                    }

                    public String getName() {

                        return "BioObject";
                    }

                    public IPersistableElement getPersistable() {

                        return null;
                    }

                    public String getToolTipText() {

                        return bioObject.getUID().toString();
                    }

                    public Object getAdapter( Class adapter ) {
                        return bioObject.getAdapter( adapter );
                    }

                   };
                   page.openEditor( input, editorId );
                } catch (PartInitException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void save(String filePath, InputStream toWrite) {
        save(
            ResourcePathTransformer.getInstance().transform( filePath ),
            toWrite, null, null
        );
    }

    public void save(final IFile target, InputStream toWrite,
                     IProgressMonitor monitor, Runnable callbackFunction) {
        if (monitor == null) monitor = new NullProgressMonitor();
        try {
            int ticks = 10000;
            monitor.beginTask("Writing file", ticks);
            if (target.exists()) {
                target.setContents(toWrite, false, true, monitor);
            } else {
                target.create(toWrite, false, monitor);
            }
            monitor.worked(ticks);
        } catch (Exception exception) {
            throw new RuntimeException(
                "Error while saving to IFile", exception
            );
        } finally {
            monitor.done();
        }
        if (callbackFunction != null) {
            Display.getDefault().asyncExec(callbackFunction);
        }
    }

	public boolean fileExists(IFile file) {
		return file.exists();
	}

	public boolean fileExists(String filePath) {
		try {
			return fileExists(ResourcePathTransformer.getInstance()
					.transform( filePath ));
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	
  public void open(IBioObject bioObject) throws BioclipseException, CoreException, IOException {

      //Strategy: Determine editor ID from IBioObject and open in this

      IJsConsoleManager js = net.bioclipse.scripting.ui.Activator.getDefault().getJsConsoleManager();

      //If bioObject has a resource, use Content Type on this to determine editor
      if (bioObject.getResource()!=null){
          if ( bioObject.getResource() instanceof IFile ) {
            IFile file = (IFile) bioObject.getResource();

            IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
            InputStream stream = file.getContents();
            IContentType contentType = contentTypeManager.findContentTypeFor(stream, file.getName());
            
//            IEditorDescriptor[] editors = PlatformUI.getWorkbench().getEditorRegistry().getEditors( file.getName(), contentType );
            IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor( file.getName(), contentType );
            if (editor!=null){
                js.print( "Chosen editor: " + editor.getLabel());
                open( bioObject, editor.getId() );
                return;
            }
        }
      }
      
      //Ok, either we had a file but could not get an editor for it, or we don't
      //have a resource for the IBioObject
      
      //Get all describers that are valid for this bioObject
      List<IBioObjectDescriber> describers = EditorDetermination.getAvailableDescribersFromEP();

      for (IBioObjectDescriber describer : describers){
          String editorId=describer.getPreferredEditorID( bioObject );
          //For now, just grab the first that comes.
          //TODO: implement some sort of hierarchy here if multiple matches
          if (editorId!=null){
              IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().findEditor( editorId );
              if (editor!=null){
                  js.print( "Chosen editor: " +editor.getLabel());
                  open( bioObject, editor.getId() );
                  return;
              }
          }
      }
      throw new IllegalArgumentException("No editor found for object: " + bioObject);

  }
  
}
