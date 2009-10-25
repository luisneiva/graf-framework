package MyPackage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.ResourceChange;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

public class TestClass {
	
	public static void main(String[] args) {
		// Creates the resourceSet where we'll load the models
		ResourceSet resourceSet = new ResourceSetImpl();
		


		// Register additional packages here. For UML2:
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI,
				UMLPackage.eINSTANCE);
		
		System.out.println("Loading resources..."); //$NON-NLS-1$
		
		
		Resource myResource = resourceSet.getResource(URI.createURI("microwaveUMLDiagram.uml"),true);
		EObject documentRoot = myResource.getContents().get(0);
		
		try {
			// Creates the file where the triple will be held
			PrintWriter out = new PrintWriter("protA-micro.triple");
			System.out.println("Classes:");
			for (EObject obj : documentRoot.eContents()) {
				if (obj.eClass().getName().equals("Class")) {
					String className = "" + obj.eGet(obj.eClass().getEStructuralFeature("name"));
					System.out.println("Class: " + className);		
					out.println(className + " i " + obj.eClass().getName());
					for (EObject o : obj.eContents()) {
						if (o.eClass().getName().equals("Property")) {
							String propertyName = "" + o.eGet(o.eClass().getEStructuralFeature("name"));
							out.println(className + " ownedAttribute " + propertyName);
							out.println(propertyName + " i " + o.eClass().getName());
						}
						if (o.eClass().getName().equals("Operation")) {
							String operationName = "" + o.eGet(o.eClass().getEStructuralFeature("name"));
							out.println(className + " ownedOperation " + operationName);
							out.println(operationName + " i " + o.eClass().getName());
							for (EObject opParam : o.eContents()) {
								if (opParam.eClass().getName().equals("Parameter")) {
									String parameterName = "" + opParam.eGet(opParam.eClass().getEStructuralFeature("name"));
									out.println(operationName + " ownedParameter " + parameterName);
									out.println(parameterName + " i " + opParam.eClass().getName());
								}
							}
						}
						if (o.eClass().getName().equals("StateMachine")) {
							String stateMachineName = "" + o.eGet(o.eClass().getEStructuralFeature("name"));
							out.println(className + " classifierBehavior " + stateMachineName);
							out.println(stateMachineName + " i " + o.eClass().getName());
							for (EObject region : o.eContents()) {
								if (region.eClass().getName().equals("Region")) {
									String regionName = "" + region.eGet(region.eClass().getEStructuralFeature("name"));
									out.println(stateMachineName + " region " + regionName);
									out.println(regionName + " i " + o.eClass().getName());
									for (EObject stateMachineElement : region.eContents()) {
										if (stateMachineElement.eClass().getName().equals("Pseudostate")) {
											String pseudoStateName = "" + stateMachineElement.eGet(stateMachineElement.eClass().getEStructuralFeature("name"));
											out.println(regionName + " subvertex " + pseudoStateName);
											out.println(pseudoStateName + " i " + stateMachineElement.eClass().getName());
										}
										if (stateMachineElement.eClass().getName().equals("Final State")) {
											String finalStateName = "" + stateMachineElement.eGet(stateMachineElement.eClass().getEStructuralFeature("name"));
											out.println(regionName + " subvertex " + finalStateName);
											out.println(finalStateName + " i " + stateMachineElement.eClass().getName());
										}
										if (stateMachineElement.eClass().getName().equals("State")) {
											String stateName = "" + stateMachineElement.eGet(stateMachineElement.eClass().getEStructuralFeature("name"));
											out.println(regionName + " subvertex " + stateName);
											out.println(stateName + " i " + stateMachineElement.eClass().getName());
											for (EObject state : stateMachineElement.eContents()) {
												if (state.eClass().getName().equals("Activity")) {
													String activityName = "" + state.eGet(state.eClass().getEStructuralFeature("name"));
													out.println(stateName + " ? " + activityName);
													out.println(activityName + " i " + state.eClass().getName());
												}
											}
										}
										if (stateMachineElement.eClass().getName().equals("Transition")) {
											String transitionName = "" + stateMachineElement.eGet(stateMachineElement.eClass().getEStructuralFeature("name"));
											out.println(regionName + " transition " + transitionName);
											out.println(transitionName + " i " + stateMachineElement.eClass().getName());
											for (EObject trigger : stateMachineElement.eContents()) {
												if (trigger.eClass().getName().equals("Trigger")) {
													String triggerName = "" + trigger.eGet(trigger.eClass().getEStructuralFeature("name"));
													out.println(transitionName + " trigger " + triggerName);
													out.println(triggerName + " i " + trigger.eClass().getName());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			out.close();
		}
		catch (IOException exception) {
			System.out.println("Error processing file: " + exception);
		}
	}
}
