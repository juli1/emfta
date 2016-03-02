/*
 * <copyright>
 * Copyright  2012 by Carnegie Mellon University, all rights reserved.
 *
 * Use of the Open Source AADL Tool Environment (OSATE) is subject to the terms of the license set forth
 * at http://www.eclipse.org/org/documents/epl-v10.html.
 *
 * NO WARRANTY
 *
 * ANY INFORMATION, MATERIALS, SERVICES, INTELLECTUAL PROPERTY OR OTHER PROPERTY OR RIGHTS GRANTED OR PROVIDED BY
 * CARNEGIE MELLON UNIVERSITY PURSUANT TO THIS LICENSE (HEREINAFTER THE "DELIVERABLES") ARE ON AN "AS-IS" BASIS.
 * CARNEGIE MELLON UNIVERSITY MAKES NO WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED AS TO ANY MATTER INCLUDING,
 * BUT NOT LIMITED TO, WARRANTY OF FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, INFORMATIONAL CONTENT,
 * NONINFRINGEMENT, OR ERROR-FREE OPERATION. CARNEGIE MELLON UNIVERSITY SHALL NOT BE LIABLE FOR INDIRECT, SPECIAL OR
 * CONSEQUENTIAL DAMAGES, SUCH AS LOSS OF PROFITS OR INABILITY TO USE SAID INTELLECTUAL PROPERTY, UNDER THIS LICENSE,
 * REGARDLESS OF WHETHER SUCH PARTY WAS AWARE OF THE POSSIBILITY OF SUCH DAMAGES. LICENSEE AGREES THAT IT WILL NOT
 * MAKE ANY WARRANTY ON BEHALF OF CARNEGIE MELLON UNIVERSITY, EXPRESS OR IMPLIED, TO ANY PERSON CONCERNING THE
 * APPLICATION OF OR THE RESULTS TO BE OBTAINED WITH THE DELIVERABLES UNDER THIS LICENSE.
 *
 * Licensee hereby agrees to defend, indemnify, and hold harmless Carnegie Mellon University, its trustees, officers,
 * employees, and agents from all claims or demands made against them (and any related losses, expenses, or
 * attorney's fees) arising out of, or relating to Licensee's and/or its sub licensees' negligent use or willful
 * misuse of or negligent conduct or willful misconduct regarding the Software, facilities, or other rights or
 * assistance granted by Carnegie Mellon University under this License, including, but not limited to, any claims of
 * product liability, personal injury, death, damage to property, or violation of any laws or regulations.
 *
 * Carnegie Mellon Carnegie Mellon University Software Engineering Institute authored documents are sponsored by the U.S. Department
 * of Defense under Contract F19628-00-C-0003. Carnegie Mellon University retains copyrights in all material produced
 * under this contract. The U.S. Government retains a non-exclusive, royalty-free license to publish or reproduce these
 * documents, or allow others to do so, for U.S. Government purposes only pursuant to the copyright license
 * under the contract clause at 252.227.7013.
 * </copyright>
 */
package org.osate.aadl2.errormodel.emfta.actions;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.osate.aadl2.Element;
import org.osate.aadl2.Feature;
import org.osate.aadl2.errormodel.emfta.fta.EmftaWrapper;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.util.OsateDebug;
import org.osate.ui.actions.AaxlReadOnlyActionAsJob;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorTypes;
import org.osate.xtext.aadl2.errormodel.errorModel.OutgoingPropagationCondition;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;

public final class EMFTAAction extends AaxlReadOnlyActionAsJob {

	private static String ERROR_STATE_NAME = null;
	private static final String prefixState = "state ";
	private static final String prefixOutgoingPropagation = "outgoing propagation on ";
	SystemInstance si;
	private org.osate.aadl2.errormodel.analysis.fta.Event ftaEvent;

	@Override
	protected String getMarkerType() {
		return "org.osate.analysis.errormodel.FaultImpactMarker";
	}

	@Override
	protected String getActionName() {
		return "FTA";
	}

