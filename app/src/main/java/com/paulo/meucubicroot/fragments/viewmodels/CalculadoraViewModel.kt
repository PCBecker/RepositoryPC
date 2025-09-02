package com.paulo.meucubicroot.fragments.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paulo.meucubicroot.utils.SingleLiveEvent
import paulo.com.meucubicroot.utils.GammaUtils

class CalculadoraViewModel : ViewModel() {

    private val TAG = "CalcViewModel"
    // LiveData para o visor de entrada (o que o usuário digita)
    private val _visor1Text = MutableLiveData<String>()
    val visor1Text: LiveData<String> get() = _visor1Text

    // LiveData para o visor de resultado/histórico (visor2)
    private val _visor2Text = MutableLiveData<String>()
    val visor2Text: LiveData<String> get() = _visor2Text
    private val _showErrorEvent = SingleLiveEvent<String>()

    init {
        // Inicializa os visores quando o ViewModel é criado
        _visor1Text.value = ""
        _visor2Text.value = ""
        Log.d(TAG, "ViewModel inicializado.")
    }

    fun appendOperator(operator: String) {
        val currentText = _visor1Text.value ?: ""
        val currentText1 = _visor2Text.value ?: ""

        if (currentText1.contains("=")){
            clearAllDisplays()
            _visor1Text.value = operator
        }else{
            if (currentText.isEmpty() && operator != "-"  && operator != "(") {
                // Não permite a maioria dos operadores no início, exceto '-' e '('
                return
            }
            // Lógica para substituir o último operador se for o caso
            else if (currentText.isNotEmpty() && currentText.last() == ')') {
                // Se o último é ')', e o novo é um operador aritmético, apenas adiciona.
                // Ex: "sin(45)" + "+" -> "sin(45)+"
                _visor1Text.value = currentText + operator
            } else {
                _visor1Text.value = currentText + operator
            }
        }

    }

    fun appendParenthesis(paren: String) {
        val currentText = _visor1Text.value ?: ""
        //_visor1Text.value = currentText + paren
        if (paren == "(") {
            // Se o último é um número ou ')', insere um '*' implícito antes do '('.
            // Aqui, a lógica é que (num)(...) ou (expr)(...) se tornem (num)*1*(...) ou (expr)*1*(...)
            if (currentText.isNotEmpty() && (currentText.last().isDigit() || currentText.last() == ')')) {
                _visor1Text.value = "$currentText*1*$paren" // <-- MUDANÇA AQUI PARA ADICIONAR *1*
            } else {
                _visor1Text.value = currentText + paren
            }
        } else { // paren == ")"
            // Lógica para fechar parênteses, permanece a mesma
            if (currentText.isNotEmpty() &&
                !setOf("+", "-", "*", "/", "%", "^", "√", "(").contains(currentText.last().toString())) {
                _visor1Text.value = currentText + paren
            }
        }
    }
    fun calculateGamma() {
        val currentValue = _visor1Text.value?.toDoubleOrNull()
        if (currentValue == null) {
            _showErrorEvent.value = "Entrada inválida para a Função Gama."
            return
        }
        try {
            val result = GammaUtils.gamma(currentValue) // <-- AQUI É FEITA A CHAMADA
            if (result.isNaN()) {
                _showErrorEvent.value = "A Função Gama não está definida para esta entrada."
                clearAllDisplays()
                return
            }

            _visor1Text.value = "$result"
            _visor2Text.value = "Γ($currentValue) = $result"

        } catch (e: Exception) {
            _showErrorEvent.value = "Erro no cálculo da Função Gama: ${e.message}"
            clearAllDisplays()
        }
    }
    fun calculateFactorial() {
        val currentValue = _visor1Text.value?.toDoubleOrNull()
        val n = currentValue
        n?.let {
            if (it >= 0.0){
                try {
                    val result = GammaUtils.factorialFractional(currentValue)
                    if (result.isNaN()) {
                        _showErrorEvent.value = "Fatorial não definido para esta entrada."
                        clearAllDisplays()
                        return
                    }
                    _visor1Text.value = "$result"
                    _visor2Text.value = "Fatorial($currentValue) = $result"// Representação no visor

                } catch (e: IllegalArgumentException) { // Captura exceções de argumentos inválidos
                    _showErrorEvent.value = e.message
                    clearAllDisplays()
                } catch (e: Exception) {
                    _showErrorEvent.value = "Erro no cálculo do Fatorial: ${e.message}"
                    clearAllDisplays()
                }

            }else{
                _visor2Text.value = "Entrada inválida para Fatorial."
                _visor2Text.value = ""
            }
        }

    }
    fun appendFunction(func: String) {
        val currentVisor1 = _visor1Text.value ?: ""
        val currentVisor2 = _visor2Text.value ?: ""

        if (currentVisor2.contains("=")){
            _visor1Text.value = func
            _visor2Text.value = ""
        }else{
            _visor1Text.value = currentVisor1 + func
        }
    }
    fun appendDot() {
        val currentText = _visor1Text.value ?: ""
        val parts = currentText.split("+", "-", "*", "/", "%", "^", "√", "(", ")")
        if (parts.lastOrNull()?.contains('.') == true) {
            // Já tem um ponto no último número/parte
            return
        }
        _visor1Text.value = "$currentText."
    }

    fun clearLastCharacter() {
        val currentText = _visor1Text.value ?: ""
        if (currentText.length > 1) {
            _visor1Text.value = currentText.substring(0, currentText.length - 1)
        } else {
            _visor1Text.value = "" // Se só tiver um caractere, volta para "0"
        }
    }

    fun clearAllDisplays() {
        _visor1Text.value = ""
        _visor2Text.value = ""
    }

    fun setResult(result: String, expression: String = "") {

        _visor2Text.value = "$expression = $result" // Exibe a expressão e o resultado no visor2
        _visor1Text.value = "Result = $result" // O resultado fica no visor1 para futuras operações

    }

    // Adicione esta função para lidar com o "=" no visor1 e visor2 quando o número for clicado
    fun handleNumberClick(number: String) {
        val currentVisor1 = _visor1Text.value ?: ""
        val currentVisor2 = _visor2Text.value ?: ""

        if (currentVisor2.contains("=")) {
            // Se já tem um resultado, limpa e começa uma nova entrada
            _visor1Text.value = ""
            _visor1Text.value = number
            _visor2Text.value = ""
        } else if (currentVisor1 == "0" && number != ".") {
            // Se o visor for apenas "0", substitui pelo número (a menos que seja um ponto)
            _visor1Text.value = number
        }else if (currentVisor1.isNotEmpty() && currentVisor1.last() == ')') {
            // Se o último caractere é um ')' (ex: sin(45)), assume multiplicação implícita
            _visor1Text.value = "$currentVisor1*1$number"
        }
        else {
            _visor1Text.value = currentVisor1 + number
        }
    }
}




