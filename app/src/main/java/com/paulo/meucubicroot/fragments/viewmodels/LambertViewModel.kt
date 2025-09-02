package com.paulo.meucubicroot.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

data class LambertResult(val originalX: Double, val w0: Double, val w_1: Double?)

class LambertViewModel: ViewModel() {
    private val _result = MutableLiveData<LambertResult>()
    val result: LiveData<LambertResult> = _result

    private val _visor1Text = MutableLiveData<String>()
    val visor1Text: LiveData<String> = _visor1Text

    private val _visor2Text = MutableLiveData<String>()
    val visor2Text: LiveData<String> = _visor2Text

    init {
        _visor1Text.value = ""
        _visor2Text.value = ""
    }
    fun clearLastCharacter() {
        val currentText = _visor1Text.value ?: ""
        if (currentText.length > 1) {
            _visor1Text.value = currentText.substring(0, currentText.length - 1)
        } else {
            _visor1Text.value = "" // Se só tiver um caractere, volta para "0"
        }
    }

    /**
     * Limpa completamente ambos os visores.
     */
    fun clearAll() {
        _visor1Text.value = "0"
        _visor2Text.value = ""
    }

    fun invertSignVisor1() {
        val currentText = _visor1Text.value ?: "0"
        if (currentText.startsWith("-")) {
            _visor1Text.value = currentText.substring(1)
        } else {
            _visor1Text.value = "-$currentText"
        }
    }
    fun handleNumberInput(digit: String) {
        val currentVisor1Text = _visor1Text.value ?: ""
        val currentVisor2Text = _visor2Text.value ?: ""

        if (currentVisor2Text.contains("W") || currentVisor2Text.contains("Não é equação")) {
            _visor1Text.value = ""
            _visor2Text.value = ""
        }

        if (currentVisor1Text == "0" && digit == "0") return

        if (currentVisor1Text == "0" && digit != ".") {
            setVisor1Text(digit)
        } else {
            if (digit == ".") {
                if (!currentVisor1Text.contains(".")) {
                    setVisor1Text(currentVisor1Text + digit)
                }
            } else {
                setVisor1Text(currentVisor1Text + digit)
            }
        }
    }

    fun setVisor1Text(text: String) {
        _visor1Text.value = text
    }
    /**
     * Função principal que recebe 'x' e decide qual ramificação(ões) calcular.
     * NOVO: Agora passa o originalX para o LambertResult.
     */
    fun calculateLambert(x: Double?) {
        val oneOverE = -1.0 / E
        _visor1Text.value = ""

        if (x == null || x < oneOverE) {
            // Se a entrada for inválida ou fora do domínio real, define resultados como NaN.
            // E passa o x original para o resultado.
            _result.value = LambertResult(x ?: Double.NaN, Double.NaN, Double.NaN)
            return
        }
        val epsilon = 1e-9
        val maxIterations = 100

        val w0Result = lambertW(x, 0, epsilon, maxIterations)
        val w_1Result = if (x >= oneOverE && x < 0) {
            lambertW(x, -1, epsilon, maxIterations)
        } else {
            null
        }
        // Passa o x original para o resultado.
        _result.value = LambertResult(x, w0Result, w_1Result)
    }
    public fun lambertW(x: Double, branch: Int, epsilon: Double, maxIterations: Int): Double {
        val oneOverE = -1.0 / E

        if (x < oneOverE) {
            return Double.NaN
        }
        if (x == oneOverE && branch == -1) {
            return -1.0
        }
        var w: Double = when (branch) {
            0 -> when {
                x < 1 -> x
                else -> ln(x)
            }
            -1 -> when {
                x < -0.2 -> -0.5
                else -> -ln(-x)
            }
            else -> return Double.NaN
        }
        var iteration = 0
        while (iteration < maxIterations) {
            val ew = exp(w)
            val wew = w * ew
            val wew1 = wew + ew
            val delta = (wew - x) / (wew1 - (w + 2.0) * (wew - x) / (2.0 * wew1))

            w -= delta

            if (abs(delta) < epsilon) {
                return w
            }
            iteration++
        }
        return Double.NaN
    }
}