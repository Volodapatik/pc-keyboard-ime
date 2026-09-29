# PC Keyboard IME for Android

Кастомна клавіатура для Android у стилі PC (як Gboard, але з F1–F12, Ctrl, Alt, Win, Esc, стрілками тощо).

## Що вміє
- Повна QWERTY розкладка
- Ряд F1–F12
- Ctrl / Alt / Win / Esc / Tab / Caps / Shift
- Стрілки, Home, End, PgUp, PgDn
- Цифри та символи

## Як встановити готовий APK
1. Відкрий вкладку **Releases** у цьому репозиторії
2. Завантаж останній `app-debug.apk`
3. Встанови на телефон (дозволь установку з невідомих джерел)
4. Налаштування → Система → Мови та введення → Екранна клавіатура → Керування клавіатурами → увімкни **PC Keyboard**
5. Вибери її як поточну клавіатуру

## Як зібрати самому
### Варіант 1: GitHub Actions (рекомендовано)
Просто зроби push в `main` або створи тег `v*` — Actions автоматично збере APK і прикріпить його до Release.

### Варіант 2: Android Studio
1. Клонуй репозиторій
2. Відкрий в Android Studio
3. Build → Build Bundle(s) / APK(s) → Build APK(s)

## Структура
- `app/src/main/java/.../PcKeyboardService.kt` — основний сервіс IME
- `res/xml/keyboard_pc.xml` — розкладка клавіш
- `.github/workflows/build-and-release.yml` — автоматична збірка + реліз

Зроблено для тебе 🔥
