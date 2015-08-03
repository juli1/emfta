package edu.cmu.emfta.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeSpec;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DSemanticDiagramSpec;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.xtext.ui.util.ResourceUtil;

public class ValidateProbabilityAction extends AbstractExternalJavaAction {

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {

//		System.out.println("[CutSetAction] calling execute");
		for (EObject eo : selections) {
			EObject target = null;

//			System.out.println("[CutSetAction] eobject = " + eo);

			if (eo instanceof DSemanticDiagramSpec) {
				DSemanticDiagramSpec ds = (DSemanticDiagramSpec) eo;
				target = ds.getTarget();
//
//				System.out.println("[CutSetAction] eobject class= " + eo.getClass());
//
				System.out.println("[CutSetAction] target = " + target);
			}

			if (eo instanceof DNodeSpec) {
				DNodeSpec ds = (DNodeSpec) eo;
				target = ds.getTarget();
//
//				System.out.println("[CutSetAction] eobject class= " + eo.getClass());
//
				System.out.println("[CutSetAction] target = " + target);
			}

			if (target != null) {

				System.out.println("Check Probability for event = " + target);
			}

			MessageBox dialog = new MessageBox(Display.getDefault().getActiveShell(), SWT.ERROR | SWT.ICON_ERROR);
			dialog.setText("Error");
			dialog.setMessage("Please select an event in the FTA tree");

			dialog.open();

		}
	}

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {

		/**
		 * For now, we return true all the time. Might need to optimize
		 * it to make it more user-friendly.
		 */
//		System.out.println("[CutSetAction] calling canExecute");
		for (EObject eo : selections) {
//			System.out.println("[CutSetAction] eobject class= " + eo.getClass());

			if (eo instanceof DSemanticDiagramSpec) {
				DSemanticDiagramSpec ds = (DSemanticDiagramSpec) eo;
				EObject target = ds.getTarget();

//				System.out.println("[CutSetAction] eobject class= " + eo.getClass());
//
//				System.out.println("[CutSetAction] target = " + target);
			}

			if (eo instanceof DNodeSpec) {
				DNodeSpec ds = (DNodeSpec) eo;
				EObject target = ds.getTarget();

//				System.out.println("[CutSetAction] eobject class= " + eo.getClass());
//
//				System.out.println("[CutSetAction] target = " + target);

				if (target instanceof edu.cmu.emfta.Event) {
					return true;
				}

				if (target instanceof edu.cmu.emfta.Tree) {
					return true;
				}

				if (target instanceof edu.cmu.emfta.FTAModel) {
					return true;
				}
			}

		}
		return false;
	}

	public void generateCutSet(edu.cmu.emfta.Event event) {
		String fileName;
		CutSet cs = new CutSet(event);
		cs.process();

		fileName = ResourceUtil.getFile(event.eResource()).getName();
		fileName = fileName.replace(".emfta", "") + ".xlsx";
//		System.out.println("filename=" + fileName);

//		System.out.println(cs);
		URI uri = EcoreUtil.getURI(event);
//		System.out.println("directory=" + uri.toPlatformString(true));
//		System.out.println("uri string=" + uri.toString());

		IPath path = new Path(uri.toPlatformString(true));
//
//		System.out.println("path=" + path.makeAbsolute().toOSString());
//		System.out.println("path2=" + Utils.getPath(tree.eResource().getURI()));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String path2 = file.getRawLocation().removeLastSegments(1).toOSString();
		path2 = path2 + File.separator + fileName;
		System.out.println("path2=" + path2);

//		final InputStream input = new ByteArrayInputStream((cs.toCSV()).getBytes());
		try {
//			toCreate.create(input, true, null);
			cs.toWorkbook().write(new FileOutputStream(path2));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.refreshWorkspace(null);
	}
}
