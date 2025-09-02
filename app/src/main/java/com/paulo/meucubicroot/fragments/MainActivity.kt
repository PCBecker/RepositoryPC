package com.paulo.meucubicroot.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.paulo.meucubicroot.R

class MainActivity : AppCompatActivity(), FragmentInteractionListener {

    private var currentFragmentTag: String? = null

    companion object {
        private const val CURRENT_FRAGMENT_TAG_KEY = "current_fragment_tag_key"
        private const val TAG_BASICO = "CALCULADORA"
        private const val TAG_EQGRAU2 = "EQGRAU2"
        private const val TAG_EQGRAU3 = "EQGRAU3"
        private const val TAG_Lambert = "Lambert"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        currentFragmentTag = savedInstanceState?.getString(CURRENT_FRAGMENT_TAG_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalculadoraFragment(), TAG_BASICO)
                // Substitua 'fragment_container' pelo ID do seu FrameLayout/Container
                .commit()
            currentFragmentTag =TAG_BASICO // Define a tag do fragmento inicial

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva o currentFragmentTag para que ele possa ser restaurado após a rotação
        outState.putString(CURRENT_FRAGMENT_TAG_KEY, currentFragmentTag)
    }
    override fun onLoadFragment(fragment: Fragment, tag: String) {
        Log.d("MainActivity", "onLoadFragment chamado. Fragment: ${fragment::class.simpleName}, Tag: $tag")

        // Procura por um fragmento existente com a mesma tag
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment != null) {
            Log.d("MainActivity", "Fragmento $tag já existe. Atualizando argumentos e exibindo-o.")

            // Se o fragmento já existe, anexa os novos argumentos a ele e o exibe.
            existingFragment.arguments = fragment.arguments

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, existingFragment, tag)
                .addToBackStack(tag)
                .commit()

        } else {
            Log.d("MainActivity", "Trocando de fragmento. currentFragmentTag: $currentFragmentTag -> $tag")

            // Se o fragmento não existe, cria-o do zero.
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
        currentFragmentTag = tag
        Log.d("MainActivity", "Transação de fragmento confirmada.")
    }
    @SuppressLint("SetTextI18n")
    override fun onNumberClicked(number: String) {
        when (currentFragmentTag) {
            "CALCULADORA" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("CALCULADORA")?.view?.findViewById<TextView>(
                        R.id.visor1
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "EQGRAU2" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("EQGRAU2")?.view?.findViewById<TextView>(
                        R.id.visor1grau2
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "EQGRAU3" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("EQGRAU3")?.view?.findViewById<TextView>(
                        R.id.visor1grau3
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "Lambert" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("Lambert")?.view?.findViewById<TextView>(
                        R.id.visor1Lambert
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }

        }
    }
     override fun onOperatorClicked(operator: String) {
         // Implementação vazia ou com lógica de depuração, conforme necessário.
         // Se você tiver operadores globais que a MainActivity precisa processar,
         // a lógica seria adicionada aqui.
         Log.d("MainActivity", "Operador '$operator' clicado. Lógica a ser implementada pelo fragmento ativo.")
         // Você pode passar este evento para o fragmento ativo, se ele precisar lidar com operadores.
     }

     override fun onClearClicked() {
         // Implementação vazia ou com lógica de depuração.
         // Se a MainActivity precisar lidar com a limpeza de algum visor principal,
         // a lógica seria adicionada aqui.
         Log.d("MainActivity", "Botão AC/Clear clicado. Lógica de limpeza a ser implementada pelo fragmento ativo.")
         // Ou chame clearDisplays() se essa função é para limpar tudo na Activity
         // clearDisplays()
     }

     override fun onCleanFragmentVisors(fragmentTag: String) {
         // Implementação vazia ou com lógica de depuração.
         // Esta função provavelmente seria chamada de dentro de um fragmento
         // para pedir à Activity para limpar os visores, se eles forem gerenciados pela Activity.
         Log.d("MainActivity", "Solicitação para limpar visores do fragmento '$fragmentTag' recebida.")
         // A sua função 'clearDisplays()' já existe e pode ser usada aqui, se apropriado.
         when (fragmentTag) {
             TAG_BASICO -> {
                 val visor1 = supportFragmentManager.findFragmentByTag(TAG_BASICO)?.view?.findViewById<TextView>(
                     R.id.visor1)
                 val visor2 = supportFragmentManager.findFragmentByTag(TAG_BASICO)?.view?.findViewById<TextView>(
                     R.id.visor2)
                 visor1?.text = ""
                 visor2?.text = ""
             }
             TAG_EQGRAU2 -> {
                 val visor1grau2 = supportFragmentManager.findFragmentByTag(TAG_EQGRAU2)?.view?.findViewById<TextView>(
                     R.id.visor1grau2)
                 val visor2grau2 = supportFragmentManager.findFragmentByTag(TAG_EQGRAU2)?.view?.findViewById<TextView>(
                     R.id.visor2grau2)
                 visor1grau2?.text = ""
                 visor2grau2?.text = ""
             }
             TAG_EQGRAU3 -> {
                 val visor1grau3 = supportFragmentManager.findFragmentByTag(TAG_EQGRAU3)?.view?.findViewById<TextView>(
                     R.id.visor1grau3)
                 val visor2grau3 = supportFragmentManager.findFragmentByTag(TAG_EQGRAU3)?.view?.findViewById<TextView>(
                     R.id.visor2grau3)
                 visor1grau3?.text = ""
                 visor2grau3?.text = ""
             }
             TAG_Lambert -> {
                 val visor1LambertFragment = supportFragmentManager.findFragmentByTag(TAG_Lambert)?.view?.findViewById<TextView>(
                     R.id.visor1Lambert)
                 val visor2LambertFragment = supportFragmentManager.findFragmentByTag(TAG_Lambert)?.view?.findViewById<TextView>(
                     R.id.visor2Lambert)
                 visor1LambertFragment?.text = ""
                 visor2LambertFragment?.text = ""
             }
         }
     }

     // A sua função clearDisplays() original, que pode ser chamada de onCleanFragmentVisors
     fun clearDisplays() {
         // Lógica de limpeza genérica ou para o fragmento atualmente ativo
         // Esta lógica foi movida para onCleanFragmentVisors para ser chamada por tag
         // Se ainda for necessária como uma função separada, mantenha-a.
         Log.d("MainActivity", "clearDisplays() chamado diretamente.")
     }

}