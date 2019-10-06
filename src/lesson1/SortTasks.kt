@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortTimes(inputName: String, outputName: String) {
    val text = File(inputName).readLines()
    val days = mutableListOf<Pair<Int, String>>()
    val nights = mutableListOf<Pair<Int, String>>()
    val daysAns = mutableListOf<String>()
    val nightsAns = mutableListOf<String>()

    for (line in text) {
        if (Regex("""((\d\d):(\d\d):(\d\d)\s(AM|PM))""").matches(line)) {

            val timeAndSun = line.split(" ")
            val time = timeAndSun[0].split(":")
            val hours = (time[0]).toInt()
            val minutes = (time[1]).toInt()
            val seconds = (time[2]).toInt()

            if (hours !in 1..12 || minutes !in 0..59 || seconds !in 0..59) throw Exception("Неверный формат")

            if ("AM" in line) {
                days += Pair(hours % 12 * 3600 + minutes * 60 + seconds, line)
            } else nights += Pair(hours % 12 * 3600 + minutes * 60 + seconds, line)

        } else throw Exception("Неверный формат")
    }

    for (pair in days.sortedBy { it.first }) {
        daysAns.add(pair.second)
    }
    for (pair in nights.sortedBy { it.first }) {
        nightsAns.add(pair.second)
    }

    File(outputName).writeText((daysAns + nightsAns).joinToString("\n"))
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    val text = File(inputName).readLines()

    class Resident(var person: String, var street: String, var numHouse: Int)

    val temp = mutableListOf<Resident>()

    for (line in text) {
        if (Regex("""\S+\s\S+\s-\s\S+\s\d+""").matches(line)) {

            val parts = line.split(" ")
            temp.add(Resident(parts[0] + " " + parts[1], parts[3], parts[4].toInt()))

        } else throw Exception("Неверный формат")
    }

    val sorted = temp.sortedWith(compareBy(Resident::street, Resident::numHouse))
    val names = mutableListOf<String>()
    val answer = mutableListOf<String>()

    for (i in 0..sorted.size - 2) {
        names.add(sorted[i].person)
        if (sorted[i].street == sorted[i + 1].street && sorted[i].numHouse == sorted[i + 1].numHouse) {
            if (i != sorted.size - 2)
            else {
                names.add(sorted[i + 1].person)
                answer.add("${sorted[i].street} ${sorted[i].numHouse} - " + names.sorted().joinToString(", "))
            }
        } else {
            if (i != sorted.size - 2) {
                answer.add("${sorted[i].street} ${sorted[i].numHouse} - " + names.sorted().joinToString(", "))
                names.clear()
            } else {
                answer.add("${sorted[i].street} ${sorted[i].numHouse} - " + names.sorted().joinToString(", "))
                answer.add("${sorted[i + 1].street} ${sorted[i + 1].numHouse} - ${sorted[i + 1].person}")
            }
        }
    }
    File(outputName).writeText(answer.joinToString("\n"))
}


/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val answer = mutableListOf<Double>()
    File(inputName).readLines().forEach { answer.add(it.toDouble()) }
    /**0for (i in 1 until answer.size) {
        var repl = i - 1
        var count = 0
        while (answer[i] < answer[repl]) {
            if (repl > 0) {
                repl--
                count++
            } else {
                count++
                break
            }
        }
        if (count > 0) {
            answer.add(i - count, answer[i])
            answer.removeAt(i + 1)
        }
    }*/
    File(outputName).writeText(answer.sorted().joinToString("\n"))
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    val text = File(inputName).readLines()
    val nums = mutableListOf<Int>()
    val answer = mutableListOf<Int>()

    text.forEach { nums.add(it.toInt()) }

    val numCounts = nums.sorted().groupingBy { it }.eachCount()
    val max = numCounts.values.max() ?: 0
    val maxStr = numCounts.filter { it.value == max }.keys.min() ?: 0

    nums.forEach { if (it != maxStr) answer.add(it) }

    for (i in 1..max) answer.add(maxStr)

    File(outputName).writeText(answer.joinToString("\n"))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 18 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var count = 0
    var numSec = first.size
    var index = 0
    while (index < second.size && count < first.size) {
        if (count == first.size - 1 && index == second.size - 1) {
            second[index] = first[count]
            index++
        } else
            if (numSec == second.size) {
                second[index] = first[count]
                count++
                index++
            } else
                if (second[numSec]!! < first[count]) {
                    second[index] = second[numSec]
                    numSec++
                    index++
                } else {
                    second[index] = first[count]
                    count++
                    index++
                }
    }
}

