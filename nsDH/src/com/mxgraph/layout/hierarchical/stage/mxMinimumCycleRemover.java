/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package com.mxgraph.layout.hierarchical.stage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyEdge;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyModel;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyNode;
import com.mxgraph.view.mxGraph;

/**
 * An implementation of the first stage of the Sugiyama layout. Straightforward
 * longest path calculation of layer assignment
 */
public class mxMinimumCycleRemover implements mxHierarchicalLayoutStage
{

	/**
	 * Reference to the enclosing layout algorithm
	 */
	protected mxHierarchicalLayout layout;

	/**
	 * Constructor that has the roots specified
	 */
	public mxMinimumCycleRemover(mxHierarchicalLayout layout)
	{
		this.layout = layout;
	}

	/**
	 * Produces the layer assignmment using the graph information specified
	 */
	public void execute(Object parent)
	{
		mxGraphHierarchyModel model = layout.getModel();
		final Set seenNodes = new HashSet();
		final Set unseenNodes = new HashSet(model.getVertexMapping().values());
		
		// Perform a dfs through the internal model. If a cycle is found,
		// reverse it.
		Object rootsArray[] = null;
		
		if (model.roots != null)
		{
			Object[] modelRoots = model.roots.toArray();
			rootsArray = new Object[modelRoots.length];
			
			for (int i = 0; i < modelRoots.length; i++)
			{
				Object node = modelRoots[i];
				mxGraphHierarchyNode internalNode = (mxGraphHierarchyNode) model
						.getVertexMapping().get(node);
				rootsArray[i] = internalNode;
			}
		}

		model.visit(new mxGraphHierarchyModel.CellVisitor()
		{
			public void visit(Object parent, Object cell,
					Object connectingEdge, int layer, int seen)
			{
				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if (((mxGraphHierarchyNode) cell)
						.isAncestor((mxGraphHierarchyNode) parent))
				{
					((mxGraphHierarchyEdge) connectingEdge).invert();
					((mxGraphHierarchyNode) parent).connectsAsSource
							.remove(connectingEdge);
					((mxGraphHierarchyNode) parent).connectsAsTarget
							.add(connectingEdge);
					((mxGraphHierarchyNode) cell).connectsAsTarget
							.remove(connectingEdge);
					((mxGraphHierarchyNode) cell).connectsAsSource
							.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, rootsArray, true, null);

		Set possibleNewRoots = null;
		
		if (unseenNodes.size() > 0)
		{
			possibleNewRoots = new HashSet(unseenNodes);
		}
		
		// If there are any nodes that should be nodes that the dfs can miss
		// these need to be processed with the dfs and the roots assigned
		// correctly to form a correct internal model
		Set seenNodesCopy = new HashSet(seenNodes);
		
		// Pick a random cell and dfs from it
		model.visit(new mxGraphHierarchyModel.CellVisitor()
		{
			public void visit(Object parent, Object cell,
					Object connectingEdge, int layer, int seen)
			{
				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if (((mxGraphHierarchyNode) cell)
						.isAncestor((mxGraphHierarchyNode) parent))
				{
					((mxGraphHierarchyEdge) connectingEdge).invert();
					((mxGraphHierarchyNode) parent).connectsAsSource
							.remove(connectingEdge);
					((mxGraphHierarchyNode) parent).connectsAsTarget
							.add(connectingEdge);
					((mxGraphHierarchyNode) cell).connectsAsTarget
							.remove(connectingEdge);
					((mxGraphHierarchyNode) cell).connectsAsSource
							.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, unseenNodes.toArray(), true, seenNodesCopy);

		mxGraph graph = layout.getGraph();

		if (possibleNewRoots != null && possibleNewRoots.size() > 0)
		{
			Iterator iter = possibleNewRoots.iterator();
			List roots = model.roots;

			while (iter.hasNext())
			{
				mxGraphHierarchyNode node = (mxGraphHierarchyNode) iter.next();
				Object realNode = node.cell;
				int numIncomingEdges = graph.getIncomingEdges(realNode).length;

				if (numIncomingEdges == 0)
				{
					roots.add(realNode);
				}
			}
		}
	}
}
