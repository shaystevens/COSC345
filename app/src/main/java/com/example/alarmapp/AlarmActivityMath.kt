package com.example.alarmapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_alarm_math.*

/**
 * Alarm activity class, used for the alarm question to disable the alarm.
 *
 * @author Shay Stevens, Dougal Colquhoun, Liam Iggo, Austin Donnelly
 */
class AlarmActivityMath : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition: Int = 1
    private var mQuestionList: ArrayList<QuestionDatabase>? = null
    private var mSelectedOptionPosition: Int = 0
    private var correctAmt: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_math)

        mQuestionList = AddToDatabase.getQuestion()
        setQuestion()

        tv_option_one.setOnClickListener(this)
        tv_option_two.setOnClickListener(this)
        tv_option_three.setOnClickListener(this)
        tv_option_four.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    /**
     * This function is used to set the math question.
     */
    @SuppressLint("SetTextI18n")
    private fun setQuestion() {

        val question = mQuestionList!!.get(mCurrentPosition - 1)

        defaultOptionsView()
        if (mCurrentPosition == mQuestionList!!.size) {
            btn_submit.text = "Finish"
        } else {
            btn_submit.text = "Submit"
        }

        tv_question.text = question.question
        tv_option_one.text = question.option_one
        tv_option_two.text = question.option_two
        tv_option_three.text = question.option_three
        tv_option_four.text = question.option_four
    }

    /**
     * This function is used for displaying the default view.
     */
    private fun defaultOptionsView() {

        val options = ArrayList<TextView>()
        options.add(0, tv_option_one)
        options.add(1, tv_option_two)
        options.add(2, tv_option_three)
        options.add(3, tv_option_four)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.option_for_question
            )
        }

    }

    /**
     * This is called when an option is clicked.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_option_one -> {
                selectedOptionView(tv_option_one, 1)
            }
            R.id.tv_option_two -> {
                selectedOptionView(tv_option_two, 2)
            }
            R.id.tv_option_three -> {
                selectedOptionView(tv_option_three, 3)
            }
            R.id.tv_option_four -> {
                selectedOptionView(tv_option_four, 4)
            }
            R.id.btn_submit -> {
                if (mSelectedOptionPosition == 0) {
                    mCurrentPosition++

                    when {
                        mCurrentPosition <= mQuestionList!!.size -> {
                            setQuestion()
                        }
                        else -> {
                            Toast.makeText(
                                this,
                                "Rise&Shine!!", Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    val question = mQuestionList?.get(mCurrentPosition - 1)
                    if (question!!.correct_ans != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.incorrect_answer)
                    } else {
                        answerView(question.correct_ans, R.drawable.correct_answer)
                        correctAmt += 1;
                    }
                    if (mCurrentPosition == mQuestionList!!.size) {
                        btn_submit.text = "Finish"
                        //Where the alarm is stopped (not correct place)
                        stopAlarm()
                    } else {
                        btn_submit.text = "Go to next question"
                    }
                    mSelectedOptionPosition = 0
                }

            }
        }
    }

    /**
     * This function is called when an option is selected.
     * @param tv the textView of the option selected.
     * @param selectedOptionNum the number of the option selected.
     */
    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option
        )
    }

    /**
     * This function changes the view based on the answer selected and the correct answer.
     * @param answer the answer chosen.
     * @param drawableView view correct (green) or wrong (red).
     */
    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                tv_option_one.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            2 -> {
                tv_option_two.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            3 -> {
                tv_option_three.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            4 -> {
                tv_option_four.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
        }
    }

    /**
     * This function is called to stop the alarm.
     */
    private fun stopAlarm(){
        if (correctAmt == 2) {
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
        } else {
            val intent = Intent(this,AlarmActivityMath::class.java)
            startActivity(intent)
        }
    }
}