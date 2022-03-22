/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.utilities.lists;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class CircularLinkedList<E> implements Iterable<E> {
	private Node<E> head;
	private Node<E> tail;

	/** Tracks the current node being traversed */
	private Node<E> current;

	@SafeVarargs
	public static <E> CircularLinkedList<E> of(E... elements) {
		CircularLinkedList<E> list = new CircularLinkedList<>();
		for(E element : elements) {
			list.append(element);
		}
		return list;
	}

	@SuppressWarnings("UnusedAssignment")
	public void append(E value) {
		Node<E> node = new Node<>(value);

		if(this.head == null) {
			this.head = node;
		} else {
			this.tail.next = node;
		}

		this.tail = node;
		this.tail.next = this.head;
	}

	public Optional<E> getCurrent() {
		return Optional.ofNullable(this.current).map(Node::getValue);
	}

	public Optional<E> next() {
		if(this.current == null) {
			this.current = this.head;
		} else {
			if(this.current == this.tail) {
				this.current = this.head;
			} else {
				this.current = this.current.next;
			}
		}

		return Optional.ofNullable(this.current.getValue());
	}

	public E nextOrThrow() {
		if(this.current == null) {
			this.current = this.head;
		} else {
			if(this.current == this.tail) {
				this.current = this.head;
			} else {
				this.current = this.current.next;
			}
		}

		if(this.current == null) {
			throw new IllegalStateException("No available element");
		}

		return this.current.getValue();
	}

	public List<E> getFramesNonCircular() {
		List<E> output = Lists.newArrayList();
		Node<E> working = this.head;
		while(working != null) {
			output.add(working.getValue());
			working = working.next;

			if(Objects.equals(this.head.getValue(), working.getValue())) {
				break;
			}
		}

		return output;
	}

	public boolean empty() {
		return this.getFramesNonCircular().isEmpty();
	}

	public int size() {
		return this.getFramesNonCircular().size();
	}

	public E at(int index) throws IndexOutOfBoundsException {
		if(index >= this.size() || index < 0) {
			throw new IndexOutOfBoundsException("" + index);
		}

		Node<E> node = this.head;
		for(int i = 0; i < index; i++) {
			node = node.next;
		}

		return node.getValue();
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return this.getFramesNonCircular().iterator();
	}

	public static <E> CircularLinkedList<E> fromStream(Stream<E> stream) {
		CircularLinkedList<E> result = new CircularLinkedList<>();
		stream.forEach(result::append);
		return result;
	}
}
