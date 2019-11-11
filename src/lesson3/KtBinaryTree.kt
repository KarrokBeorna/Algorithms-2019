package lesson3

import java.lang.IllegalArgumentException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>>() : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null
    private var fromElement: T? = null
    private var toElement: T? = null

    private constructor(root: Node<T>?, fromElement: T?, toElement: T?) : this() {
        this.root = root
        this.fromElement = fromElement
        this.toElement = toElement
    }

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override var size = 0
        private set
        get() {
            var result = 0
            for (i in this)
                if (range(i))
                    result++
            return result
        }

    /**
     * T = O(1), M = O(1)
     */
    private fun range(value: T) =
        (fromElement == null || value >= fromElement!!) && (toElement == null || value < toElement!!)

    override fun add(element: T): Boolean {
        require(range(element))

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

    /**
     * Функция нахождения родителя удаляемого элемента
     * N - число вершин
     * T = O(logN), M = O(1)
     */
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
     *
     *  N - число вершин
     *  T = O(logN), M = O(1)
     */
    override fun remove(element: T): Boolean {
        val removable = find(element)

        if (removable != null && removable.value == element) {

            var remRight = removable.right
            var remLeft = removable.left

            fun replace(replaceable: Node<T>) {
                // Добавление перемещаемому узлу потомков
                replaceable.right = remRight
                replaceable.left = remLeft

                // Поиск родителя удаляемого элемента и замена ему потомка
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

                // Наименьший элемент правого поддерева
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

                // Наибольший элемент левого поддерева
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

                // Удаляемый - листик
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
        return closest != null && element.compareTo(closest.value) == 0 && range(closest.value)
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

        private var current: Node<T>? = null
        private val stack: Stack<Node<T>> = Stack()

        init {
            var unit = root
            while (unit != null) {
                stack.push(unit)
                unit = unit.left
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         * T = O(1), M = O(1)
         */
        override fun hasNext(): Boolean = stack.isNotEmpty()

        /**
         * Поиск следующего элемента
         * Средняя
         * N - число вершин
         * T = O(logN), M = O(logN)
         */
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()

            var last = stack.pop()
            val new = last

            if (last.right != null) {
                last = last.right
                while (last != null) {
                    stack.push(last)
                    last = last.left
                }
            }

            current = new
            return new.value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         * N - число вершин
         * T = O(logN), M = O(1)
         */
        override fun remove() {
            if (current != null) {

                // т.к. могут возникнуть проблемы с повторным добавлением узлов                 .
                // при удалении элемента (именно с заменой из правого поддерева,        .               .
                // потому что элемента левого поддерева уже нет в стеке,           .        .       .       .
                // и он не будет вызван методом next()), нужно очистить                     ^       ^
                // стек и добавить туда лишь заменяющий элемент, чтобы            (проблем нет)    (проблема)
                // при вызове next() он добавил правых потомков как будто впервые

                val remRight = current!!.right
                if (remRight?.left != null) {
                    var replaceable = remRight.left
                    while (replaceable!!.left != null) {
                        replaceable = replaceable.left
                    }
                    var temp = replaceable
                    while (temp != remRight) {
                        temp = stack.pop()
                    }
                    stack.push(replaceable)
                }
                remove(current!!.value)
            }
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     * T = O(1), M = O(1)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> = KtBinaryTree(root, fromElement, toElement)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     * T = O(1), M = O(1)
     */
    override fun headSet(toElement: T): SortedSet<T> = KtBinaryTree(root, null, toElement)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     * T = O(1), M = O(1)
     */
    override fun tailSet(fromElement: T): SortedSet<T> = KtBinaryTree(root, fromElement, null)

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
