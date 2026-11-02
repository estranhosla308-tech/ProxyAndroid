package com.proxyandroid.panel

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import rikka.shizuku.Shizuku

class MainActivity : Activity() {
    private val purple = Color.rgb(145, 35, 220)
    private val bg = Color.rgb(8, 8, 11)
    private val card = Color.rgb(20, 20, 25)
    private val muted = Color.rgb(145, 143, 153)
    private lateinit var status: TextView
    private val switches = mutableListOf<Switch>()
    private val labels = listOf(
        "Sensi alta e precisa",
        "Otimização no processador",
        "120 FPS"
    )
    private val descriptions = listOf(
        "Ajustes de configuração permitidos",
        "Opções seguras de desempenho do aparelho",
        "Disponível somente se o aparelho e o jogo suportarem"
    )

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        runOnUiThread {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                setStatus("Shizuku autorizado", true)
                Toast.makeText(this, "Autorização concedida", Toast.LENGTH_SHORT).show()
            } else {
                setStatus("Autorização negada", false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(permissionListener)
        buildScreen()
        refreshShizuku()
    }

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener)
        super.onDestroy()
    }

    private fun shape(color: Int, radius: Float): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = radius }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

    private fun buildScreen() {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(20), dp(18), dp(18))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val menu = TextView(this).apply {
            text = "☰"
            textSize = 27f
            setTextColor(purple)
            gravity = Gravity.CENTER
            background = shape(Color.rgb(27, 15, 39), dp(12).toFloat())
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setOnClickListener { showMenu() }
        }
        header.addView(menu)
        val brand = text("  PROXY ", 21f, Color.WHITE, true)
        val brand2 = text("ANDROID", 21f, Color.rgb(190, 92, 255), true)
        val brandWrap = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        brandWrap.addView(brand); brandWrap.addView(brand2)
        header.addView(brandWrap, LinearLayout.LayoutParams(0, -2, 1f))
        status = text("● Verificando", 12f, Color.YELLOW, true)
        header.addView(status)
        outer.addView(header)

        val gap = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(25)) }
        outer.addView(gap)

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = shape(card, dp(20).toFloat())
            setPadding(dp(18), dp(20), dp(18), dp(18))
        }
        val section = text("┃  FUNÇÕES", 14f, Color.rgb(190, 92, 255), true)
        panel.addView(section)
        panel.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(10)) })

        labels.forEachIndexed { index, label ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(10), 0, dp(10))
            }
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(text(label, 16f, Color.WHITE, true))
            col.addView(text(descriptions[index], 11f, muted).apply { setPadding(0, dp(4), 0, 0) })
            row.addView(col, LinearLayout.LayoutParams(0, -2, 1f))
            val toggle = Switch(this).apply {
                buttonTintList = android.content.res.ColorStateList.valueOf(purple)
                isChecked = false
            }
            switches.add(toggle)
            row.addView(toggle)
            panel.addView(row)
        }

        val inject = TextView(this).apply {
            text = "INJETAR"
            textSize = 16f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = shape(purple, dp(15).toFloat())
            setPadding(0, dp(17), 0, dp(17))
            setOnClickListener { applyAndLaunch() }
        }
        val injectParams = LinearLayout.LayoutParams(-1, dp(58)).apply { topMargin = dp(18) }
        panel.addView(inject, injectParams)
        outer.addView(panel)

        val footer = text("FREE FIRE NORMAL  •  com.dts.freefireth", 10f, muted)
        footer.gravity = Gravity.CENTER
        footer.setPadding(0, dp(18), 0, 0)
        outer.addView(footer)

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            setBackgroundColor(bg)
            addView(outer, android.view.ViewGroup.LayoutParams(-1, -2))
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN

        window.statusBarColor = bg
        window.navigationBarColor = bg


        // ANIMACOES PROXY ANDROID

        // Entrada suave do painel
        panel.alpha = 0f
        panel.translationY = dp(18).toFloat()
        panel.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(850)
            .start()

        // Brilho roxo pulsante na borda
        val borderAnimator = ValueAnimator.ofArgb(
            Color.rgb(75, 15, 120),
            Color.rgb(190, 50, 255)
        ).apply {
            duration = 1600
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animator ->
                val drawable = panel.background as? GradientDrawable
                drawable?.setStroke(dp(2), animator.animatedValue as Int)
            }
        }
        borderAnimator.start()

        // Botao INJETAR pulsante
        ValueAnimator.ofFloat(1f, 1.035f).apply {
            duration = 900
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                val scale = it.animatedValue as Float
                inject.scaleX = scale
                inject.scaleY = scale
            }
            start()
        }

        // Menu com movimento suave
        ValueAnimator.ofFloat(0.94f, 1f).apply {
            duration = 1100
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                val scale = it.animatedValue as Float
                menu.scaleX = scale
                menu.scaleY = scale
            }
            start()
        }

        // Entrada suave do cabecalho
        header.alpha = 0f
        header.animate().alpha(1f).setDuration(1000).start()

        setContentView(scroll)
    }

    private fun showMenu() {
        val items = arrayOf("Ativar / abrir Shizuku", "Verificar conexão", "Como parear por código", "Sobre as funções")
        AlertDialog.Builder(this)
            .setTitle("PROXY ANDROID")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> openShizuku()
                    1 -> refreshShizuku()
                    2 -> showPairingHelp()
                    3 -> showFeatureInfo()
                }
            }
            .setNegativeButton("Fechar", null)
            .show()
    }

    private fun openShizuku() {
        val launch = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
        if (launch != null) startActivity(launch)
        else {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/")))
            } catch (_: Exception) {
                Toast.makeText(this, "Instale o Shizuku primeiro", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPairingHelp() {
        AlertDialog.Builder(this)
            .setTitle("Parear o Shizuku")
            .setMessage(
                "1. Ative as Opções do desenvolvedor e a Depuração sem fio.\\n" +
                "2. Abra o aplicativo Shizuku e escolha Parear.\\n" +
                "3. Toque em Parear com código de pareamento.\\n" +
                "4. Informe o código exibido pelo Android na notificação do Shizuku.\\n" +
                "5. Inicie o Shizuku e volte aqui para conceder a autorização.\\n\\n" +
                "O pareamento é feito pelo Android/Shizuku; este app não consegue gerar nem descobrir o código."
            )
            .setPositiveButton("Entendi", null)
            .show()
    }

    private fun showFeatureInfo() {
        AlertDialog.Builder(this)
            .setTitle("Sobre as funções")
            .setMessage(
                "Os interruptores selecionam as opções da interface. Os módulos reais de sensibilidade, desempenho e taxa de quadros ainda precisam ser implementados e validados para cada aparelho. " +
                "O Shizuku não libera automaticamente acesso a configurações protegidas nem garante 120 FPS. Este app não altera a memória do Free Fire nem contorna o anti-cheat."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun refreshShizuku() {
        try {
            if (!Shizuku.pingBinder()) {
                setStatus("Shizuku Offline", false)
                return
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                setStatus("Shizuku autorizado", true)
            } else {
                setStatus("Shizuku conectado", true)
            }
        } catch (_: Exception) {
            setStatus("Shizuku indisponível", false)
        }
    }

    private fun setStatus(message: String, good: Boolean) {
        status.text = if (good) "● $message" else "● $message"
        status.setTextColor(if (good) Color.rgb(50, 210, 120) else Color.rgb(255, 90, 90))
    }

    private fun applyAndLaunch() {
        val selected = switches.indices.filter { switches[it].isChecked }
        if (selected.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos uma função", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            if (!Shizuku.pingBinder()) {
                AlertDialog.Builder(this)
                    .setTitle("Shizuku não está ativo")
                    .setMessage("Inicie o Shizuku pelo pareamento de Depuração sem fio e tente novamente.")
                    .setPositiveButton("Abrir Shizuku") { _, _ -> openShizuku() }
                    .setNegativeButton("Cancelar", null)
                    .show()
                return
            }
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(1001)
                Toast.makeText(this, "Conceda a autorização na janela do Shizuku e toque INJETAR novamente.", Toast.LENGTH_LONG).show()
                return
            }
            val names = selected.joinToString("\n") { "• ${labels[it]}" }
            AlertDialog.Builder(this)
                .setTitle("Opções selecionadas")
                .setMessage("$names\n\nA conexão Shizuku está autorizada. Os módulos de otimização ainda não estão implementados; vou abrir o Free Fire sem aplicar alterações no jogo.")
                .setPositiveButton("Abrir Free Fire") { _, _ -> launchFreeFire() }
                .setNegativeButton("Voltar", null)
                .show()
        } catch (_: Exception) {
            setStatus("Erro ao verificar Shizuku", false)
        }
    }

    private fun launchFreeFire() {
        val launch = packageManager.getLaunchIntentForPackage("com.dts.freefireth")
        if (launch != null) startActivity(launch)
        else Toast.makeText(this, "Free Fire normal não encontrado. Instale o Free Fire (não MAX).", Toast.LENGTH_LONG).show()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
