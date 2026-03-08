package com.hailong.magiccomputer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var tvExpression: TextView
    private lateinit var horizontalScrollView: HorizontalScrollView

    // 计算相关变量
    private var operand1: Double = 0.0
    private var operand2: Double = 0.0
    private var operator: String = ""
    private var isNewOperation = true
    private var lastOperator: String = ""
    private var lastOperand1: Double = 0.0

    // 历史记录 - 使用ArrayList存储，最多3条
    private val historyList = ArrayList<String>()
    private val maxHistorySize = 3

    // 魔术相关变量 - 完全隐藏
    private var magicModeActive = false        // 魔术模式是否激活
    private var firstNumber: Double = 0.0      // 第一个数
    private var secondNumber: Double = 0.0     // 第二个数
    private var firstResult: Double = 0.0      // 第一次等号的结果
    private var magicStep = 0                   // 0-初始, 1-已记录第一个数, 2-已记录第二个数, 3-已得到第一次结果

    private val decimalFormat = DecimalFormat("#.##########")

    // 添加TAG用于Logcat过滤
    companion object {
        private const val TAG = "MagicCalculator"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 输出时区信息
        val timeZone = TimeZone.getDefault()
        Log.d(TAG, "=== 魔术计算器启动 ===")
        Log.d(TAG, "默认时区: ${timeZone.id}")
        Log.d(TAG, "时区偏移: ${timeZone.getOffset(System.currentTimeMillis()) / (1000 * 60 * 60)} 小时")

        tvResult = findViewById(R.id.tvResult)
        tvExpression = findViewById(R.id.tvExpression)
        horizontalScrollView = findViewById(R.id.horizontalScrollView)

        // 设置所有按钮的点击事件
        setNumberButtonClick()
        setOperatorButtonClick()
        setFunctionButtonClick()

        // 设置长按除号触发魔术
        setupLongPressForDivide()
    }

    // 获取当前时间作为目标值 (格式: MMddHHmm)
    private fun getCurrentTimeTarget(): Double {
        // 使用默认时区的Calendar
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1  // 月份从0开始，所以要+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)  // 0-23小时
        val minute = calendar.get(Calendar.MINUTE)

        // 格式化为 MMddHHmm
        val targetStr = String.format("%02d%02d%02d%02d", month, day, hour, minute)
        val target = targetStr.toDouble()

        // 使用SimpleDateFormat输出本地时间
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()  // 明确设置使用默认时区
        val currentTimeStr = dateFormat.format(Date())

        // 同时输出时区信息
        val timeZone = TimeZone.getDefault()
        val timeZoneId = timeZone.id
        val timeZoneOffset = timeZone.getOffset(System.currentTimeMillis()) / (1000 * 60 * 60)

        Log.d(TAG, "=================================")
        Log.d(TAG, "当前系统时间: $currentTimeStr")
        Log.d(TAG, "时区: $timeZoneId (UTC${if (timeZoneOffset >= 0) "+" else ""}$timeZoneOffset)")
        Log.d(TAG, "Calendar获取的小时: $hour")
        Log.d(TAG, "月份: $month")
        Log.d(TAG, "日期: $day")
        Log.d(TAG, "分钟: $minute")
        Log.d(TAG, "目标值格式: ${month}${day}${hour}${minute} = $target")
        Log.d(TAG, "=================================")

        return target
    }

    // 设置数字按钮点击事件
    private fun setNumberButtonClick() {
        val numberIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        numberIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                onNumberClick(it as Button)
            }
        }
    }

    // 设置运算符点击事件
    private fun setOperatorButtonClick() {
        val operatorIds = listOf(
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
            R.id.btnDivide, R.id.btnEquals
        )

        operatorIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                onOperatorClick(it as Button)
            }
        }
    }

    // 设置功能按钮点击事件
    private fun setFunctionButtonClick() {
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearAll()
            // 退出魔术模式
            magicModeActive = false
            magicStep = 0
            firstNumber = 0.0
            secondNumber = 0.0
            firstResult = 0.0
            Log.d(TAG, "清除所有，退出魔术模式")
        }

        findViewById<Button>(R.id.btnToggleSign).setOnClickListener {
            toggleSign()
        }

        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            percent()
        }
    }

    // 设置长按除号触发魔术 - 完全静默
    private fun setupLongPressForDivide() {
        val divideButton = findViewById<Button>(R.id.btnDivide)

        divideButton.setOnLongClickListener {
            if (!magicModeActive) {
                // 首次触发魔术模式 - 无提示
                magicModeActive = true
                magicStep = 0
                firstNumber = 0.0
                secondNumber = 0.0
                firstResult = 0.0
                Log.d(TAG, "长按除号 - 进入魔术模式")
                // 不显示任何提示
            } else if (magicStep == 3) {
                // 已经得到第一次结果，现在需要计算差值
                Log.d(TAG, "长按除号 - 执行魔术，当前firstResult = $firstResult")
                performMagic()
            }
            true
        }
    }

    // 执行魔术：计算差值并自动填充 - 静默执行
    private fun performMagic() {
        // 动态获取当前时间作为目标值
        val currentTarget = getCurrentTimeTarget()

        // 计算差值：目标 - 第一次结果
        val diff = currentTarget - firstResult

        // 格式化差值显示
        val diffStr = formatNumber(diff)

        // 获取当前显示内容
        val currentText = tvResult.text.toString()

        Log.d(TAG, "执行魔术 - 当前显示: $currentText")
        Log.d(TAG, "执行魔术 - 目标值: $currentTarget")
        Log.d(TAG, "执行魔术 - 第一次结果: $firstResult")
        Log.d(TAG, "执行魔术 - 计算差值: $diff")

        // 检查当前显示是否包含运算符和第二个数
        if (currentText.contains(" ")) {
            val parts = currentText.split(" ")
            if (parts.size >= 3) {
                // 格式如 "89502 + 12345"，替换第二个数为差值
                val newText = "${parts[0]} ${parts[1]} $diffStr"
                Log.d(TAG, "执行魔术 - 替换乱点数字: $newText")
                updateResultWithScroll(newText)
            } else if (parts.size == 2 && currentText.endsWith(" ")) {
                // 格式如 "89502 + "，直接追加差值
                val newText = currentText + diffStr
                Log.d(TAG, "执行魔术 - 追加差值: $newText")
                updateResultWithScroll(newText)
            } else {
                // 其他情况直接显示差值
                Log.d(TAG, "执行魔术 - 直接显示差值: $diffStr")
                updateResultWithScroll(diffStr)
            }
        } else {
            // 纯数字，直接显示差值
            Log.d(TAG, "执行魔术 - 纯数字显示差值: $diffStr")
            updateResultWithScroll(diffStr)
        }

        // 不显示任何提示
    }

    // 数字点击处理
    private fun onNumberClick(button: Button) {
        val currentText = tvResult.text.toString()
        val buttonText = button.text.toString()

        // 检查当前是否在输入第二个操作数（显示中有运算符）
        if (operator.isNotEmpty()) {
            // 正在输入第二个操作数
            if (currentText.contains(" ")) {
                val parts = currentText.split(" ")
                if (parts.size == 2) {
                    // 格式如 "5 +"，开始输入第二个数
                    val newNumber = if (buttonText == ".") "0." else buttonText
                    val newText = "${parts[0]} ${parts[1]} $newNumber"
                    Log.d(TAG, "输入数字 - 开始输入第二个数: $newText")
                    updateResultWithScroll(newText)
                } else if (parts.size >= 3) {
                    // 格式如 "5 + 3"，继续输入第二个数
                    val currentSecondNum = parts[2]
                    val newSecondNum = if (currentSecondNum == "0" && buttonText != ".") {
                        buttonText
                    } else if (buttonText == ".") {
                        if (currentSecondNum.contains(".")) {
                            currentSecondNum
                        } else {
                            currentSecondNum + buttonText
                        }
                    } else {
                        currentSecondNum + buttonText
                    }
                    val newText = "${parts[0]} ${parts[1]} $newSecondNum"
                    Log.d(TAG, "输入数字 - 继续输入第二个数: $newText")
                    updateResultWithScroll(newText)
                }
            }
        } else {
            // 输入第一个操作数
            if (isNewOperation) {
                val newText = if (buttonText == ".") "0." else buttonText
                Log.d(TAG, "输入数字 - 开始新运算: $newText")
                updateResultWithScroll(newText)
                isNewOperation = false
            } else {
                if (buttonText == ".") {
                    if (!currentText.contains(".")) {
                        val newText = currentText + buttonText
                        Log.d(TAG, "输入数字 - 添加小数点: $newText")
                        updateResultWithScroll(newText)
                    }
                } else {
                    if (currentText == "0") {
                        Log.d(TAG, "输入数字 - 替换0: $buttonText")
                        updateResultWithScroll(buttonText)
                    } else {
                        val newText = currentText + buttonText
                        Log.d(TAG, "输入数字 - 追加数字: $newText")
                        updateResultWithScroll(newText)
                    }
                }
            }
        }
    }

    // 运算符点击处理
    private fun onOperatorClick(button: Button) {
        val currentText = tvResult.text.toString()

        when (button.id) {
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide -> {
                val currentValue = extractNumberFromDisplay(currentText)

                if (operator.isEmpty()) {
                    // 第一次输入运算符
                    operand1 = currentValue
                    operator = when (button.id) {
                        R.id.btnPlus -> "+"
                        R.id.btnMinus -> "-"
                        R.id.btnMultiply -> "×"
                        else -> "÷"
                    }

                    // 魔术模式记录第一个数
                    if (magicModeActive && magicStep == 0 && operator == "+") {
                        firstNumber = operand1
                        magicStep = 1
                        Log.d(TAG, "魔术模式 - 记录第一个数: $firstNumber")
                    }

                    // 显示 operand1 + 运算符
                    val newText = "${formatNumber(operand1)} ${operator} "
                    Log.d(TAG, "运算符 - 第一次输入: $newText")
                    updateResultWithScroll(newText)
                    isNewOperation = false
                } else {
                    // 已经有运算符，先计算
                    operand2 = currentValue

                    // 魔术模式记录第二个数
                    if (magicModeActive && magicStep == 1 && operator == "+") {
                        secondNumber = operand2
                        magicStep = 2
                        Log.d(TAG, "魔术模式 - 记录第二个数: $secondNumber")
                    }

                    val result = calculate()
                    Log.d(TAG, "运算符 - 计算结果: $result")

                    // 设置新的运算符
                    operand1 = result
                    operator = when (button.id) {
                        R.id.btnPlus -> "+"
                        R.id.btnMinus -> "-"
                        R.id.btnMultiply -> "×"
                        else -> "÷"
                    }

                    // 显示结果和新运算符
                    val newText = "${formatNumber(operand1)} ${operator} "
                    Log.d(TAG, "运算符 - 连续运算: $newText")
                    updateResultWithScroll(newText)
                }
            }

            R.id.btnEquals -> {
                val currentValue = extractNumberFromDisplay(currentText)

                if (operator.isNotEmpty()) {
                    operand2 = currentValue

                    // 魔术模式记录第二个数（如果没有在按加号时记录）
                    if (magicModeActive && magicStep == 1 && operator == "+") {
                        secondNumber = operand2
                        magicStep = 2
                        Log.d(TAG, "等号 - 记录第二个数: $secondNumber")
                    }

                    val result = calculate()
                    Log.d(TAG, "等号 - 计算结果: $result")

                    // 魔术模式：如果是第一次得到结果，保存它
                    if (magicModeActive && magicStep == 2 && operator == "+") {
                        firstResult = result
                        magicStep = 3
                        Log.d(TAG, "魔术模式 - 保存第一次结果: $firstResult, 进入步骤3")
                    }

                    // 判断是否是魔术模式的最后一步
                    val finalResult = if (magicModeActive && magicStep == 4) {
                        // 最后一步显示动态获取的目标值
                        val target = getCurrentTimeTarget()
                        Log.d(TAG, "魔术模式 - 最后一步，显示目标值: $target")
                        target
                    } else {
                        result
                    }

                    // 构建完整的算式 - 用于历史记录
                    val currentExpression = "${formatNumber(operand1)} ${operator} ${formatNumber(operand2)} = ${formatNumber(finalResult)}"
                    Log.d(TAG, "等号 - 添加历史记录: $currentExpression")

                    // 添加到历史记录
                    addToHistory(currentExpression)

                    // 显示结果
                    Log.d(TAG, "等号 - 显示结果: ${formatNumber(finalResult)}")
                    updateResultWithScroll(formatNumber(finalResult))

                    // 保存当前运算以便连续运算
                    lastOperator = operator
                    lastOperand1 = operand1
                    operand1 = finalResult
                    operator = ""
                    isNewOperation = true

                    // 如果是魔术模式的最后一步，重置状态
                    if (magicModeActive && magicStep == 4) {
                        Log.d(TAG, "魔术模式 - 完成，重置状态")
                        magicModeActive = false
                        magicStep = 0
                    }

                } else if (operator.isEmpty() && lastOperator.isNotEmpty()) {
                    // 连续等于运算
                    operand2 = currentValue
                    operand1 = lastOperand1
                    operator = lastOperator

                    val result = calculate()
                    Log.d(TAG, "连续等号 - 计算结果: $result")

                    // 判断是否是魔术模式的最后一步
                    val finalResult = if (magicModeActive && magicStep == 4) {
                        val target = getCurrentTimeTarget()
                        Log.d(TAG, "魔术模式 - 最后一步(连续等号)，显示目标值: $target")
                        target
                    } else {
                        result
                    }

                    // 构建完整的算式 - 用于历史记录
                    val currentExpression = "${formatNumber(operand1)} ${operator} ${formatNumber(operand2)} = ${formatNumber(finalResult)}"
                    Log.d(TAG, "连续等号 - 添加历史记录: $currentExpression")

                    // 添加到历史记录
                    addToHistory(currentExpression)

                    // 显示结果
                    Log.d(TAG, "连续等号 - 显示结果: ${formatNumber(finalResult)}")
                    updateResultWithScroll(formatNumber(finalResult))

                    operand1 = finalResult
                    operator = ""

                    // 如果是魔术模式的最后一步，重置状态
                    if (magicModeActive && magicStep == 4) {
                        Log.d(TAG, "魔术模式 - 完成(连续等号)，重置状态")
                        magicModeActive = false
                        magicStep = 0
                    }
                }
            }
        }
    }

    // 更新结果并自动滚动到最右边
    private fun updateResultWithScroll(text: String) {
        tvResult.text = text
        tvResult.post {
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }

    // 添加算式到历史记录
    private fun addToHistory(expression: String) {
        // 添加新记录
        historyList.add(expression)

        // 如果超过最大数量，移除最旧的（第一个）
        if (historyList.size > maxHistorySize) {
            historyList.removeAt(0)
        }

        // 更新显示 - 从上到下显示（最旧的在上面，最新的在下面）
        val displayText = StringBuilder()
        for (i in historyList.indices) {
            if (i > 0) {
                displayText.append("\n")
            }
            displayText.append(historyList[i])
        }

        // 无论是否魔术模式，都显示历史记录
        tvExpression.text = displayText.toString()
    }

    // 从显示文本中提取数字
    private fun extractNumberFromDisplay(text: String): Double {
        return if (text.contains(" ")) {
            val parts = text.split(" ")
            if (parts.size >= 3) {
                try {
                    parts[2].toDouble()
                } catch (e: Exception) {
                    operand1
                }
            } else {
                operand1
            }
        } else {
            try {
                text.toDouble()
            } catch (e: Exception) {
                0.0
            }
        }
    }

    // 计算逻辑
    private fun calculate(): Double {
        return when (operator) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "×" -> operand1 * operand2
            "÷" -> if (operand2 != 0.0) operand1 / operand2 else 0.0
            else -> operand2
        }
    }

    // 格式化数字显示（去掉多余的.0）
    private fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toInt().toString()
        } else {
            decimalFormat.format(number)
        }
    }

    // 清除所有
    private fun clearAll() {
        updateResultWithScroll("0")
        tvExpression.text = ""
        operand1 = 0.0
        operand2 = 0.0
        operator = ""
        lastOperator = ""
        lastOperand1 = 0.0
        historyList.clear()
        isNewOperation = true

        // 重置魔术状态
        magicModeActive = false
        magicStep = 0
        firstNumber = 0.0
        secondNumber = 0.0
        firstResult = 0.0

        Log.d(TAG, "清除所有")
    }

    // 正负号切换
    private fun toggleSign() {
        val currentText = tvResult.text.toString()

        if (currentText.contains(" ")) {
            val parts = currentText.split(" ")
            if (parts.size >= 3) {
                try {
                    val num = parts[2].toDouble() * -1
                    val newText = "${parts[0]} ${parts[1]} ${formatNumber(num)}"
                    Log.d(TAG, "正负号切换 - 第二个数: $newText")
                    updateResultWithScroll(newText)
                } catch (e: Exception) {
                    // 忽略
                }
            } else if (parts.size == 2) {
                val newText = "${formatNumber(operand1 * -1)} ${operator} "
                Log.d(TAG, "正负号切换 - 第一个数: $newText")
                updateResultWithScroll(newText)
            }
        } else {
            if (currentText != "0" && currentText != "0.") {
                try {
                    val value = currentText.toDouble() * -1
                    val newText = formatNumber(value)
                    Log.d(TAG, "正负号切换 - 纯数字: $newText")
                    updateResultWithScroll(newText)
                } catch (e: Exception) {
                    // 忽略
                }
            }
        }
    }

    // 百分比
    private fun percent() {
        val currentText = tvResult.text.toString()

        if (currentText.contains(" ")) {
            val parts = currentText.split(" ")
            if (parts.size >= 3) {
                try {
                    val num = parts[2].toDouble() / 100
                    val newText = "${parts[0]} ${parts[1]} ${formatNumber(num)}"
                    Log.d(TAG, "百分比 - 第二个数: $newText")
                    updateResultWithScroll(newText)
                } catch (e: Exception) {
                    // 忽略
                }
            } else if (parts.size == 2) {
                val newText = "${formatNumber(operand1 / 100)} ${operator} "
                Log.d(TAG, "百分比 - 第一个数: $newText")
                updateResultWithScroll(newText)
            }
        } else {
            if (currentText != "0" && currentText != "0.") {
                try {
                    val value = currentText.toDouble() / 100
                    val newText = formatNumber(value)
                    Log.d(TAG, "百分比 - 纯数字: $newText")
                    updateResultWithScroll(newText)
                } catch (e: Exception) {
                    // 忽略
                }
            }
        }
    }
}