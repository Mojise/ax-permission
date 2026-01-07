package com.ax.library.ax_permission.customview

import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max

/**
 * 최대 2줄까지 텍스트를 표시하고, 넘어가면 폰트 크기를 자동으로 줄이는 TextView
 *
 * 기본 폰트 크기에서 시작하여 텍스트가 [maxAutoSizeLines]줄을 초과하면
 * [minAutoSizeTextSize]까지 폰트 크기를 단계적으로 줄여서 맞춥니다.
 */
internal class AutoSizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    /** 최소 폰트 크기 (sp) */
    private var minAutoSizeTextSize: Float = 12f

    /** 폰트 크기 조정 단위 (sp) */
    private var autoSizeStepGranularity: Float = 1f

    /** 최대 허용 줄 수 */
    private var maxAutoSizeLines: Int = 2

    /** 원본 폰트 크기 (px) - 초기 설정된 textSize를 저장 */
    private var originalTextSizePx: Float = 0f

    /** 원본 폰트 크기가 설정되었는지 여부 */
    private var isOriginalTextSizeSet: Boolean = false

    /** 현재 auto-size 조정 중인지 여부 (무한 루프 방지) */
    private var isAdjusting: Boolean = false

    init {
        // maxLines 설정 (TextView 기본 속성과 연동)
        if (maxLines > 0) {
            maxAutoSizeLines = maxLines
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 원본 폰트 크기 저장 (최초 1회)
        if (!isOriginalTextSizeSet && textSize > 0) {
            originalTextSizePx = textSize
            isOriginalTextSizeSet = true
        }

        // 가용 너비가 있을 때만 조정
        val availableWidth = measuredWidth - paddingStart - paddingEnd
        if (availableWidth > 0 && text.isNotEmpty() && !isAdjusting) {
            adjustTextSize(availableWidth)
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        // 텍스트 변경 시 원본 크기로 리셋 후 재조정
        if (isOriginalTextSizeSet && !isAdjusting) {
            resetAndRequestLayout()
        }
    }

    /**
     * 텍스트 크기를 원본으로 리셋하고 레이아웃 재요청
     */
    private fun resetAndRequestLayout() {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSizePx)
        requestLayout()
    }

    /**
     * 텍스트가 maxAutoSizeLines 줄에 맞도록 폰트 크기 조정
     */
    private fun adjustTextSize(availableWidth: Int) {
        if (text.isNullOrEmpty() || availableWidth <= 0) return

        isAdjusting = true

        val textString = text.toString()
        val minTextSizePx = spToPx(minAutoSizeTextSize)
        val stepPx = spToPx(autoSizeStepGranularity)

        var currentTextSizePx = originalTextSizePx

        // 현재 크기에서 줄 수 계산
        var lineCount = calculateLineCount(textString, currentTextSizePx, availableWidth)

        // maxAutoSizeLines를 초과하면 폰트 크기 감소
        while (lineCount > maxAutoSizeLines && currentTextSizePx > minTextSizePx) {
            currentTextSizePx = max(currentTextSizePx - stepPx, minTextSizePx)
            lineCount = calculateLineCount(textString, currentTextSizePx, availableWidth)
        }

        // 폰트 크기가 변경되었으면 적용
        if (currentTextSizePx != textSize) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSizePx)
        }

        isAdjusting = false
    }

    /**
     * 주어진 텍스트가 지정된 폰트 크기와 너비에서 몇 줄로 표시되는지 계산
     */
    private fun calculateLineCount(text: String, textSizePx: Float, width: Int): Int {
        val textPaint = TextPaint(paint).apply {
            this.textSize = textSizePx
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .setIncludePad(includeFontPadding)
                .build()
                .lineCount
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                textPaint,
                width,
                Layout.Alignment.ALIGN_CENTER,
                lineSpacingMultiplier,
                lineSpacingExtra,
                includeFontPadding
            ).lineCount
        }
    }

    /**
     * sp 값을 px로 변환
     */
    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
    }

    /**
     * Auto-size 설정을 프로그래밍 방식으로 구성
     *
     * @param minTextSizeSp 최소 폰트 크기 (sp)
     * @param maxLines 최대 허용 줄 수
     * @param stepGranularitySp 크기 조정 단위 (sp)
     */
    fun setAutoSizeConfiguration(
        minTextSizeSp: Float = 12f,
        maxLines: Int = 2,
        stepGranularitySp: Float = 1f
    ) {
        this.minAutoSizeTextSize = minTextSizeSp
        this.maxAutoSizeLines = maxLines
        this.autoSizeStepGranularity = stepGranularitySp
        this.setMaxLines(maxLines)
        requestLayout()
    }
}
