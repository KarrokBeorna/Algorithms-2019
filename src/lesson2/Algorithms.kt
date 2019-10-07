@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.lang.Math.sqrt
import kotlin.math.floor

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {

    val temp = mutableListOf<Int>()

    File(inputName).readLines().forEach { temp.add(it.toInt()) }

    var buyIndex = 0
    var sellIndex = 1

    for (i in 1 until temp.size) {
        var j = i
        do {
            j--
            val sell = temp[sellIndex] - temp[buyIndex]
            val newSell = temp[i] - temp[j]

            if (sell < 0 && temp[j] < temp[buyIndex] || temp[j] <= temp[buyIndex] && newSell > sell) {
                sellIndex = i
                buyIndex = j
            }

        } while (j != buyIndex)
    }

    return Pair(buyIndex + 1, sellIndex + 1)
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {

    val listForFirst = mutableListOf<String>()
    var listForSecond = mutableListOf<String>()
    var answer = ""

    /**
     *  Создаем всевозможные комбинации из подряд идущих символов
     */
    for (i in -1..first.length - 2) {
        var tempStr = ""
        for (j in i + 1 until first.length) {
            tempStr = "$tempStr${first[j]}"
            listForFirst.add(tempStr)
        }
    }
    for (i in -1..second.length - 2) {
        var tempStr = ""
        for (j in i + 1 until second.length) {
            tempStr = "$tempStr${second[j]}"
            listForSecond.add(tempStr)
        }
    }

    /**
     *  Сортируем созданные ранее списки,
     *  чтобы наибольшие элементы были в самом начале
     */
    listForFirst.sortByDescending { it.length }
    listForSecond.sortByDescending { it.length }

    val tempListForSecond = listForSecond

    var i = 0

    /** Пока не найдем первое совпадение или не дойдем до последнего элемента первого списка:
     *  Пока длина элемента второго списка >= длины исследуемой строки:
     *  Если длины равны, то сравниваем на равенство, иначе удаляем этот элемент из второго списка;
     *  Если исследуемый индекс второго списка пока что меньше (размер второго списка - 1), то индекс увеличиваем;
     *  Увеличиваем индекс первого списка, приравниваем "старый" второй лист к "новому";
     *
     *  Собственно, мы избавляемся от длинных строк, с которыми уже не нужно
     *  сравниваться и сравниваемся лишь с себе подобными.
     *
     *  Почему он не хочет проходить тесты? Kappa...
     */
    while (answer == "" && i < listForFirst.size) {
        var indexSec = 0
        while (listForSecond[indexSec].length >= listForFirst[i].length) {
            if (listForFirst[i].length == listForSecond[indexSec].length) {
                if (listForFirst[i] == listForSecond[indexSec]) answer = listForFirst[i]
            } else {
                tempListForSecond.removeAt(indexSec)
            }
            if (indexSec != listForSecond.size - 1) indexSec++ else break
        }
        i++
        listForSecond = tempListForSecond
    }

    return answer
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {

    var answer = 1

    when {
        limit <= 1 -> return 0
        limit == 2 -> return 1
    }

    for (i in 3..limit) {
        var temp = 0
        val range = sqrt(i.toDouble()).toInt() + 1
        for (j in 2..range) {
            if (i % j != 0) {
                temp++
            } else {
                temp = 0
                break
            }
        }
        if (temp != 0) answer++
    }

    return answer
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}