package edu.cmu.emfta.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

public class Utils {
	public static void refreshWorkspace(IProgressMonitor monitor) {
		for (IProject ip : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				ip.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static IPath getPath(final URI resourceURI) {
		/*
		 * I don't really understand why this method does what it does, but
		 * the point seems to be to take a URI for a Resource that resembles
		 * "platform:/resource/xxx/yyy/zzz" and return the Eclipse IPath to
		 * the file for that Resource. This seems to involve removing the
		 * "/resource/" part.
		 * 
		 * --aarong
		 */

		// Is it a "plaform:" uri?
		if (resourceURI.scheme() != null && resourceURI.scheme().equalsIgnoreCase("platform")) {
			// Get the segments. See if the first is "resource"
			final String[] segments = resourceURI.segments();
			final StringBuffer path = new StringBuffer();

			if (segments.length >= 1) {
				final int firstSegment = segments[0].equals("resource") ? 1 : 0;
				final int lastIdx = segments.length - 1;
				for (int i = firstSegment; i < (lastIdx); i++) {
					path.append(segments[i]);
					path.append('/');
				}
				if (lastIdx >= 0) {
					path.append(segments[lastIdx]);
				}
			}
			return new Path(null, path.toString());
		} else if (resourceURI.isFile()) {
			return new Path(resourceURI.toFileString());
		} else {
			throw new IllegalArgumentException("Cannot decode URI protocol: " + resourceURI.scheme());
		}
	}
}
