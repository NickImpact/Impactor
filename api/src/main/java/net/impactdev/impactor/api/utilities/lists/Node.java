package net.impactdev.impactor.api.utilities.lists;

public class Node<E> {

	private E value;
	Node<E> next;

	Node(E value) {
		this.value = value;
	}

	public E getValue() {
		return this.value;
	}

	public Node<E> getNext() {
		return this.next;
	}

	public void setValue(E value) {
		this.value = value;
	}

	public void setNext(Node<E> next) {
		this.next = next;
	}
}
