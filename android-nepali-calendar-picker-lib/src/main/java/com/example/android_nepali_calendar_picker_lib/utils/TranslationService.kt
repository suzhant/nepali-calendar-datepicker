package com.example.android_nepali_calendar_picker_lib.utils

import java.util.*

object TranslationService {

    private val dictionary = mapOf(
        // numbers
        "0" to "०",
        "1" to "१",
        "2" to "२",
        "3" to "३",
        "4" to "४",
        "5" to "५",
        "6" to "६",
        "7" to "७",
        "8" to "८",
        "9" to "९",

    )

    private val weekDays = mapOf(
        //days
        "Sun" to "आइ",
        "Mon" to "सोम",
        "Tue" to "मंगल",
        "Wed" to "बुध",
        "Thr" to "बिहि",
        "Thu" to "बिहि",
        "Fri" to "शुक्र",
        "Sat" to "शनि",
        "Sunday" to "आइतबार",
        "Monday" to "सोमबार",
        "Tuesday" to "मंगलबार",
        "Wednesday" to "बुधबार",
        "Thursday" to "बिहिबार",
        "Friday" to "शुक्रबार",
        "Saturday" to "शनिबार",
        " " to " "
    )

    private val nepToEngDictionary = mapOf(
        // numbers
        "०" to "0",
        "१" to "1",
        "२" to "2",
        "३" to "3",
        "४" to "4",
        "५" to "5",
        "६" to "6",
        "७" to "7",
        "८" to "8",
        "९" to "9",
        )

    internal fun translateToEng(word: String):String{
        val translatedWords = mutableListOf<String>()
        var i = 0
        while (i < word.length) {
            var j = i + 1
            while (j <= word.length) {
                val subword = word.lowercase(Locale.getDefault()).substring(i, j)
                if (subword in nepToEngDictionary) {
                    translatedWords.add(nepToEngDictionary[subword]!!)
                    i = j
                    break
                }
                j += 1
            }
            if (j > word.length) {
                translatedWords.add(word.substring(i, i + 1))
                i += 1
            }
        }
        return translatedWords.joinToString("")
    }
    internal fun translateDays(word: String):String{
        return weekDays[word]!!
    }

    internal fun translate(word: String): String {
        val translatedWords = mutableListOf<String>()
        var i = 0
        while (i < word.length) {
            var j = i + 1
            while (j <= word.length) {
                val subword = word.lowercase(Locale.getDefault()).substring(i, j)
                if (subword in dictionary) {
                    translatedWords.add(dictionary[subword]!!)
                    i = j
                    break
                }
                j += 1
            }
            if (j > word.length) {
                translatedWords.add(word.substring(i, i + 1))
                i += 1
            }
        }
        return translatedWords.joinToString("")
    }

}