/**
 * $Id: mxOrthogonalLayout.java,v 1.1 2008/10/02 12:49:11 gaudenz Exp $
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.layout.orthogonal;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.orthogonal.model.mxGraphRectangulation;
import com.mxgraph.view.mxGraph;

/**
 *
 */
public class mxOrthogonalLayout extends mxGraphLayout
{

	/**
	 * 
	 */
	protected mxGraphRectangulation rectangulation;

	/**
	 * 
	 */
	public mxOrthogonalLayout(mxGraph graph)
	{
		super(graph);
	}

	/**
	 * 
	 */
	public void execute(Object parent)
	{
		// Empty
	}

}
