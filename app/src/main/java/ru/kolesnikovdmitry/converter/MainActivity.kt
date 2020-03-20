package ru.kolesnikovdmitry.converter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity: AppCompatActivity() {

    private lateinit var textViewFrom  : TextView
    private lateinit var textViewTo    : TextView
    private lateinit var mEditTextFrom : EditText
    private lateinit var mEditTextTo   : EditText

    //первое поле
    val STR_M_FROM  : String = "Метр"
    val STR_CM_FROM : String = "Сантиметр"
    val STR_DM_FROM : String = "Дециметр"
    val ID_M_FROM     : Int = 102
    val ID_CM_FROM    : Int = 103
    val ID_DM_FROM    : Int = 104
    val GROUP_ID_FROM : Int = 101
    //Второе Поле
    val STR_M_TO  : String = "Метр"
    val STR_CM_TO : String = "Сантиметр"
    val STR_DM_TO : String = "Дециметр"
    val ID_M_TO     : Int = 202
    val ID_CM_TO    : Int = 203
    val ID_DM_TO    : Int = 204
    val GROUP_ID_TO : Int = 201

    //константы для перевода:
    val CONSTS = listOf<BigDecimal>(BigDecimal(0.01), BigDecimal(0.1), BigDecimal(1))

    // идентификатор величины для обоих полей:
    var CUR_SIZE_FROM : Int = 0 //0 - не выбрано, 1 - см, 2 - дм, 3 - м
    var CUR_SIZE_TO   : Int = 0 //аналогично

    var CUR_EDIT_TEXT = 0 //0 - editTextFrom, 1 - editTextTo

    //база данных ->
    var BOOL_STATUS_M_FROM  : Boolean = false
    var BOOL_STATUS_CM_FROM : Boolean = false
    var BOOL_STATUS_DM_FROM : Boolean = false
    //2е поле
    var BOOL_STATUS_M_TO  : Boolean = false
    var BOOL_STATUS_CM_TO : Boolean = false
    var BOOL_STATUS_DM_TO : Boolean = false

    //НАШ ФОРМАТЕР
    val decimalFormat : DecimalFormat = DecimalFormat("#.##########")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        textViewFrom = findViewById(R.id.textViewFrom)
        textViewFrom.setOnClickListener { view ->
            showPopUpMenuFrom(view)
        }

        textViewTo = findViewById(R.id.textViewTo)
        textViewTo.setOnClickListener { textView ->
            showPopUpMenuTo(textView)
        }

        mEditTextTo   = findViewById(R.id.editTextToMain)
        mEditTextFrom = findViewById(R.id.editTextFromMain)

        val imageButton : ImageButton = findViewById(R.id.imageButtonArrows)
        imageButton.setOnClickListener { view ->
            val strFrom : String = mEditTextFrom.text.toString()
            val strTo   : String = mEditTextTo.text.toString()
            if(strFrom == "" && strTo == "") {
                val snackbar : Snackbar = Snackbar.make(view, "Заполните хотя бы одно поле!", Snackbar.LENGTH_LONG)
                snackbar.setAction("ОК", View.OnClickListener {
                    snackbar.dismiss()
                })
                snackbar.show()
                return@setOnClickListener
            }
            else if(CUR_SIZE_TO == 0 || CUR_SIZE_FROM == 0) {
                val snackbar : Snackbar = Snackbar.make(view, "Выберете величины", Snackbar.LENGTH_LONG)
                snackbar.setAction("OK", View.OnClickListener {
                    snackbar.dismiss()
                })
                snackbar.show()
                return@setOnClickListener
            }
            convert()
        }

        //устанавливаем на поля текстовые слушатели нажатия: при нажатии на определенное поле "фокус" будет переходить к этому полю
        mEditTextFrom.setOnTouchListener { v, event ->
            CUR_EDIT_TEXT = 0
            return@setOnTouchListener false
        }  //тоже возвращаем фолс, типо переопределили метод, но не до конца.

        mEditTextTo.setOnTouchListener {v, event ->
            CUR_EDIT_TEXT = 1
            return@setOnTouchListener false
        }

        mEditTextFrom.setOnKeyListener { view, keyCode, event ->
            convert()
            return@setOnKeyListener false                     //если тру, значит метод переопределили и число не вводится, если фолс, то и число вводится и метод исполняется
        }
        mEditTextTo.setOnKeyListener { v, keyCode, event ->
            convert()
            return@setOnKeyListener false
        }

        val buttonChooseFrom : ImageButton = findViewById(R.id.buttonChooseFrom)
        buttonChooseFrom.setOnClickListener { view ->
            showPopUpMenuFrom(view)
        }

        val buttonChooseTo : ImageButton = findViewById(R.id.buttonChooseTo)
        buttonChooseTo.setOnClickListener { view ->
            showPopUpMenuTo(view)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuButtonExit -> {
                finish()
                return true
            }
            else -> {
                return true
            }
        }
    }

    private fun convert(): Boolean {
        try{
            var strFrom : String = mEditTextFrom.text.toString()
            var strTo   : String = mEditTextTo.text.toString()
            if(CUR_SIZE_FROM == 0 || CUR_SIZE_TO == 0) {
                return true
            }
            if(CUR_EDIT_TEXT == 0) {  //Если текущий эдит текст первый, тогда
                if(strFrom == "") {
                    mEditTextTo.setText("")
                    return true
                }
                strTo = decimalFormat.format(strFrom.toBigDecimal() * (CONSTS[CUR_SIZE_FROM - 1] / CONSTS[CUR_SIZE_TO - 1])).toBigDecimal().setScale(howManyNumbersAfterPoint(strFrom) - 1 + CUR_SIZE_TO, RoundingMode.HALF_UP).toString()
                mEditTextTo.setText(strTo)
                return true
            }
            else {
                if(strTo == "") {
                    mEditTextFrom.setText("")
                    return true
                }
                strFrom = decimalFormat.format(strTo.toBigDecimal() * (CONSTS[CUR_SIZE_TO - 1] / CONSTS[CUR_SIZE_FROM - 1])).toBigDecimal().setScale(howManyNumbersAfterPoint(strTo)- 1 + CUR_SIZE_FROM, RoundingMode.HALF_UP).toString()
                mEditTextFrom.setText(strFrom)
                return true
            }
        } catch (ex :Throwable) {
            if (CUR_EDIT_TEXT == 0) mEditTextTo.setText("")
            else mEditTextFrom.setText("")
        }
        return true
    }

    private fun howManyNumbersAfterPoint(str: String) : Int {
        for(char in str) {
            if (char == '.') {
                return str.substringAfterLast('.').length
            }
        }
        return 0
    }

    private fun showPopUpMenuTo(view: View?) {
        val popupMenuFrom : PopupMenu = PopupMenu(applicationContext, view, 0)
        popupMenuFrom.menu.add(GROUP_ID_TO, ID_M_TO,  1, STR_M_TO ).isChecked = BOOL_STATUS_M_TO
        popupMenuFrom.menu.add(GROUP_ID_TO, ID_CM_TO, 3, STR_CM_TO).isChecked = BOOL_STATUS_CM_TO
        popupMenuFrom.menu.add(GROUP_ID_TO, ID_DM_TO, 2, STR_DM_TO).isChecked = BOOL_STATUS_DM_TO
        popupMenuFrom.menu.setGroupCheckable(GROUP_ID_TO, true, true)
        popupMenuFrom.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                ID_CM_TO -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_TO = true
                        BOOL_STATUS_DM_TO = false
                        BOOL_STATUS_M_TO  = false
                        menuItem.isChecked = BOOL_STATUS_CM_TO
                        textViewTo.text = STR_CM_TO
                        CUR_SIZE_TO = 1
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                ID_M_TO -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_TO = false
                        BOOL_STATUS_DM_TO = false
                        BOOL_STATUS_M_TO  = true
                        menuItem.isChecked = BOOL_STATUS_M_TO
                        textViewTo.text = STR_M_TO
                        CUR_SIZE_TO = 3
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                ID_DM_TO -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_TO = false
                        BOOL_STATUS_DM_TO = true
                        BOOL_STATUS_M_TO  = false
                        menuItem.isChecked = BOOL_STATUS_DM_TO
                        textViewTo.text = STR_DM_TO
                        CUR_SIZE_TO = 2
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        //popupMenuFrom.menu.findItem(102).isChecked = true
        convert()
        popupMenuFrom.show()
    }

    private fun showPopUpMenuFrom(view: View?) {
        val popupMenuFrom : PopupMenu = PopupMenu(applicationContext, view, 0)
        popupMenuFrom.menu.add(GROUP_ID_FROM, ID_M_FROM,  1, STR_M_FROM ).isChecked = BOOL_STATUS_M_FROM
        popupMenuFrom.menu.add(GROUP_ID_FROM, ID_CM_FROM, 3, STR_CM_FROM).isChecked = BOOL_STATUS_CM_FROM
        popupMenuFrom.menu.add(GROUP_ID_FROM, ID_DM_FROM, 2, STR_DM_FROM).isChecked = BOOL_STATUS_DM_FROM
        popupMenuFrom.menu.setGroupCheckable(GROUP_ID_FROM, true, true)
        popupMenuFrom.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                ID_CM_FROM -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_FROM = true
                        BOOL_STATUS_DM_FROM = false
                        BOOL_STATUS_M_FROM  = false
                        menuItem.isChecked = BOOL_STATUS_CM_FROM
                        textViewFrom.text = STR_CM_FROM
                        CUR_SIZE_FROM = 1
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                ID_M_FROM -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_FROM = false
                        BOOL_STATUS_DM_FROM = false
                        BOOL_STATUS_M_FROM  = true
                        menuItem.isChecked = BOOL_STATUS_M_FROM
                        textViewFrom.text = STR_M_FROM
                        CUR_SIZE_FROM = 3
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                ID_DM_FROM -> {
                    if (!menuItem.isChecked) {
                        BOOL_STATUS_CM_FROM = false
                        BOOL_STATUS_DM_FROM = true
                        BOOL_STATUS_M_FROM  = false
                        menuItem.isChecked = BOOL_STATUS_DM_FROM
                        textViewFrom.text = STR_DM_FROM
                        CUR_SIZE_FROM = 2
                    }
                    convert()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        //popupMenuFrom.menu.findItem(102).isChecked = true
        convert()
        popupMenuFrom.show()
    }

}