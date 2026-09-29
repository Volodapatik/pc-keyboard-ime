package com.pckeyboard.ime

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class PcKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private var caps = false

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.keyboard_pc)
        keyboardView?.keyboard = keyboard
        keyboardView?.setOnKeyboardActionListener(this)
        keyboardView?.isPreviewEnabled = false
        return keyboardView!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardView?.keyboard = keyboard
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection = currentInputConnection ?: return

        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_SHIFT -> {
                caps = !caps
                keyboard?.isShifted = caps
                keyboardView?.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
            // Custom modifiers
            -6 -> { /* Ctrl */ }
            -7 -> { /* Alt */ }
            -8 -> { /* Win */ }
            -9 -> { // Esc
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ESCAPE))
            }
            // Function keys F1–F12
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
                if (caps && code in 97..122) {
                    code -= 32
                }
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
