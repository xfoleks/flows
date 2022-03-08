package edu.xfolex.flows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownFlow = flow {
        val startValue = 10
        var currentValue = startValue
        emit(startValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        collectFlow()
        reduceFlow()
        foldFlow()
        flattenFlow()
        bufferFlow()
    }

    private fun collectFlow() {
        viewModelScope.launch {
            val count = countDownFlow
                .filter { time ->
                    time % 2 == 0
                }
                .map { time ->
                    time * time
                }
                .onEach { time ->
                    println(time)
                }
                .count {
                    it % 2 == 0
                }
            println("The count is: $count")
        }
    }

    private fun reduceFlow() {
        viewModelScope.launch {
            val reduceResult = countDownFlow
                .reduce { accumulator, value ->
                    accumulator + value
                }
            println("The count is: $reduceResult")
        }
    }

    private fun foldFlow() {
        viewModelScope.launch {
            val foldResult = countDownFlow
                .fold(100) { accumulator, value ->
                    accumulator + value
                }
            println("The result is: $foldResult")
        }
    }

    private fun flattenFlow() {
        val flowOne = flow {
            emit(1)
            delay(500L)
            emit(2)
        }

        viewModelScope.launch {
            flowOne.flatMapConcat { value ->
                flow {
                    emit(value + 1)
                    delay(500L)
                    emit(value + 2)
                }
            }.collect { value ->
                println("The value is: $value")
            }
        }
    }

    private fun bufferFlow() {
        val flow = flow {
            delay(200L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }

        viewModelScope.launch {
            flow.onEach {
                println("$it is delivered")
            } .collect {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it" )
            }
        }
    }
}