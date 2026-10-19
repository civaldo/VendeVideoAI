package br.com.vendevideoai

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.ViewGroup
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var productName: EditText
    private lateinit var benefits: EditText
    private lateinit var price: EditText
    private lateinit var script: EditText
    private lateinit var status: TextView
    private lateinit var voice: Spinner
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 28, 32, 28)
            setBackgroundColor(Color.rgb(248, 246, 255))
        }
        val scroll = ScrollView(this)
        scroll.addView(root)

        root.addView(text("VendeVídeo AI", 28, Color.rgb(91, 45, 145)))
        root.addView(text("Crie um roteiro de vendas pelo celular", 16, Color.DKGRAY))
        root.addView(button("Escolher foto do produto") { choosePhoto() })
        root.addView(text("Nome do produto", 14, Color.DKGRAY))
        productName = edit("Ex.: Fone Bluetooth")
        root.addView(productName)
        root.addView(text("Benefícios e características", 14, Color.DKGRAY))
        benefits = edit("Ex.: bateria duradoura, leve, fácil de usar")
        root.addView(benefits)
        root.addView(text("Preço (opcional)", 14, Color.DKGRAY))
        price = edit("Ex.: R$ 49,90")
        root.addView(price)
        root.addView(text("Estilo de narração", 14, Color.DKGRAY))
        voice = Spinner(this)
        voice.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Voz disponível no celular (pt-BR)", "Tentar voz feminina", "Tentar voz masculina"))
        root.addView(voice)
        root.addView(button("Gerar roteiro de vendas") { generateScript() })
        root.addView(text("Roteiro gerado", 16, Color.rgb(91, 45, 145)))
        script = edit("Seu roteiro aparecerá aqui")
        script.minLines = 7
        script.gravity = android.view.Gravity.TOP
        root.addView(script)
        root.addView(button("Ouvir narração") { speakScript() })
        root.addView(button("Copiar roteiro") { copyScript() })
        status = text("Versão inicial gratuita. A foto é selecionada no aparelho.", 13, Color.DKGRAY)
        root.addView(status)
        setContentView(scroll)
    }

    private fun text(value: String, size: Int, color: Int) = TextView(this).apply {
        text = value
        textSize = size.toFloat()
        setTextColor(color)
        setPadding(0, 12, 0, 12)
    }

    private fun edit(hintText: String) = EditText(this).apply {
        hint = hintText
        textSize = 16f
        setSingleLine(false)
        setPadding(18, 12, 18, 12)
        background = getDrawable(android.R.drawable.edit_text)
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = 12
        }
    }

    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = 10
        }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated Android callback retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImage = data?.data
            status.text = if (selectedImage != null) "Foto selecionada. O roteiro é criado a partir dos dados digitados." else "Nenhuma foto selecionada."
        }
    }

    private fun generateScript() {
        val name = productName.text.toString().trim().ifBlank { "este produto" }
        val details = benefits.text.toString().trim().ifBlank { "praticidade e facilidade no dia a dia" }
        val cost = price.text.toString().trim()
        val offer = if (cost.isNotBlank()) " E o melhor: por $cost." else ""
        script.setText("Olha só essa oportunidade! 🔥\n\nConheça $name: $details.$offer\n\nSe você estava procurando algo útil para facilitar sua rotina, vale a pena conferir todos os detalhes e ver se combina com você.\n\nToque no link do produto, confira as informações e aproveite enquanto estiver disponível!")
        status.text = "Roteiro criado. Revise preço e características antes de publicar."
    }

    private fun speakScript() {
        val content = script.text.toString().trim()
        if (content.isBlank()) {
            status.text = "Gere ou escreva um roteiro primeiro."
            return
        }
        if (!ttsReady) {
            status.text = "A narração ainda está carregando. Tente novamente em alguns segundos."
            return
        }
        val locale = Locale("pt", "BR")
        tts?.language = locale
        tts?.speak(content, TextToSpeech.QUEUE_FLUSH, null, "vendevideo-script")
        status.text = "Reproduzindo com a voz disponível no celular."
    }

    private fun copyScript() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Roteiro VendeVídeo AI", script.text.toString()))
        status.text = "Roteiro copiado."
    }

    override fun onInit(statusCode: Int) {
        ttsReady = statusCode == TextToSpeech.SUCCESS
        if (ttsReady) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                status.text = "Instale dados de voz em português nas configurações de texto para fala do Android."
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
