package com.paulo.meucubicroot.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.paulo.meucubicroot.databinding.FragmentEqG3Binding
import com.paulo.meucubicroot.fragments.viewmodels.EqG3ViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint

import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.pow

class EqG3Fragment : Fragment() {
    private var _binding: FragmentEqG3Binding? = null
    private val binding get() = _binding!!
    private val eqGrau3ViewModel: EqG3ViewModel by viewModels()
    private lateinit var visor1grau3: TextView
    private lateinit var visor2grau3: TextView
    private lateinit var voltar: Button
    private lateinit var graphView: GraphView
    private lateinit var calcular: Button
    private lateinit var indiceA: Button
    private lateinit var indiceB: Button
    private lateinit var indiceC: Button
    private lateinit var indiceD: Button
    private lateinit var grafico3G: Button
    private lateinit var help: Button
    private lateinit var sinal: Button
    private lateinit var clear: Button
    private lateinit var eqgrau2: Button
    private lateinit var ponto: Button
    private lateinit var funcW: Button
    private lateinit var clearText: Button
    // Listener para comunicação com a Activity (se ainda estiver a usar)
    private var listener: FragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractionListener) {
            listener = context
        } else {
            Log.e("Eqgrau3", "Activity must implement FragmentInteractionListener")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // Removido o '?' pois binding!! garante não nulo
        _binding = FragmentEqG3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        indiceA = binding.indiceA
        indiceB = binding.indiceB
        indiceC = binding.indiceC
        indiceD = binding.indiceD
        calcular = binding.calcular
        visor1grau3 = binding.visor1grau3
        visor2grau3 = binding.visor2grau3
        sinal = binding.sinal
        clear = binding.clear
        clearText = binding.clearText
        help = binding.help

        graphView = binding.graphEqgrau3
        grafico3G = binding.grafico3G
        voltar = binding.voltar
        clearText = binding.clearText
        ponto = binding.ponto
        eqgrau2 = binding.eqgrau2
        funcW = binding.funcW

        binding.indiceA.setOnClickListener { indiceA() }
        binding.indiceB.setOnClickListener { indiceB() }
        binding.indiceC.setOnClickListener { indiceC() }
        binding.indiceD.setOnClickListener { indiceD() }

        sinal.setOnClickListener { eqGrau3ViewModel.invertSignVisor1() }

        clear.setOnClickListener { eqGrau3ViewModel.clearLastCharacter() }

        clearText.setOnClickListener { eqGrau3ViewModel.clearAll() }

        help.setOnClickListener { eqGrau3ViewModel.oneHelpButtonClicked() }

        calcular.setOnClickListener {
            eqGrau3ViewModel.setIndiceA(eqGrau3ViewModel.indiceA.value ?: 0.0) // Força uma atualização
        }
        grafico3G.setOnClickListener {
            val a = eqGrau3ViewModel.indiceA.value ?: 0.0
            val b = eqGrau3ViewModel.indiceB.value ?: 0.0
            val c = eqGrau3ViewModel.indiceC.value ?: 0.0
            val d = eqGrau3ViewModel.indiceC.value ?: 0.0

            Log.d("EqGrau3Fragment", "Botão grafico3G clicado. Gerando gráfico com a=$a, b=$b, c=$c, d=$d")
            generateCubicGraph(a, b, c, d, graphView)
        }

        setupObservers()
    }

    private fun mostrarAlertaAjuda() {
        Log.d("Eqgrau3", "Função mostrarAlertaAjuda() chamada para exibir o diálogo.")
        val builder = AlertDialog.Builder(requireContext()) // Use requireContext() aqui
        builder.setTitle("Instruções!") // Título do Alerta
            .setMessage(
                "Digitar: o valor a, após clicar Indice A. O valor b, após clicar Indice B. " +
                        "O valor de c, após clicar Indice C. E o valor de d, após clicar Indice D. " +
                        "Exemplo: Para 1*X^3 + 6*X^2 + 11*X + 6 = 0, digite 1 e clique Indice A, " +
                        "depois 6 e clique Indice B, depois 11 e clique Indice C, e finalmente 6 e clique Indice D. " +
                        "Em seguida, clique em Calcular (X1=-1, X2=-2 e X3=-3) ou em Gráfico."
            ) // A MENSAGEM DO ALERTA
            .setCancelable(true)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun indiceA() {
        val value = visor1grau3.text.toString().toDoubleOrNull() ?: 0.0
        Log.d(
            "EqGrau3Fragment",
            "Função indiceA() chamada. Valor lido do visor: ${visor1grau3.text}, Convertido: $value"
        )
        visor2grau3.text = "" // Limpa o visor da equação para nova entrada
        visor1grau3.text = "" // Limpa o visor de entrada
        visor2grau3.append("a =   $value ; ") // Adiciona ao visor da equação
        eqGrau3ViewModel.setIndiceA(value)
        eqGrau3ViewModel.setVisor1Text("0") // Reseta visor de entrada para "0"
        Log.d(
            "EqGrau3Fragment",
            "Indice A definido no ViewModel para: ${eqGrau3ViewModel.indiceA.value}"
        )
    }
    private fun indiceB() {
        val value = visor1grau3.text.toString().toDoubleOrNull() ?: 0.0
        Log.d(
            "EqGrau3Fragment",
            "Função indiceB() chamada. Valor lido do visor: ${visor1grau3.text}, Convertido: $value"
        )
        visor2grau3.text = ""
        visor1grau3.text = ""
        visor2grau3.append("b =   $value ; ")
        eqGrau3ViewModel.setIndiceB(value) // CORRIGIDO: Chama setIndiceB
        eqGrau3ViewModel.setVisor1Text("0")
        Log.d(
            "EqGrau3Fragment",
            "Indice B definido no ViewModel para: ${eqGrau3ViewModel.indiceB.value}"
        )
    }

    private fun indiceC() {
        val value = visor1grau3.text.toString().toDoubleOrNull() ?: 0.0
        Log.d(
            "EqGrau3Fragment",
            "Função indiceC() chamada. Valor lido do visor: ${visor1grau3.text}, Convertido: $value"
        )
        visor2grau3.text = ""
        visor1grau3.text = ""
        visor2grau3.append("c =   $value ; ")
        eqGrau3ViewModel.setIndiceC(value) // CORRIGIDO: Chama setIndiceC
        eqGrau3ViewModel.setVisor1Text("0")
        Log.d(
            "EqGrau3Fragment",
            "Indice C definido no ViewModel para: ${eqGrau3ViewModel.indiceC.value}"
        )
    }
    private fun indiceD() {
        val value = visor1grau3.text.toString().toDoubleOrNull() ?: 0.0
        Log.d(
            "EqGrau3Fragment",
            "Função indiceD() chamada. Valor lido do visor: ${visor1grau3.text}, Convertido: $value"
        )
        visor2grau3.text = ""
        visor1grau3.text = ""
        visor2grau3.append("d =   $value ; ")
        eqGrau3ViewModel.setIndiceD(value) // CORRIGIDO: Chama setIndiceD
        eqGrau3ViewModel.setVisor1Text("0")
        Log.d(
            "EqGrau3Fragment",
            "Indice D definido no ViewModel para: ${eqGrau3ViewModel.indiceD.value}"
        )
    }

    private fun setupObservers() {
        eqGrau3ViewModel.result.observe(viewLifecycleOwner) { resultObject ->
            visor1grau3.text = resultObject.resultText // Agora 'resultObject' é EqGrau3Result

            Log.d(
                "EqGrau3Fragment",
                "Resultado final do ViewModel observado. Texto: ${resultObject.resultText}"
            )
        }
        eqGrau3ViewModel.result.observe(viewLifecycleOwner) { resultObject ->
            visor1grau3.text = resultObject.resultText
        }
        eqGrau3ViewModel.showHelpDialogEvent.observe(viewLifecycleOwner) {
            mostrarAlertaAjuda() // Chame a função no Fragment para mostrar o AlertDialog
        }
        eqGrau3ViewModel.visor1Text.observe(viewLifecycleOwner) { text ->

            visor1grau3.text = text
            Log.d("EqGrau3Fragment", "Visor de entrada/temp atualizado para: $text")
        }
        eqGrau3ViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            visor2grau3.text = text
            Log.d("EqGrau33Fragment", "Visor 2 (equação) atualizado para: $text")
        }
        drawInitialGraph() // Desenha o gráfico inicial
        setupNavigationButtons() // Chama a configuração dos botões de navegação
        setupNumberButtons()

    }
    private fun setupNumberButtons(){
        // Obtenha os botões usando binding
        val buttons = listOf(
            binding.zero, binding.one, binding.two, binding.three, binding.four,
            binding.five, binding.six, binding.seven, binding.eight, binding.nine, binding.ponto // Inclua o ponto decimal
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                val digit = (it as Button).text.toString() // Pega o texto do botão clicado
                handleNumberInput(digit)
            }
        }
    }
    private fun EqG3Fragment.handleNumberInput(digit: String) {
        val currentVisor1Text = eqGrau3ViewModel.visor1Text.value ?: ""
        val currentVisor2Text = eqGrau3ViewModel.visor2Text.value ?: ""

        if (currentVisor2Text.contains("X1") || currentVisor2Text.contains("Não é equação")) {
            eqGrau3ViewModel.clearAll() // Limpa tudo se um resultado anterior está sendo exibido
        }
// Se o visor de entrada é "0" e o dígito é "0", não faça nada (evita "000")
        if (currentVisor1Text == "0" && digit == "0") return

// Se o visor é "0" e o dígito não é um ponto, substitui "0"
        if (currentVisor1Text == "0" && digit != ".") {
            eqGrau3ViewModel.setVisor1Text(digit)
        } else {
            // Lógica para ponto decimal: só adiciona se já não houver um
            if (digit == ".") {
                if (!currentVisor1Text.contains(".")) {
                    eqGrau3ViewModel.setVisor1Text(currentVisor1Text + digit)
                }
            } else {
                eqGrau3ViewModel.setVisor1Text(currentVisor1Text + digit)
            }
        }// Limpa visores de resultados/equação se o usuário começar a digitar um novo número

    }
    private fun setupNavigationButtons() {
        binding.voltar.setOnClickListener {
            listener?.onLoadFragment(CalculadoraFragment(), "CALCULADORA")
        }
        binding.eqgrau2.setOnClickListener {
            listener?.onLoadFragment(EqG2Fragment(), "GRAU2")
        }
        binding.funcW.setOnClickListener {
            listener?.onLoadFragment(LambertFragment(), "Lambert")
        }
    }
    private fun drawInitialGraph() {

        graphView.removeAllSeries()
        graphView.title = "Equação: ax³ + bx² + cx + d = 0"
        graphView.titleTextSize = 50f
        graphView.titleColor = Color.WHITE

        val gridLabel = graphView.gridLabelRenderer
        gridLabel.numVerticalLabels = 7
        gridLabel.horizontalAxisTitle = "X"
        gridLabel.verticalAxisTitle = "Y"
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.verticalAxisTitleColor = Color.WHITE
        gridLabel.labelsSpace = 8
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
        gridLabel.textSize = 30f
        gridLabel.gridColor = Color.DKGRAY
        gridLabel.gridStyle = GridLabelRenderer.GridStyle.BOTH
        gridLabel.setHumanRounding(true)

        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.setMinX(-5.0)
        graphView.viewport.setMaxX(5.0)
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinY(-10.0)
        graphView.viewport.setMaxY(10.0)

        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true

    }
    private fun generateCubicGraph(a: Double,
                                   b: Double,
                                   c: Double,
                                   d: Double,
                                   graph: GraphView){

        graph.removeAllSeries()
        val series = LineGraphSeries<DataPoint>()
        val minXPlot = -10.0
        val maxXPlot = 10.0
        val numPoints = 500

        for (i in 0..numPoints) {
            val x = minXPlot + (i / numPoints.toDouble()) * (maxXPlot - minXPlot)
            // CORREÇÃO DA EQUAÇÃO: ax^3 + bx^2 + cx + d
            val y = a * x.pow(3) + b * x.pow(2) + c * x + d
            series.appendData(DataPoint(x, y), true, numPoints + 1)
        }

        graphView.addSeries(series)
        graph.addSeries(series)

        // Log.d("GraphDebug", "generateCubicGraph: Número de pontos = ${series.getValues().count()}")
        Log.d("GraphDebug", "generateCubicGraph: Viewport X: [$minXPlot, $maxXPlot]")

        series.color = Color.MAGENTA // Cor para o gráfico cúbico
        series.thickness = 6
        series.isDrawBackground = false
        series.backgroundColor = Color.argb(50, 255, 0, 255) // Roxo transparente

        graph.title = "Equação: ${(a)}x³ + ${(b)}x² + ${(c)}x + ${(d)}"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(minXPlot)
        graph.viewport.setMaxX(maxXPlot)

        val calculatedMinY = series.lowestValueY
        val calculatedMaxY = series.highestValueY
        val marginY = (calculatedMaxY - calculatedMinY) * 0.1
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(calculatedMinY - marginY)
        graph.viewport.setMaxY(calculatedMaxY + marginY)
    }
}

