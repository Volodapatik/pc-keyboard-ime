package com.pckeyboard.ime

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.os.Build

class PcKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private var keyboardView: KeyboardView? = null
    private var keyboardEn: Keyboard? = null
    private var keyboardUk: Keyboard? = null
    private var keyboardRu: Keyboard? = null
    private var currentKeyboard: Keyboard? = null
    private var langIndex = 0 // 0=EN, 1=UK, 2=RU
    private var caps = false

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboardEn = Keyboard(this, R.xml.keyboard_pc)
        keyboardUk = Keyboard(this, R.xml.keyboard_uk)
        keyboardRu = Keyboard(this, R.xml.keyboard_ru)
        currentKeyboard = keyboardEn
        keyboardView?.keyboard = currentKeyboard
        keyboardView?.setOnKeyboardActionListener(this)
        keyboardView?.isPreviewEnabled = false

        // Extra bottom padding so keys don't go under system navigation bar
        val navHeight = getNavBarHeight()
        keyboardView?.setPadding(0, 6, 0, navHeight + 8)

        return keyboardView!!
    }

    private fun getNavBarHeight(): Int {
        val resources = resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 48
    }

    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        val view = keyboardView ?: return
        // Tell the system the visible content ends at the top of our keyboard
        outInsets.contentTopInsets = view.top
        outInsets.visibleTopInsets = view.top
        outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardView?.keyboard = currentKeyboard
        // Re-apply padding in case nav bar height changed (gesture vs 3-button)
        val navHeight = getNavBarHeight()
        keyboardView?.setPadding(0, 6, 0, navHeight + 8)
    }

    private fun switchLanguage() {
        langIndex = (langIndex + 1) % 3
        currentKeyboard = when (langIndex) {
            1 -> keyboardUk
            2 -> keyboardRu
            else -> keyboardEn
        }
        caps = false
        currentKeyboard?.isShifted = false
        keyboardView?.keyboard = currentKeyboard
        keyboardView?.invalidateAllKeys()
    }

    private fun showImePicker() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection = currentInputConnection ?: return

        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_SHIFT -> {
                caps = !caps
                currentKeyboard?.isShifted = caps
                keyboardView?.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
            -10 -> switchLanguage()          // EN / UK / RU
            -11 -> showImePicker()           // 🌐 switch keyboard app (like Gboard)
            -6 -> { /* Ctrl */ }
            -7 -> { /* Alt */ }
            -8 -> { /* Win */ }
            -9 -> { // Esc
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ESCAPE))
            }
            in 131..142 -> {
                val fKey = KeyEvent.KEYCODE_F1 + (primaryCode - 131)
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, fKey))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, fKey))
            }
            19 -> sendKey(ic, KeyEvent.KEYCODE_DPAD_UP)
            20 -> sendKey(ic, KeyEvent.KEYCODE_DPAD_DOWN)
            21 -> sendKey(ic, KeyEvent.KEYCODE_DPAD_LEFT)
            22 -> sendKey(ic, KeyEvent.KEYCODE_DPAD_RIGHT)
            else -> {
                var code = primaryCode
                if (caps && code in 97..122) code -= 32
                if (caps && code in 1072..1103) code -= 32
                if (caps && code == 1110) code = 1030 // І
                if (caps && code == 1108) code = 1028 // Є
                if (caps && code == 1101) code = 1069 // Э
                if (caps && code == 1099) code = 1067 // Ы
                ic.commitText(code.toChar().toString(), 1)
            }
        }
    }

    private fun sendKey(ic: InputConnection, keyCode: Int) {
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {
        currentInputConnection?.commitText(text, 1)
    }
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
