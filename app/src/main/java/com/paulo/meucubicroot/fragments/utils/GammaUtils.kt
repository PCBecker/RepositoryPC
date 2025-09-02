package paulo.com.meucubicroot.utils // SEU PACOTE CORRETO

import kotlin.math.*
import android.util.Log

/**
 * Utilitários para o cálculo de funções matemáticas avançadas, como a Função Gama e Fatorial.
 */
object GammaUtils {
    private const val TAG = "GammaUtils"

    /**
     * Calcula o fatorial de um número inteiro não negativo.
     * @param n O número inteiro para calcular o fatorial.
     * @return O valor do fatorial.
     * @throws IllegalArgumentException se o número for negativo.
     */
    public fun factorial(n: Int): Long {
        Log.d(TAG, "Chamada factorial($n)")
        if (n < 0) {
            Log.e(TAG, "Fatorial de número negativo: $n")
            throw IllegalArgumentException("Fatorial não é definido para números negativos")
        }
        if (n == 0 || n == 1) {
            Log.d(TAG, "Fatorial de $n é 1")
            return 1L
        }
        var result: Long = 1
        for (i in 2..n) {
            result *= i
            Log.d(TAG, "  Iteração $i, resultado parcial: $result")
        }
        Log.d(TAG, "Fatorial($n) = $result")
        return result
    }

    /**
     * Calcula o fatorial de um número (inteiro ou fracionado) usando a Função Gama.
     * A relação é n! = Gamma(n + 1).
     * @param n O valor (inteiro ou fracionado) para calcular o fatorial.
     * @return O valor do fatorial fracionado. Retorna NaN se a entrada for inválida (ex: inteiros negativos).
     */
    public fun factorialFractional(n: Double): Double {
        Log.d(TAG, "Chamada factorialFractional($n)")
        val result = gamma(n + 1.0)
        Log.d(TAG, "factorialFractional($n) chamou gamma(${n + 1.0}) e obteve: $result")
        return result
    }

    /**
     * Tenta calcular a Função Gama para um dado número real x.
     * Se x for um inteiro positivo, usa o fatorial.
     * Para outros valores (não-inteiros), usa a Aproximação de Lanczos.
     *
     * @param x O valor real para o qual a Função Gama será calculada.
     * @return O resultado do cálculo da Função Gama ou NaN (Not a Number) se a entrada for inválida.
     */
    fun gamma(x: Double): Double {
        Log.d(TAG, "Chamada gamma($x)")
        // A função gama tem singularidades nos inteiros não positivos (0, -1, -2, ...)
        if (x <= 0.0 && x.rem(1.0) == 0.0) { // Verifica inteiros não positivos
            Log.w(TAG, "Gamma($x): Singularidade, retornando NaN")
            return Double.NaN // Retorna NaN para valores onde a função não está definida
        }

        // Se x for um inteiro positivo, calculamos o fatorial diretamente.
        if (x > 0.0 && x.rem(1.0) == 0.0) {
            val nInt = x.toInt() - 1
            Log.d(TAG, "Gamma($x): Chamando factorial($nInt) para inteiro positivo")
            return factorial(nInt).toDouble()
        }

        // --- Aproximação de Lanczos Aprimorada para não-inteiros ---
        val p = doubleArrayOf(
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        )
        val g = 7

        var currentXForLanczos = x // Argumento que será usado na aproximação de Lanczos
        var sinTerm: Double = 1.0 // sin(pi * x_original)
        var reflectionNeeded = false

        // Step 1: Lida com a reflexão para argumentos negativos ou x em (0, 0.5)
        if (currentXForLanczos < 0.5) {
            sinTerm = sin(PI * currentXForLanczos)
            if (abs(sinTerm) < 1e-9) { // Evita divisão por zero se o sin(PI*x) for próximo de 0
                Log.w(TAG, "Gamma($x): Sin(PI*x) próximo de zero, retornando NaN")
                return Double.NaN
            }
            currentXForLanczos = 1.0 - currentXForLanczos // Novo argumento para Gamma(1-x)
            reflectionNeeded = true
            Log.d(TAG, "Gamma($x): Aplicando reflexão. Novo argumento para Lanczos: $currentXForLanczos")
        }

        // Agora currentXForLanczos é >= 0.5 (ou era originalmente >= 0.5)
        // Step 2: Aplica a Aproximação de Lanczos para Gamma(currentXForLanczos)
        // A série é projetada para Gamma(z+1) onde z é o argumento.
        // Então, para obter Gamma(currentXForLanczos), precisamos que 'z' na fórmula seja (currentXForLanczos - 1).
        val z_arg_for_series = currentXForLanczos - 1.0

        val tmp = z_arg_for_series + g + 0.5
        var seriesSum = p[0]
        for (i in 1 until p.size) {
            seriesSum += p[i] / (z_arg_for_series + i)
        }

        // Isso calcula Gamma(z_arg_for_series + 1), que é Gamma(currentXForLanczos)
        val lanczosGammaResult = sqrt(2.0 * PI) * tmp.pow(z_arg_for_series + 0.5) * exp(-tmp) * seriesSum

        // Step 3: Aplica a reflexão se foi usada (formula: Gamma(x) = PI / (sin(PI*x) * Gamma(1-x)) )
        val finalResult = if (reflectionNeeded) {
            PI / (sinTerm * lanczosGammaResult) // CORREÇÃO AQUI: Divisão em vez de multiplicação
        } else {
            lanczosGammaResult // Nenhuma reflexão necessária
        }

        Log.d(TAG, "Gamma($x): Resultado final: $finalResult")
        return finalResult
    }
}
