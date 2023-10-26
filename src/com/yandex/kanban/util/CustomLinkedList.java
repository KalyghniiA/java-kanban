package com.yandex.kanban.util;

import com.yandex.kanban.exception.CustomListException;

import java.util.Objects;

public class CustomLinkedList<T> {
    int size = 0;
    Node<T> prev = null;
    Node<T> next = null;

    public int size() {
        return size;
    }

    public Node<T> add(T elem) {
        if (this.prev == null || this.next == null) {
            Node<T> node = new Node<>(null, null, elem);
            prev = node;
            next = node;
            size++;
            return node;
        } else {
            Node<T> oldNode = next;
            Node<T> newNode = new Node<>(oldNode, null, elem);
            oldNode.setNext(newNode);
            next = newNode;
            size++;
            return newNode;
        }
    }

    public void remove(Node<T> node) throws CustomListException {
        if (this.prev == null && this.next == null) {
            throw new CustomListException("Список пуст");
        }
        if (node.equals(this.prev)) {
            this.prev = node.getNext();
        }

        if (node.equals(this.next)) {
            this.next = node.getPrev();
        }

        node.removeNode();
        size--;
    }

    public Node<T> getFirst() throws CustomListException {
        if (this.prev == null) {
            throw new CustomListException("Список пуст");
        }

        return this.prev;
    }


    public static class Node<E> {
        private Node<E> prev = null;
        private Node<E> next = null;
        private E data = null;

        Node(Node<E> prev, Node<E> next, E data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        public Node<E> getPrev() {
            return prev;
        }

        public void setPrev(Node<E> prev) {
            this.prev = prev;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setNext(Node<E> next) {
            this.next = next;
        }

        public E getData() {
            return data;
        }

        public void removeNode() {

            if (this.prev == null && this.next == null) {
                return;
            } else if (this.prev == null) {
                this.next.setPrev(null);
            } else if (this.next == null) {
                this.prev.setNext(null);
            } else {
                this.prev.setNext(this.next);
                this.next.setPrev(this.prev);
            }

            this.prev = null;
            this.next = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(prev, node.prev) && Objects.equals(next, node.next) && Objects.equals(data, node.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, next, data);
        }
    }
}
