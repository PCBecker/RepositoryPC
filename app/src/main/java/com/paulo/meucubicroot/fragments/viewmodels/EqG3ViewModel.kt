package com.paulo.meucubicroot.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paulo.meucubicroot.utils.SingleLiveEvent
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class EqGrau3Result( // Crie esta data class, se ainda não tiver
    val x1: Double?, // Raízes da cúbica
    val x2: Double?,
    val x3: Double?,
    val resultText: String // <--- Esta propriedade é essencial!
)
class EqG3ViewModel: ViewModel() {
    private val _indiceA = MutableLiveData<Double>(0.0)
    val indiceA: LiveData<Double> = _indiceA

    private val _indiceB = MutableLiveData<Double>(0.0)
    val indiceB: LiveData<Double> = _indiceB

    private val _indiceC = MutableLiveData<Double>(0.0)
    val indiceC: LiveData<Double> = _indiceC

    private val _indiceD = MutableLiveData<Double>(0.0)
    val indiceD: LiveData<Double> = _indiceD
    private val _result = MediatorLiveData< EqGrau3Result>()

    val result: LiveData<EqGrau3Result> = _result

    private val _delta = MutableLiveData<Double>(0.0)

    private val _x1 = MutableLiveData<String>("") // Usar String para X1/X2 por causa de números complexos

    private val _visor1Text = MutableLiveData<String>("")
    val visor1Text: LiveData<String> = _visor1Text

    private val _visor2Text = MutableLiveData<String>("0.0*X^3 + 0.0*X^2 + (0.0)*X + 0.0 = 0")
    val visor2Text: LiveData<String> = _visor2Text

    private val _x2 = MutableLiveData<String>("")

    private val _x3 = MutableLiveData<String>("")
    private val _showHelpDialogEvent = SingleLiveEvent<Unit>()
    val showHelpDialogEvent: LiveData<Unit> = _showHelpDialogEvent

    init {
        // Adicione _indiceD como fonte para o MediatorLiveData
        _result.addSource(indiceA) {
            calcular(it, indiceB.value,indiceC.value, indiceD.value)
        }
        _result.addSource(indiceB){
            calcular(indiceA.value, it, indiceC.value, indiceD.value)
        }
        _result.addSource(indiceC){
            calcular(indiceA.value, indiceB.value, it, indiceD.value)
        }
        _result.addSource(indiceD) {
            calcular(indiceA.value, indiceB.value, indiceC.value, it)
        }
        // Adapte calculateAndSetResult para 4 parâmetros
        calcular(0.0, 0.0, 0.0, 0.0)
    }
    fun oneHelpButtonClicked(){
        _showHelpDialogEvent.call()
    }
    fun clearLastCharacter() {
        val currentText = _visor1Text.value ?: ""
        if (currentText.length > 1) {
            _visor1Text.value = currentText.substring(0, currentText.length - 1)
        } else {
            _visor1Text.value = "" // Se só tiver um caractere, volta para "0"
        }
    }
    fun clearAll() { // Ou como você preferir chamar para limpar só o de entrada
        _indiceA.value = 0.0
        _indiceB.value = 0.0
        _indiceC.value = 0.0
        _visor1Text.value = ""
        _visor2Text.value = ""
        _delta.value = 0.0
        _x1.value = ""
        _x2.value = ""
        _x3.value = ""
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
    fun calcular(value: Double?, value1: Double?, value2: Double?, d1: Double?) {
        val a = indiceA.value ?: 0.0
        val b = indiceB.value ?: 0.0
        val c = indiceC.value ?: 0.0
        val d = indiceD.value ?: 0.0

        if (a == 0.0) {
            _visor2Text.value = "Não é equação de 3º grau (a=0)"
            _x1.value = ""
            _x2.value = ""
            return
        }
        val p = c / (3 * a) - b.pow(2) / (9 * a.pow(2))
        val q = b * c / (6 * a.pow(2)) - b.pow(3) / (27 * a.pow(3)) - d / (2 * a)
        val delta3g = p.pow(3) + q.pow(2)

        if (delta3g>=0){
            val x1 = (cbrt(q+ sqrt(delta3g)) + cbrt(q- sqrt(delta3g)) -b/(3*a)).roundToInt().toDouble()
            val disc3g =(b/a + x1).pow(2) + 4*d/(a*x1)

            if (disc3g>=0){
                val x2  = (-(b/a+x1)- sqrt(disc3g))/2
                val x3  = (-(b/a+x1)+ sqrt(disc3g))/2
                _x1.value = x1.toString()
                _x2.value = x2.toString()
                _x3.value = x3.toString()
                _visor1Text.value = ""
                _visor1Text.value = "X1 = $x1;  X2 = $x2;  X3 = $x3"
                _visor2Text.value = "$a*X^3 + (${b})*X^2 + ($c)*X + ($d) = 0 "
            }else{
                val rm = -(b/a + x1)/2
                val im = sqrt(-disc3g)/2
                if (rm==0.0){
                    _visor1Text.value =""
                    _visor1Text.value = "X1 = $x1;  X2 =   i($im);  X3 =  - i($im)"
                    _visor2Text.value = "$a*X^3+($b)*X^2+($c)*X+($d) = 0 "
                }else{
                    _visor1Text.value =""
                    _visor1Text.value = "X1 = $x1;  X2 = $rm + i($im);  X3 = $rm - i($im)"
                    _visor2Text.value = "$a*X^3+($b)*X^2+($c)*X+($d) = 0 "
                }
            }
        }else {
            val x1 = 2* sqrt(-p)* cos(acos((1/3)*q/ sqrt(-p.pow(3))))-b/(3*a).roundToInt().toDouble()
            val disc3g =(b/a + x1.roundToInt()).pow(2) + 4*d/(a*x1.roundToInt())

            if (disc3g>=0){
                val x2  = (-(b/a+x1)- sqrt(disc3g))/2
                val x3  = (-(b/a+x1)+ sqrt(disc3g))/2
                _visor1Text.value = ""
                _visor1Text.value = "X1 = ${x1};  X2 = $x2;  X3 = $x3"
                _visor2Text.value = "${a}x³ + ${b}x² + ${c}x + $d = 0 "
            }else{
                val rm = -(b/a + x1)/2
                val im = sqrt(-disc3g)/2

                if (rm==0.0){
                    _visor1Text.value =""
                    _visor1Text.value = "X1 = $x1;  X2 =   i($im);  X3 =  - i($im)"
                    _visor2Text.value = "${a}x³ + ${b}x² + ${c}x + $d = 0 "
                }else{
                    _visor1Text.value=""
                    _visor1Text.value = "X1 = $x1;  X2 = $rm + i($im);  X3 = $rm - i($im)"
                    _visor2Text.value = "${a}x³ + ${b}x² + ${c}x + $d = 0 "
                }
            }
        }
    }
    // Funções para atualizar os valores
    fun setIndiceA(value: Double) {
        _indiceA.value = value
    }
    fun setIndiceB(value: Double) {
        _indiceB.value = value
    }
    fun setIndiceC(value: Double) {
        _indiceC.value = value
    }
    fun setIndiceD(value: Double) {
        _indiceD.value = value
    }
    fun setVisor1Text(text: String) {
        _visor1Text.value = text }
}
