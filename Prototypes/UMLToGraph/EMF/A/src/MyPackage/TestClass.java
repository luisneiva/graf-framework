package MyPackage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
	
	private static void printEObjectTree(EObject obj) {
		printEObjectTree(obj, "");
	}
	private static void printEObjectTree(EObject obj, String indent) {
		System.out.print(indent);
		System.out.println(obj.eClass().getName());
		
		//every EObject is a node
		//every StructuralFeature is an edge
		
		boolean lastprinted = true;
		EList<EStructuralFeature> features = obj.eClass().getEAllStructuralFeatures();
		for (EStructuralFeature feature : features) {
			if (obj.eIsSet(feature)) {
				//all values are either: EObject, EList<EObject>, or primitive type (int, string, etc)
				if (obj.eGet(feature) instanceof EObject)
					System.out.println(indent + " -> " + feature.getName() + ": " + EcoreUtil.getIdentification((EObject)obj.eGet(feature)));
				else if (obj.eGet(feature) instanceof EList)			// (need to extract nodename identifier)
					//recursively call this inner bit again? (eg in case String vs int vs EObject vs EList again)  
					System.out.println(indent + " ->_" + feature.getName() + ": " + (EList)obj.eGet(feature));
				else
					System.out.println(indent + " ->X" + feature.getName() + ": " + obj.eGet(feature));
				lastprinted = true;
			} else lastprinted = false;
		}
		if (!lastprinted) System.out.println();
		
//		EStructuralFeature nameFeature = obj.eClass().getEStructuralFeature("name");
//		if (nameFeature != null) {
//			System.out.println(": " + obj.eGet(nameFeature));
//		} else {
//			System.out.println();
//		}
		for (EObject o : obj.eContents()) {
			printEObjectTree(o, indent + "   ");
		}
	}
	
	public static void main(String[] args) {
		// Creates the resourceSet where we'll load the models
		ResourceSet resourceSet = new ResourceSetImpl();

		// Register additional packages here. For UML2:
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI,
				UMLPackage.eINSTANCE);
		
		System.out.println("Loading resources..."); //$NON-NLS-1$
		
		String filepath = "Microwave.uml";
		//String filepath = "myUMLDiagram.uml";
		Resource myResource = resourceSet.getResource(URI.createURI(filepath),true);
		EObject documentRoot = myResource.getContents().get(0);
		
		System.out.println("Classes:");
		for (EObject obj : documentRoot.eContents()) {
			if (obj.eClass().getName().equals("Class")) {
				System.out.println("Class: " + obj.eGet(obj.eClass().getEStructuralFeature("name")));
			}
		}
		
		printEObjectTree(documentRoot);
		
		{
		EObject obj = myResource.getContents().get(0).eContents().get(5);
		System.out.println("EClass getName(): " + obj.eClass().getName());
		System.out.println("It's name is: " +
				obj.eGet(obj.eClass().getEStructuralFeature("name")));
		//System.out.println(obj.eClass().getEAttributes().get(5).getName()); //NO!! Metaclass!
		}
		
		/*to dynamically create an instance of a class diagram:
		EClass myEClass = EcoreFactory.eINSTANCE.createEClass();	//NO!! Metaclass!
		myEClass.setName("MyEClass");
		System.out.println(myEClass.getName());
		EObject myobj = EcoreUtil.create(myEClass);		//create a class (instance of the EClass - anything possibly)
		myobj.eSet(myEClass.getEStructuralFeature("name"), "MyClass");
		//eg to duplicate a class (for some reason):
		myEClass = myResource.getContents().get(0).eContents().get(5).eClass();
		myobj = EcoreUtil.create(myEClass);
		myobj.eSet(myEClass.getEStructuralFeature("name"), "MyClass");
		
		ChangeRecorder recorder = new ChangeRecorder();
		recorder.beginRecording(Collections.singleton(myobj));
		//myobj.e
		ChangeDescription change = recorder.endRecording();
		//change.apply();			- important? (possibly undoes the changes and empties description)
		//change.applyAndReverse();	- important? (possibly undoes but keeps changes in description)
		for (ResourceChange rc : change.getResourceChanges()) {
			for (Object obj : rc.getValue()) {
				System.out.println(obj);
			}
		}
		*/
		
		//Model _model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.CLASS);
		Collection<ClassImpl> classes =
			EcoreUtil.getObjectsByType(myResource.getContents().get(0).eContents(), UMLPackage.Literals.CLASS);
		Iterator<ClassImpl> it = classes.iterator();
		while (it.hasNext()) {
			ClassImpl myClassImpl = it.next();
			System.out.print(myClassImpl.getName() + ": {");
			EList<Property> propList = myClassImpl.getAllAttributes();
			Boolean first = true;
			for (Property attr : propList) {
				if (first) first=false; else System.out.print(", ");
				System.out.print(attr.getName());
			}
			System.out.println("}");
		}
		
		System.out.println("done");
		System.exit(0);
		
		for (EObject obj : myResource.getContents()) {
			printEObjectTree(obj);
		}
		
		System.exit(0);
		
		
		try{
			// Loads the model to modify
			final EObject model1 = ModelUtils.load(new File("myUMLDiagram.uml"), resourceSet);
			
			{
			System.out.println("--If looking for specific element (eg MyPackage::TennisBallCarrier):");
			Object[] objs = UMLUtil.findNamedElements(resourceSet, "MyPackage::TennisBallCarrier").toArray();
			for (int i = 0; i < objs.length; i++) {
				System.out.println("Name: " + ((NamedElement)objs[i]).getName());
				System.out.println("Label: " + ((NamedElement)objs[i]).getLabel());
				//System.out.println(((NamedElement)objs[i]).getValue(null, "radius"));
				System.out.println("Relationships: " + ((NamedElement)objs[i]).getRelationships());
			}
			System.out.println("-----");
			}
			try{Thread.sleep(2000);}catch(Exception e){}
			
			
			// Print out all objects, and display names and attributes of classes
			TreeIterator<EObject> objTree = model1.eAllContents();
			while (objTree.hasNext()) {
				EObject obj = objTree.next();
				System.out.print("obj EClass name: " + obj.eClass().getName());
				//if (obj instanceof ClassImpl) {}
				if (obj.eClass().getName().equals("Class")) {
					ClassImpl myClassImpl = (ClassImpl)obj;	  //Can't use EClassImpl :( (because it is not, it is just ClassImpl) 
					System.out.print("\t\t" + myClassImpl.getName() + ": {");
					EList<Property> propList = myClassImpl.getAllAttributes();
					Boolean first = true; 
					for (Property attr : propList) {
						if (first) first=false; else System.out.print(", ");
						System.out.print(attr.getName());
					}
					System.out.print("}");
				}
				System.out.println();
			}
		} catch (IOException e) {
			
		}

	}
}
