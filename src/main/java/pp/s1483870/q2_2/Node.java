package pp.s1483870.q2_2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Node in a Control Flow Graph. */
public class Node {
	/** Node number; used to distinguish nodes. */
	private final int nr;
	/** String ID for nodes, mainly for human readability. */
	private String id;
	/** Set of nodes to which there is an edge from this node. */
	private final Set<Node> edges;
	/** Set of target node nrs, used for equality check. */
	private final Set<String> targetIds;

	/** Creates a node with a given number and ID. */
	public Node(int nr, String id) {
		assert id != null;
		this.nr = nr;
		this.id = id;
		this.edges = new LinkedHashSet<>();
		this.targetIds = new LinkedHashSet<>();
	}

	/** Returns the number of this node. */
	public int getNr() {
		return this.nr;
	}

	/** Sets the node identifier. */
	public void setId(String id) {
		this.id = id;
	}

	/** Returns the node identifier. */
	public String getId() {
		return this.id;
	}

	/** Adds an outgoing edge to a given target node. */
	public boolean addEdge(Node target) {
		boolean result = this.edges.add(target);
		this.targetIds.add(target.getId());
		return result;
	}

	/** Returns the set of outgoing edge targets. */
	public Set<Node> getEdges() {
		return this.edges;
	}

	@Override
	public String toString() {
		if (this.edges.isEmpty()) {
			return "Node " + this.id + ": no outgoing edges";
		} else {
			List<String> targetIds = new ArrayList<>();
			for (Node t : this.edges) {
				targetIds.add(t.id);
			}
			return "Node " + this.id + ": edges to " + targetIds;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id.hashCode();
		// don't use the edges for the hash code for comparison, as
		// they may yet change
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (!this.id.equals(other.id)) {
			return false;
		}
		if (!this.targetIds.equals(other.targetIds)) {
			return false;
		}
		return true;
	}
}
