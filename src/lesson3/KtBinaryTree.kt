package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    // Функция нахождения родителя удаляемого элемента
    private fun parent(node: Node<T>): Node<T> {
        var parent = root!!
        var next = if (node.value < parent.value) parent.left else parent.right
        while (next != node) {
            parent = next!!
            next = if (node.value < parent.value) parent.left else parent.right
        }
        return parent
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    override fun remove(element: T): Boolean {                          // N - число вершин
        val removable = find(element)                                   // T = O(logN)
                                                                        // M = O(1)
        if (removable != null && removable.value == element) {

            var remRight = removable.right
            var remLeft = removable.left

            fun replace(replaceable: Node<T>) {
                // Добавляем перемещаемому узлу потомков
                replaceable.right = remRight
                replaceable.left = remLeft

                // Находим родителя удаляемого элемента и заменяем ему потомка
                if (removable != root) {
                    val parentRem = parent(removable)
                    if (parentRem.right == removable) {
                        parentRem.right = replaceable
                    } else {
                        parentRem.left = replaceable
                    }
                } else root = replaceable
            }

            when {
                // Самый правый потомок левого поддерева
                removable.left != null -> {
                    var tempLeft = removable.left!!
                    var parent = tempLeft

                    while (tempLeft.right != null) {
                        parent = tempLeft
                        tempLeft = tempLeft.right!!
                    }

                    if (tempLeft == remLeft) {
                        remLeft = tempLeft.left
                    } else parent.right = tempLeft.left

                    replace(tempLeft)
                }

                // Самый левый потомок правого поддерева
                removable.right != null -> {
                    var tempRight = removable.right!!
                    var parent = tempRight

                    while (tempRight.left != null) {
                        parent = tempRight
                        tempRight = tempRight.left!!
                    }

                    if (tempRight == remRight) {
                        remRight = tempRight.right
                    } else parent.left = tempRight.right

                    replace(tempRight)
                }

                // Просто удаляем элемент, если у него нет потомков
                else -> if (removable != root) {
                    val parentRem = parent(removable)
                    if (parentRem.right == removable) {
                        parentRem.right = null
                    } else parentRem.left = null
                } else root = null
            }
        } else return false

        size--
        return true
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {
        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        override fun hasNext(): Boolean {
            // TODO
            throw NotImplementedError()
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        override fun next(): T {
            // TODO
            throw NotImplementedError()
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {
            // TODO
            throw NotImplementedError()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
