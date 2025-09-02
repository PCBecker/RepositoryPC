package com.paulo.meucubicroot.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.paulo.meucubicroot.R
import com.paulo.meucubicroot.databinding.FragmentLambertBinding
import com.paulo.meucubicroot.fragments.viewmodels.LambertViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.getValue
import kotlin.math.E

class LambertFragment : Fragment() {
    private var _binding: FragmentLambertBinding? = null
    private val binding get() = _binding!!
    private val lambertViewModel: LambertViewModel by viewModels()
    // Referência ao GraphView
    private lateinit var graphView: GraphView
    private lateinit var help: Button
    private lateinit var eqgrau2: Button
    private lateinit var eqgrau3: Button
    private lateinit var backepace: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLambertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializa o GraphView
        graphView = binding.graphLambert
        help = binding.help
        eqgrau2 = binding.eqgrau3
        eqgrau3 = binding.eqgrau3
        backepace = binding.backspace

        help.setOnClickListener { mostrarAlertaSimples() }

        setupButtons()
        setupObservers()
        // Desenha um gráfico inicial vazio ou com um range padrão
        // para que o usuário veja o GraphView ao abrir o fragmento.
        drawInitialGraph()
    }

    /**
     * Configura todos os listeners de botões (números, cálculo, navegação, etc.).
     */
    private fun setupButtons() {
        setupNumberButtons()
        setupNavigationButtons()
        setupUtilityButtons()

        binding.calcular.setOnClickListener {
            val x = binding.visor1Lambert.text.toString().toDoubleOrNull()
            lambertViewModel.calculateLambert(x)
        }
    }
    private fun setupNumberButtons() {
        val buttons = listOf(
            binding.zero, binding.one, binding.two, binding.three, binding.four,
            binding.five, binding.six, binding.seven, binding.eight, binding.nine, binding.ponto
        )
        buttons.forEach { button ->
            button?.setOnClickListener { view ->
                val digit = (view as Button).text.toString()
                lambertViewModel.handleNumberInput(digit)
            }
        }
    }
    private fun setupNavigationButtons() {
        binding.voltar.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(CalculadoraFragment(), "CALCULADORA")
        }
        binding.eqgrau2.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(EqG2Fragment(), "EQGRAU2")
        }
        binding.eqgrau3.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(EqG3Fragment(), "EQGRAU3")
        }
    }
    private fun setupUtilityButtons() {
        binding.clearText.setOnClickListener {
            lambertViewModel.clearAll()
            drawInitialGraph() // Redesenha o gráfico inicial ao limpar
        }
        binding.sinal.setOnClickListener {
            lambertViewModel.invertSignVisor1()
        }
        binding.backspace.setOnClickListener { lambertViewModel.clearLastCharacter() }
        // Listener para o botão de ajuda
        val meuBotaoAlerta: Button = view?.findViewById(R.id.help) ?: return@setupUtilityButtons
        meuBotaoAlerta.setOnClickListener {
            mostrarAlertaSimples()
        }
    }
    private fun setupObservers() {
        // Observador para o visor de entrada (visor1Lambert)
        lambertViewModel.visor1Text.observe(viewLifecycleOwner) { text ->
            binding.visor1Lambert.text = text
        }

        // Observador para o visor de saída (visor2Lambert)
        lambertViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            binding.visor2Lambert.text = text
        }

        // Observador para o resultado final do cálculo
        lambertViewModel.result.observe(viewLifecycleOwner) { result ->
            // NOVO: Usa result.originalX para formatar a string de saída.
            val displayedX = result.originalX

            if (result.w0.isNaN()) {
                binding.visor2Lambert.text = "Resultado inválido"
                binding.visor1Lambert.text = ""
                // Limpa o gráfico ou exibe uma mensagem de erro no gráfico se o resultado for inválido
                graphView.removeAllSeries()
                graphView.title = "Domínio Inválido"
                graphView.titleColor = Color.RED
            } else {
                // Atualiza o visor de texto com os resultados
                binding.visor1Lambert.text = ""
                if (result.w_1 != null && !result.w_1.isNaN()) {
                    binding.visor2Lambert.text = "W₀(${displayedX}) = %.6f\nW₋₁(${displayedX}) = %.6f".format(result.w0, result.w_1)
                } else {
                    binding.visor2Lambert.text = "W₀(${displayedX}) = %.6f".format(result.w0)
                }
                // Desenha o gráfico com os resultados
                generateLambertGraph(displayedX, result.w0, result.w_1, graphView)
            }
        }
    }
    private fun drawInitialGraph() {
        graphView.removeAllSeries() // Limpa qualquer série anterior
        graphView.title = "Função W de Lambert"
        graphView.titleColor = Color.WHITE
        graphView.titleTextSize = 40f

        val gridLabel = graphView.gridLabelRenderer
        gridLabel.horizontalAxisTitle = "X"
        gridLabel.verticalAxisTitle = "W(X)"
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.verticalAxisTitleColor = Color.WHITE
        gridLabel.labelsSpace = 8
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
        gridLabel.textSize = 30f
        gridLabel.gridColor = Color.DKGRAY
        gridLabel.gridStyle = GridLabelRenderer.GridStyle.BOTH
        gridLabel.setHumanRounding(false)

        // Configura o viewport para focar na região de interesse [-0.4, 0.1]
        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.setMinX(-0.4) // CORRIGIDO: Usando setMinX()
        graphView.viewport.setMaxX(1.1)  // CORRIGIDO: Usando setMaxX()
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinY(-10.0) // CORRIGIDO: Usando setMinY()
        graphView.viewport.setMaxY(1.5)  // CORRIGIDO: Usando setMaxY()

        // Habilita zoom e scroll
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true

        // Desenha a ramificação principal (W0)
        val seriesW0 = LineGraphSeries<DataPoint>()
        val minXPlot = -0.36787944117 // Aproximadamente -1/e
        val maxXPlot = 5.0 // Manter uma faixa mais ampla para a curva, mas o viewport foca
        val numPoints = 200

        for (i in 0..numPoints) {
            val x = minXPlot + (i / numPoints.toDouble()) * (maxXPlot - minXPlot)
            // Usa o lambertW do ViewModel para gerar os pontos
            val w = lambertViewModel.lambertW(x, 0, 1e-9, 100)
            if (!w.isNaN()) {
                seriesW0.appendData(DataPoint(x, w), true, numPoints + 1)
            }
        }
        seriesW0.color = Color.CYAN
        seriesW0.thickness = 6
        graphView.addSeries(seriesW0)

        // Desenha a ramificação inferior (W-1) para o intervalo [-1/e, 0)
        val seriesW1 = LineGraphSeries<DataPoint>()
        val minXPlotW1 = -0.36787944117 // Aproximadamente -1/e
        val maxXPlotW1 = -0.0001 // Próximo de zero, para evitar divisão por zero em ln(-x)
        val numPointsW1 = 100

        for (i in 0..numPointsW1) {
            val x = minXPlotW1 + (i / numPointsW1.toDouble()) * (maxXPlotW1 - minXPlotW1)
            val w = lambertViewModel.lambertW(x, -1, 1e-9, 100)
            if (!w.isNaN()) {
                seriesW1.appendData(DataPoint(x, w), true, numPointsW1 + 1)
            }
        }
        seriesW1.color = Color.MAGENTA // Cor diferente para a ramificação W-1
        seriesW1.thickness = 6
        graphView.addSeries(seriesW1)    }

    /**
     * Gera e exibe o gráfico da função W de Lambert com os resultados calculados.
     * @param inputX O valor de X que o usuário inseriu.
     * @param w0 O valor calculado para W0(inputX).
     * @param w_1 O valor calculado para W-1(inputX), pode ser nulo.
     * @param graph O GraphView onde o gráfico será desenhado.
     */
    private fun generateLambertGraph(inputX: Double, w0: Double, w_1: Double?, graph: GraphView) {
        graph.removeAllSeries() // Limpa qualquer série anterior

        // Configurações visuais do gráfico
        graph.title = "Função W de Lambert"
        graph.titleColor = Color.WHITE
        graph.titleTextSize = 40f

        val gridLabel = graph.gridLabelRenderer
        gridLabel.horizontalAxisTitle = "X"
        gridLabel.verticalAxisTitle = "W(X)"
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.verticalAxisTitleColor = Color.WHITE
        gridLabel.labelsSpace = 8
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
        gridLabel.textSize = 30f
        gridLabel.gridColor = Color.DKGRAY
        gridLabel.gridStyle = GridLabelRenderer.GridStyle.BOTH
        gridLabel.setHumanRounding(false)

        // Configura o viewport para focar na região de interesse [-0.4, 0.1]
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(-0.4) // CORRIGIDO: Usando setMinX()
        graph.viewport.setMaxX(0.1)  // CORRIGIDO: Usando setMaxX()
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(-1.0) // CORRIGIDO: Usando setMinY()
        graph.viewport.setMaxY(0.5)  // CORRIGIDO: Usando setMaxY()

        // Habilita zoom e scroll
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        // Desenha a ramificação principal (W0)
        val seriesW0 = LineGraphSeries<DataPoint>()
        val minXPlot = -0.36787944117 // Aproximadamente -1/e
        val maxXPlot = 5.0 // Manter uma faixa mais ampla para a curva, mas o viewport foca
        val numPoints = 200

        for (i in 0..numPoints) {
            val x = minXPlot + (i / numPoints.toDouble()) * (maxXPlot - minXPlot)
            // Usa o lambertW do ViewModel para gerar os pontos
            val w = lambertViewModel.lambertW(x, 0, 1e-9, 100)
            if (!w.isNaN()) {
                seriesW0.appendData(DataPoint(x, w), true, numPoints + 1)
            }
        }
        seriesW0.color = Color.CYAN
        seriesW0.thickness = 6
        graph.addSeries(seriesW0)

        // Desenha a ramificação inferior (W-1) para o intervalo [-1/e, 0)
        if (inputX >= -1.0 / E && inputX < 0) { // Só desenha se estiver no domínio da W-1
            val seriesW1 = LineGraphSeries<DataPoint>()
            val minXPlotW1 = -0.36787944117 // Aproximadamente -1/e
            val maxXPlotW1 = -0.0001 // Próximo de zero, para evitar divisão por zero em ln(-x)
            val numPointsW1 = 100

            for (i in 0..numPointsW1) {
                val x = minXPlotW1 + (i / numPointsW1.toDouble()) * (maxXPlotW1 - minXPlotW1)
                val w = lambertViewModel.lambertW(x, -1, 1e-9, 100)
                if (!w.isNaN()) {
                    seriesW1.appendData(DataPoint(x, w), true, numPointsW1 + 1)
                }
            }
            seriesW1.color = Color.MAGENTA // Cor diferente para a ramificação W-1
            seriesW1.thickness = 6
            graph.addSeries(seriesW1)
        }

        // Adiciona um ponto para o valor de X inserido pelo usuário
        val userPointSeries = LineGraphSeries(arrayOf(DataPoint(inputX, w0)))
        userPointSeries.color = Color.YELLOW
        userPointSeries.isDrawDataPoints = true
        userPointSeries.dataPointsRadius = 15f
        graph.addSeries(userPointSeries)

        // Se houver W-1, adicione o ponto correspondente também
        if (w_1 != null && !w_1.isNaN()) {
            val userPointSeriesW1 = LineGraphSeries(arrayOf(DataPoint(inputX, w_1)))
            userPointSeriesW1.color = Color.GREEN // Cor diferente para o ponto W-1
            userPointSeriesW1.isDrawDataPoints = true
            userPointSeriesW1.dataPointsRadius = 15f
            graph.addSeries(userPointSeriesW1)
        }
    }

    private fun mostrarAlertaSimples() {
        Log.d("LambertFragment", "Mostrar Alerta Simples chamado.")
        AlertDialog.Builder(requireContext()) // Use requireContext() aqui
            .setTitle("Sobre a Função W de Lambert")
            .setMessage("A função W de Lambert (também chamada de produtologaritmo) é a função inversa de " +
                    "f(w) = we^w. Ou seja, W(xe^x) = x. Ela tem aplicações em física, engenharia e combinatória." +
                    "Digitar o número desejado e clicar em calcular, o resultado será apresentado no Visor Principal, " +
                    "e gerará auutomaticamente um gráfico" )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Fecha o diálogo ao clicar em OK
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}