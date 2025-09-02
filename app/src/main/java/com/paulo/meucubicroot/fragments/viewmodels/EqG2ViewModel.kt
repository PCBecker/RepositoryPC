package com.paulo.meucubicroot.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jjoe64.graphview.series.DataPoint
import kotlin.math.pow
import kotlin.math.sqrt

data class EqG2Result(
    val x1: Double?,
    val x2: Double?,
    val graphPoints: List<DataPoint>,
    val graphMinX: Double,
    val graphMaxX: Double,
    val graphMinY: Double,
    val graphMaxY: Double,
    val resultText: String
)

class EqG2ViewModel: ViewModel() {
    private val _indiceA = MutableLiveData<Double>(0.0)
    val indiceA: LiveData<Double> = _indiceA

    private val _indiceB = MutableLiveData<Double>(0.0)
    val indiceB: LiveData<Double> = _indiceB

    private val _indiceC = MutableLiveData<Double>(0.0)
    val indiceC: LiveData<Double> = _indiceC

    // LiveData para os textos dos visores (se necessário para controle do ViewModel)
    private val _visor1Text = MutableLiveData<String>("0")
    val visor1Text: LiveData<String> = _visor1Text

    private val _visor2Text = MutableLiveData<String>("0.0*X^2 + (0.0)*X + 0.0 = 0")
    val visor2Text: LiveData<String> = _visor2Text

    // LiveData para expor os resultados da equação e os dados do gráfico para o Fragment
    private val _result = MediatorLiveData<EqG2Result>()
    val result: LiveData<EqG2Result> = _result

    init {
        // Adiciona as fontes para o MediatorLiveData. Sempre que 'a', 'b' ou 'c' mudarem,
        // o código dentro da lambda será executado.
        _result.addSource(indiceA) {
            calcular(it, indiceB.value, indiceC.value)
        }
        _result.addSource(indiceB) {
            calcular(indiceA.value, it, indiceC.value)
        }
        _result.addSource(indiceC) {
            calcular(indiceA.value, indiceB.value, it)
        }
        // Realiza um cálculo inicial para que o gráfico não esteja vazio ao iniciar
        calcular(0.0, 0.0, 0.0) // Exemplo: gera uma linha y = 0
    }
    fun clearLastCharacter() {
        val currentText = _visor1Text.value ?: ""
        if (currentText.length > 1) {
            _visor1Text.value = currentText.substring(0, currentText.length - 1)
        } else {
            _visor1Text.value = "" // Se só tiver um caractere, volta para "0"
        }
    }
    fun invertSignVisor1() {
        val currentText = _visor1Text.value ?: ""
        val numberToInvert = currentText.toDoubleOrNull()

        if (numberToInvert == null || currentText.isEmpty() || currentText == "Invalid operation") {
            _visor1Text.value = "Invalid operation"
        } else {
            val invertedNumber = -1 * numberToInvert
            // Para evitar -0.0, exibe como "0"
            _visor1Text.value = if (invertedNumber == -0.0) "0" else invertedNumber.toString()
        }
    }
    fun calcular(a: Double?, b: Double?, c: Double?) {
        val a = indiceA.value ?: 0.0
        val b = indiceB.value ?: 0.0
        val c = indiceC.value ?: 0.0

        if (a == null || b == null || c == null) {
            // CORREÇÃO: Atualiza o LiveData mesmo em caso de erro
            _result.value = EqG2Result(
                null, null, emptyList(),
                -10.0, 10.0, -20.0, 20.0,
                "Por favor, insira valores válidos para a, b e c."
            )
            _visor1Text.value = ""
            _visor2Text.value = "Entrada inválida"
            return
        }
        if (a == 0.0) {
            // CORREÇÃO: Atualiza o LiveData mesmo quando 'a' é zero
            _result.value = EqG2Result(
                null, null, emptyList(),
                -10.0, 10.0, -20.0, 20.0,
                "O coeficiente 'a' não pode ser zero para uma equação do 2º grau."
            )
            _visor1Text.value = ""
            _visor2Text.value = "Não é equação de 2º grau (a=0)"
            return
        }

        val delta = b.pow(2) - 4 * a * c
        var x1: Double? = null
        var x2: Double? = null
        var resultText = ""
        val graphPoints = mutableListOf<DataPoint>()

        if (delta >= 0) {
            x1 = (-b + sqrt(delta)) / (2 * a)
            x2 = (-b - sqrt(delta)) / (2 * a)
            resultText = "X1 = %.1f\nX2 = %.1f".format(x1, x2)
            _visor1Text.value = "X1 = ${x1}; X2 = ${x2}"
        } else {
            val rm = -b / (2 * a)
            val im = sqrt(-delta) / (2 * a)
            resultText = "X1 = %.2f + i(%.2f)\nX2 = %.2f - i(%.2f)".format(rm, im, rm, im)
            _visor1Text.value = "X1 = ${rm} + i(${im}); X2 = ${rm} - i(${im})"
        }
        _visor2Text.value = "${a}*x² + (${b})*x + $c = 0"

        // Gerar pontos para a parábola
        val rangeMinX = -10.0 // Intervalo padrão para plotagem
        val rangeMaxX = 10.0
        val numPoints = 500

        for (i in 0..numPoints) {
            val x = rangeMinX + (i / numPoints.toDouble()) * (rangeMaxX - rangeMinX)
            val y = a * x.pow(2) + b * x + c
            graphPoints.add(DataPoint(x, y))
        }
        // Ajustar os limites do gráfico dinamicamente com base nos pontos gerados
        val calculatedMinX = graphPoints.minOfOrNull { it.x } ?: rangeMinX
        val calculatedMaxX = graphPoints.maxOfOrNull { it.x } ?: rangeMaxX
        val calculatedMinY = graphPoints.minOfOrNull { it.y } ?: -20.0
        val calculatedMaxY = graphPoints.maxOfOrNull { it.y } ?: 20.0

        // Adicionar uma margem aos limites para melhor visualização
        val marginX = (calculatedMaxX - calculatedMinX) * 0.1
        val marginY = (calculatedMaxY - calculatedMinY) * 0.1

        // Atualiza o LiveData com todos os resultados e dados do gráfico
        _result.value = EqG2Result(
            x1, x2, graphPoints,
            calculatedMinX - marginX,
            calculatedMaxX + marginX,
            calculatedMinY - marginY,
            calculatedMaxY + marginY,
            resultText
        )
    }

    fun setIndiceA(value: Double) {
        _indiceA.value = value
    }

    fun setIndiceB(value: Double) {
        _indiceB.value = value
    }

    fun setIndiceC(value: Double) {
        _indiceC.value = value
    }

    // Funções para manipular o texto dos visores (se a UI os manipular diretamente)
    fun setVisor1Text(text: String) {
        _visor1Text.value = text
    }
    fun clearAll() {
        _indiceA.value = 0.0
        _indiceB.value = 0.0
        _indiceC.value = 0.0
        _visor1Text.value = "0" // Reseta para "0"
        _visor2Text.value = "0.0*X^2 + (0.0)*X + 0.0 = 0" // Reseta para a equação padrão
        // Limpa também o resultado do gráfico
        _result.value = EqG2Result(
            null, null, emptyList(),
            -10.0, 10.0, -20.0, 20.0,
            "" // Limpa o texto do resultado
        )
    }

}