package com.paulo.meucubicroot.fragments

import androidx.fragment.app.Fragment

interface FragmentInteractionListener {
    /**
     * Chamado quando um número é clicado em um teclado de calculadora.
     * @param number O número (String) que foi clicado.
     */
    fun onNumberClicked(number: String)

    /**
     * Chamado quando um operador (+, -, *, /) é clicado.
     * @param operator O operador (String) que foi clicado.
     */
    fun onOperatorClicked(operator: String)

    /**
     * Chamado quando o botão de limpar (AC) é acionado.
     */
    fun onClearClicked()

    /**
     * Solicita que a Activity carregue um novo fragmento em um container.
     * @param fragment A instância do Fragment a ser carregada.
     * @param tag A tag String para identificar o Fragment na FragmentManager.
     */
    fun onLoadFragment(fragment: Fragment, tag: String)

    /**
     * Solicita que a Activity limpe os visores de um fragmento específico.
     * @param fragmentTag A tag do fragmento cujos visores devem ser limpos.
     */
    fun onCleanFragmentVisors(fragmentTag: String)

    // Adicione outros métodos conforme a necessidade de comunicação.
    // Exemplo: fun onResultCalculated(result: String)
}