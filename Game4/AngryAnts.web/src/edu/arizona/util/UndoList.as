package edu.arizona.util {

	public class UndoList {

		// Elements of the linked list
		private var pointer:Node;
		private var allUndone:Boolean = false;

		private var iterator:Node;

		public function UndoList() {
			pointer = null;
		}

		public function add(toAdd:*):void {
			if (allUndone) {
				pointer = new Node(toAdd, null, null);
				allUndone = false;
			} else {
				pointer = new Node(toAdd, null, pointer);
				if (pointer.prev)
					pointer.prev.next = pointer;
			}
		}

		public function canUndo():Boolean {
			return pointer != null && !allUndone;
		}

		public function undo():* {
			if (canUndo()) {
				var result:* = pointer.object;
				if (pointer.prev) {
					pointer = pointer.prev;
				} else {
					allUndone = true;
				}
				return result;
			} else {
				return null;
			}
		}

		public function canRedo():Boolean {
			return pointer != null && (allUndone || pointer.next != null);
		}

		public function redo():* {
			if (canRedo()) {
				if (allUndone) {
					allUndone = false;
				} else {
					pointer = pointer.next;
				}
				return pointer.object;
			} else {
				return null;
			}
		}

		public function beginIteration():void {
			iterator = pointer;
			if (allUndone)
				iterator = null;
		}

		public function hasNext():Boolean {
			return iterator != null;
		}

		public function next():* {
			var result:* = iterator.object;
			iterator = iterator.prev;
			return result;
		}
	}
}

/**
 * Node elements of list above. Stores next node
 * and current node object.
 */
internal class Node {

	// Elements of a node
	private var obj:*;
	private var nex:Node;
	private var pre:Node;


	/**
	 * Node constructor. Define object and node next
	 */
	public function Node(o:*, next:Node, prev:Node) {

		obj = o;
		nex = next;
		pre = prev
	}


	/**
	 * Get next node in list.
	 */
	public function get next():Node {

		return nex;

	}


	/**
	 * Set next node in list
	 */
	public function set prev(n:Node):void {

		pre = n;

	}

	/**
	 * Get next node in list.
	 */
	public function get prev():Node {

		return pre;

	}


	/**
	 * Set next node in list
	 */
	public function set next(n:Node):void {

		nex = n;

	}


	/**
	 * Get current node's stored object.
	 */
	public function get object():* {

		return obj;

	}

}
