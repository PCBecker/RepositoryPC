package com.paulo.meucubicroot.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.paulo.meucubicroot.databinding.FragmentCalculadoraBinding
import com.paulo.meucubicroot.fragments.viewmodels.CalculadoraViewModel
import java.lang.Float.parseFloat
import java.text.DecimalFormat
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


class CalculadoraFragment : Fragment() {

    private var _binding: FragmentCalculadoraBinding? = null

    private val binding get() = _binding!!

    private lateinit var calculadoraViewModel: CalculadoraViewModel

    private lateinit var funcW: Button

    private lateinit var eqgrau3: Button

    private lateinit var eqgrau2: Button
    private lateinit var exp: Button
    private lateinit var x2: Button
    private lateinit var x3: Button
    private lateinit var visor1: TextView
    private lateinit var visor2: TextView
    private lateinit var inv: Button
    private lateinit var cub: Button
    private lateinit var fat: Button
    private lateinit var gama: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inicializa o binding aqui
        _binding = FragmentCalculadoraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eqgrau2 = binding.eqgrau2
        eqgrau3 = binding.eqgrau3
        funcW = binding.funcW
        exp = binding.exp
        x2 = binding.x2
        x3 = binding.x3
        visor1 = binding.visor1
        inv = binding.inv
        cub = binding.cub
        fat = binding.fat
        gama = binding.gama
        visor2 = binding.visor2

        // Inicializa o ViewModel (isso garante que o mesmo ViewModel sobreviva às rotações)
        calculadoraViewModel = ViewModelProvider(this)[CalculadoraViewModel::class.java]

        // Configura os observadores para LiveData
        setupObservers()
        setupNavigationButton()

