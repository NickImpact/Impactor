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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

	public static <E> CircularLinkedList<E> fromStream(Stream<E> stream) {
		CircularLinkedList<E> result = new CircularLinkedList<>();
		stream.forEach(result::append);
		return result;
	}

	public E current() {
		return this.current.value();
	}

	public E currentThenAdvance() {
		Node<E> node = this.current;
		this.current = this.current.next;
		return node.value();
	}

	public E next() {
		this.current = this.current.next;
		return this.current.value();
	}

	public void advanceTo(int index) {
		Preconditions.checkArgument(index >= 0 && index < this.size(), "Invalid index position");
		Node<E> target = this.head;
		for(int i = 1; i <= index; i++) {
			target = target.next;
		}

		this.current = target;
	}

	public void append(E value) {
		Node<E> node = new Node<>(value);

		if(this.head == null) {
			this.head = node;
			this.head.next = this.head;
			this.current = node;
		} else {
			this.tail.next = node;
		}

		this.tail = node;
		this.tail.next = this.head;
	}

	public List<E> asList() {
		List<E> output = Lists.newArrayList();
		Node<E> working = this.head;
		while(working != null) {
			output.add(working.value());
			working = working.next;

			if(Objects.equals(this.head.value(), working.value())) {
				break;
			}
		}

		return output;
	}

	public boolean empty() {
		return this.asList().isEmpty();
	}

	public int size() {
		return this.asList().size();
	}

	public E at(int index) throws IndexOutOfBoundsException {
		if(index >= this.size() || index < 0) {
			throw new IndexOutOfBoundsException("" + index);
		}

		Node<E> node = this.head;
		for(int i = 0; i < index; i++) {
			node = node.next;
		}

		return node.value();
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return this.asList().iterator();
	}

}