	@Override
	public void doAaxlAction(IProgressMonitor monitor, Element obj) {


		monitor.beginTask("Fault Tree Analysis", IProgressMonitor.UNKNOWN);

		si = null;

		if (obj instanceof InstanceObject) {
			si = ((InstanceObject) obj).getSystemInstance();
		}

		if (si == null) {
			Dialog.showInfo("Fault Tree Analysis", "Please choose an instance model");
			monitor.done();
		}

		if (!EMV2Util.hasCompositeErrorBehavior(si)) {
			Dialog.showInfo("Fault Tree Analysis", "Your system instance does not have a composite error behavior");
			monitor.done();
		}

		final Display d = PlatformUI.getWorkbench().getDisplay();
		d.syncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchWindow window;
				Shell sh;
				List<String> stateNames = new ArrayList<String> ();

				window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				sh = window.getShell();

				for (ErrorBehaviorState ebs : EMV2Util.getAllErrorBehaviorStates(si)) {
					stateNames.add(prefixState + ebs.getName());
				}

				for (OutgoingPropagationCondition opc : EMV2Util.getAllOutgoingPropagationConditions(si))
				{
					if ( ! (opc.getOutgoing().getFeatureorPPRef().getFeatureorPP() instanceof Feature))
					{
						continue;
					}
					Feature feat = (Feature) opc.getOutgoing().getFeatureorPPRef().getFeatureorPP();
					stateNames.add (prefixOutgoingPropagation + feat.getName());
				}



				FTADialog diag = new FTADialog(sh);
				diag.setValues(stateNames);
				diag.open();
				ERROR_STATE_NAME = diag.getValue();

			}
		});

		if (ERROR_STATE_NAME != null) {
			String errorStateName;
			String errorStateTypeName;
			ErrorBehaviorState errorState;
			ErrorTypes errorType;
			ErrorPropagation errorPropagation;
			String toProcess;

			errorState = null;
			errorType = null;
			errorPropagation = null;

			boolean processState;

			if (ERROR_STATE_NAME.startsWith(prefixState))
			{
				toProcess = ERROR_STATE_NAME.replace(prefixState, "");
				processState = true;
				OsateDebug.osateDebug("Will process a state" + toProcess);

				for (ErrorBehaviorState ebs : EMV2Util.getAllErrorBehaviorStates(si)) {
					if (ebs.getName().equalsIgnoreCase(toProcess)) {
						errorState = ebs;

//						if (errorStateTypeName != null) {
//							for (TypeToken tt : ebs.getTypeSet().getTypeTokens()) {
//								for (ErrorTypes et : tt.getType()) {
//									if (et.getName().equalsIgnoreCase(errorStateTypeName)) {
//										errorType = et;
//									}
//								}
//							}
//						}
					}
				}


			}

			if (ERROR_STATE_NAME.startsWith(prefixOutgoingPropagation))
			{
				toProcess = ERROR_STATE_NAME.replace(prefixOutgoingPropagation, "");
				processState = false;
				OsateDebug.osateDebug("Will process an outgoing propagation" + toProcess);

				for (OutgoingPropagationCondition opc : EMV2Util.getAllOutgoingPropagationConditions(si)) {
					if (! (opc.getOutgoing().getFeatureorPPRef().getFeatureorPP() instanceof Feature))
					{
						continue;
					}

					Feature feat = (Feature) opc.getOutgoing().getFeatureorPPRef().getFeatureorPP();
					if (feat.getName().equalsIgnoreCase(toProcess))
					{
						errorPropagation = opc.getOutgoing();
					}
				}
			}

			EmftaWrapper wrapper;
			wrapper = null;

			if ((errorState != null) || (errorPropagation != null)) {

				if (errorState != null)
				{
					wrapper = new EmftaWrapper(si, errorState, errorType);
				}
				if (errorPropagation != null)
				{
					wrapper = new EmftaWrapper(si, errorPropagation, errorType);
				}

				URI newURI = EcoreUtil.getURI(si).trimSegments(2).appendSegment(si.getName().toLowerCase() + ".emfta");

				serializeEmftaModel(wrapper.getEmftaModel(), newURI, ResourceUtil.getFile(si.eResource())
						.getProject());

			} else {
				Dialog.showInfo("Fault Tree Analysis",
						"Unable to create the Fault Tree Analysis, please read the help content");
			}
		}

		monitor.done();
	}

	public static void serializeEmftaModel(edu.cmu.emfta.FTAModel emftaModel, URI newURI, IProject activeProject) {

//		OsateDebug.osateDebug("[EMFTAAction]", "serializeReqSpecModel activeProject=" + activeProject);

//		IFile newFile = activeProject.getFile(filename);
//		OsateDebug.osateDebug("[EMFTAAction]", "save in file=" + newFile.getName());
		IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(newURI.toPlatformString(true)));

		try {

			ResourceSet set = new ResourceSetImpl();

			Resource res = set.createResource(URI.createURI(newFile.toString()));

			res.getContents().add(emftaModel);

			FileOutputStream fos = new FileOutputStream(newFile.getRawLocation().toFile());
			res.save(fos, null);
//			IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
//			OsateDebug.osateDebug("[EMFTAAction]", "activeproject=" + activeProject.getName());

			activeProject.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
