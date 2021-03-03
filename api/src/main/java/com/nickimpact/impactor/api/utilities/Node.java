package com.nickimpact.impactor.api.utilities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node<E> {

	private E value;
	Node<E> next;

	Node(E value) {
		this.value = value;
	}

}