        // Configura os listeners dos botões
        setupButtonListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência ao binding para evitar memory leaks
    }

    private fun setupButtonListeners() {
        binding.zero.setOnClickListener { calculadoraViewModel.handleNumberClick("0") }
        binding.one.setOnClickListener { calculadoraViewModel.handleNumberClick("1") }
        binding.two.setOnClickListener { calculadoraViewModel.handleNumberClick("2") }
        binding.three.setOnClickListener { calculadoraViewModel.handleNumberClick("3") }
        binding.four.setOnClickListener { calculadoraViewModel.handleNumberClick("4") }
        binding.five.setOnClickListener { calculadoraViewModel.handleNumberClick("5") }
        binding.six.setOnClickListener { calculadoraViewModel.handleNumberClick("6") }
        binding.seven.setOnClickListener { calculadoraViewModel.handleNumberClick("7") }
        binding.eight.setOnClickListener { calculadoraViewModel.handleNumberClick("8") }
        binding.nine.setOnClickListener { calculadoraViewModel.handleNumberClick("9") }

        // Operadores
        binding.soma.setOnClickListener { calculadoraViewModel.appendOperator("+") }
        binding.subtrac.setOnClickListener { calculadoraViewModel.appendOperator("-") }
        binding.multiplic.setOnClickListener { calculadoraViewModel.appendOperator("*") }
        binding.divide.setOnClickListener { calculadoraViewModel.appendOperator("/") }
        // Parênteses
        binding.botaoAbPar.setOnClickListener { calculadoraViewModel.appendParenthesis("(") }
        binding.botaoFechPar.setOnClickListener { calculadoraViewModel.appendParenthesis(")") }
        // Funções trigonométricas/matemáticas
        binding.sin.setOnClickListener { calculadoraViewModel.appendFunction("sin") }
        binding.cos.setOnClickListener { calculadoraViewModel.appendFunction("cos") }
        binding.tan.setOnClickListener { calculadoraViewModel.appendFunction("tan") }
        binding.ln.setOnClickListener { calculadoraViewModel.appendFunction("ln") }
        binding.sqrt.setOnClickListener { calculadoraViewModel.appendFunction("sqrt") }
        binding.exp.setOnClickListener {calculadoraViewModel.appendFunction("exp") }
        binding.x3.setOnClickListener { calculadoraViewModel.appendFunction("cubic") }
        binding.x2.setOnClickListener { calculadoraViewModel.appendFunction("quad") }
        binding.gama.setOnClickListener { calculadoraViewModel.calculateGamma() }
        binding.inv.setOnClickListener { calculadoraViewModel.appendFunction("inv") }
        binding.cub.setOnClickListener { calculadoraViewModel.appendFunction("cub") }
        binding.fat.setOnClickListener { calculadoraViewModel.calculateFactorial() }
        // Ponto decimal
        binding.ponto.setOnClickListener { calculadoraViewModel.appendDot() }
        // Botão de apagar último caractere (Clear - Backspace)
        binding.clear.setOnClickListener { calculadoraViewModel.clearLastCharacter() }
        binding.clearText.setOnClickListener {  calculadoraViewModel.clearAllDisplays()}
        // Botão de igual (onde a lógica de avaliação será executada)
        binding.igual.setOnClickListener {
            // Pegue a expressão do ViewModel, calcule e defina o resultado no ViewModel
            val expression = calculadoraViewModel.visor1Text.value ?: ""
            try {
                val result = eval(expression)
                val df = DecimalFormat("#.####")
                val resultFormatado = df.format(result)

                calculadoraViewModel.setResult(resultFormatado.toString(), expression) // Atualiza o ViewModel com o resultado

            } catch (_: Exception) {
                calculadoraViewModel.setResult("Invalid operation")
            }
        }
        binding.help.setOnClickListener { mostrarAlerta() }

        eqgrau2.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(EqG2Fragment(), "EQGRAU2")
        }
        binding.eqgrau3.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(EqG3Fragment(), "EQGRAU3")
        }
        binding.funcW.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(LambertFragment(), "Lambert")
        }
    }
    private fun setupNavigationButton(){

    }
    private fun setupObservers() {
        calculadoraViewModel.visor1Text.observe(viewLifecycleOwner) { text ->
            binding.visor1.text = text
        }
        calculadoraViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            binding.visor2.text = text
        }
    }
    private fun mostrarAlerta(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Instructions!").setMessage(
            "Teclas Numéricas (0-9, .)\n" +
                    "Usadas para inserir números e pontos decimais no visor da calculadora." +
                    "Operadores Básicos (+, -, *, /)\n" +
                    "Realizam as operações matemáticas fundamentais de adição, subtração, multiplicação e divisão." +
                    "Funções Exponenciais (e^x, x², X³)\n" +
                    "e^x: Calcula o número de Euler (e) elevado à potência do valor atual no visor (ex: e^5).\n" +
                    "\n" +
                    "x²: Calcula o quadrado do valor atual no visor (ex: 5² = 25).\n" +
                    "\n" +
                    "X³: Calcula o cubo do valor atual no visor (ex: 2³ = 8)." +
                    "Teclas de Limpeza e Sinal (AC, +/-)\n" +
                    "AC (All Clear): Limpa completamente todos os visores e reinicia qualquer expressão em andamento.\n" +
                    "\n" +
                    " Γ(x): Função gama Γ(n)=(n−1)! " +
                    " Fat(x) função fatorial" +
                    " Teclas de Navegação (Calc, Grau 2, Grau 3, Lambert W)\n" +
                    " Calc: Retorna calculadora.\n" +
                    "\n" +
                    "Grau 2 (ou EqGrau2): Navega para a seção de resolução de equações do 2º grau, onde você pode inserir coeficientes e visualizar o gráfico de parábola.\n" +
                    "\n" +
                    "Grau 3 (ou EqGrau3): Navega para a seção de resolução de equações do 3º grau, permitindo calcular raízes e exibir o gráfico da função cúbica.\n" +
                    "\n" +
                    "Lambert W (ou FuncW): Navega para a ferramenta da Função W de Lambert, uma função especial para equações exponenciais, com cálculo e visualização gráfica."
        )
            .setCancelable(true)
            .setPositiveButton("Ok"){ _, _ ->

            }
            .show()
    }
    // A função eval (e o objeto anônimo) pode permanecer como está,
    // já que ela faz o cálculo e não manipula as Views diretamente.
    private fun eval(str: String): Double {
        // ... (seu código da função eval permanece inalterado) ...
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }
            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }
            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }
            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }
            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x =
                        when (func) {
                            "sqrt" -> sqrt(x)
                            "sin" -> sin(Math.toRadians(x))
                            "cos" -> cos(Math.toRadians(x))
                            "tan" -> tan(Math.toRadians(x))
                            "log" -> log10(x)
                            "ln" -> ln(x)
                            "exp" -> exp(x)
                            "quad" -> x.pow(2)
                            "cubic" -> x.pow(3)
                            "inv" -> 1/x
                            "cub" -> cbrt(x)

                            else -> throw RuntimeException("Unknown function: $func")
                        }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.code)) x = x.pow(parseFactor())
                return x
            }
        }.parse()
    }

}


