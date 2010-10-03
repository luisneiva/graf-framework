package model.modelTransformer.objectDisplay;

import java.util.ArrayList;

import controller.GrafTimer;

public class ObjDiag extends ObjectDisplay {
	
	/** Links in object diagram representation of graph */
	private ArrayList<ODLink> odLinks;
	
	private GrafTimer timer;

	public ObjDiag() {}
	public ObjDiag(ArrayList<ODObject> objects, ArrayList<ODLink> links, GrafTimer timer) {
		this(objects, links);
		this.timer = timer;
	}
	
	public ObjDiag(ArrayList<ODObject> objects, ArrayList<ODLink> links) {
		timer = null;
		this.objects = new ArrayList<DisplayObject>();
		this.objects.addAll(objects);
		odLinks = links;
	}

	
	/** Get links in object diagram representation of graph */
	public ArrayList<ODLink> getODLinks() {
		return odLinks;
	}
	/** Set links in object diagram representation of graph */
	public void setODLinks(ArrayList<ODLink> odLinks) {
		this.odLinks = odLinks;
	}
}
