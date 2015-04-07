package de.predic8.meinediagramanwendung;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class DiagramView extends View {
    public DiagramView(Context context) {
        super(context);
        init();
    }

    public DiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiagramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DiagramView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        if (isInEditMode())
            setF(new Function(){
                @Override
                public double f(double x) {
                    return Math.sin(x);
                }

                @Override
                public String getTextualDescription() {
                    return "sine";
                }
            });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth, measuredHeight;
        double aspectRatio = 4.0/3;

        // thanks to http://stackoverflow.com/a/13846628

        int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int widthSize = widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode( heightMeasureSpec );
        int heightSize = heightMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize( heightMeasureSpec );

        if ( heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY ) {
        /*
         * Possibility 1: Both width and height fixed
         */
            measuredWidth = widthSize;
            measuredHeight = heightSize;

        } else if ( heightMode == MeasureSpec.EXACTLY ) {
        /*
         * Possibility 2: Width dynamic, height fixed
         */
            measuredWidth = (int) Math.min( widthSize, heightSize * aspectRatio );
            measuredHeight = (int) (measuredWidth / aspectRatio);

        } else if ( widthMode == MeasureSpec.EXACTLY ) {
        /*
         * Possibility 3: Width fixed, height dynamic
         */
            measuredHeight = (int) Math.min( heightSize, widthSize / aspectRatio );
            measuredWidth = (int) (measuredHeight * aspectRatio);

        } else {
        /*
         * Possibility 4: Both width and height dynamic
         */
            if ( widthSize > heightSize * aspectRatio ) {
                measuredHeight = heightSize;
                measuredWidth = (int)( measuredHeight * aspectRatio );
            } else {
                measuredWidth = widthSize;
                measuredHeight = (int) (measuredWidth / aspectRatio);
            }

        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    float xOffset;
    float yOffset;
    float scale;

    Paint linePaint;
    float xAxisHeight;
    float yAxisWidth;
    Paint tipPaint;
    float tipSize;
    Path tipX;
    Path tipY;
    Path curve;
    Paint curvePaint;
    float xTick;
    String xTickLabel;
    float xTickLabelWidth;
    float yTick;
    String yTickLabel;
    float tickSize;
    Paint tickTextPaint;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            float dp1 = getContext().getResources().getDisplayMetrics().densityDpi / 160;
            xAxisHeight = getHeight() * 0.5f;
            yAxisWidth = 10*dp1;
            tickSize = 10*dp1;

            if (linePaint == null) {
                linePaint = new Paint();
                linePaint.setColor(Color.BLACK);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeJoin(Paint.Join.ROUND);
                linePaint.setStrokeWidth(dp1);
            }

            tipSize = 10*dp1;


            if (tipPaint == null) {
                tipPaint = new Paint();
                tipPaint.setColor(Color.BLACK);
                tipPaint.setStyle(Paint.Style.FILL);
            }

            tipX = new Path();
            tipX.moveTo(0, 0);
            tipX.lineTo(- tipSize, - 0.5f * tipSize);
            tipX.lineTo(- tipSize, + 0.5f * tipSize);
            tipX.close();

            tipY = new Path();
            tipY.moveTo(0, 0);
            tipY.lineTo(- 0.5f * tipSize, tipSize);
            tipY.lineTo(+ 0.5f * tipSize, tipSize);
            tipY.close();

            if (curvePaint == null) {
                curvePaint = new Paint();
                curvePaint.setColor(Color.RED);
                curvePaint.setStyle(Paint.Style.STROKE);
                curvePaint.setStrokeJoin(Paint.Join.ROUND);
                curvePaint.setStrokeWidth(dp1);
            }

            if (tickTextPaint == null) {
                tickTextPaint = new Paint();
                tickTextPaint.setColor(Color.BLACK);
                tickTextPaint.setStyle(Paint.Style.FILL);
                tickTextPaint.setTextSize(tickSize);
                tickTextPaint.setTextAlign(Paint.Align.LEFT);
            }

            if (scale == 0) {
                scale = 4.8f / getHeight();
                yOffset = - getHeight()/2;
                xOffset = - yAxisWidth;
            }

            render();

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0, xAxisHeight, getWidth(), xAxisHeight, linePaint);
        canvas.drawLine(yAxisWidth, 0, yAxisWidth, getHeight(), linePaint);

        canvas.save();
        canvas.translate(getWidth(), xAxisHeight);
        canvas.drawPath(tipX, tipPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(yAxisWidth, 0);
        canvas.drawPath(tipY, tipPaint);
        canvas.restore();

        canvas.drawPath(curve, curvePaint);

        canvas.drawLine(xTick, xAxisHeight - tickSize/2, xTick, xAxisHeight + tickSize/2, linePaint);
        canvas.drawText(xTickLabel, xTick - xTickLabelWidth / 2, xAxisHeight + tickSize * 1.5f, tickTextPaint);

        canvas.drawLine(yAxisWidth - tickSize/2, yTick, yAxisWidth + tickSize/2, yTick, linePaint);
        canvas.drawText(yTickLabel, yAxisWidth + 3 * tickSize / 4, yTick + tickSize / 3, tickTextPaint);
    }

    public interface Function {
        public double f(double x);
        public String getTextualDescription();
    }

    private Function f;

    public Function getF() {
        return f;
    }

    public void setF(Function f) {
        this.f = f;
        render();
        invalidate();
    }


    private void render() {
        curve = new Path();
        if (f != null) {
            boolean reset = true;
            for (int x = 0; x < getWidth()-1; x++) {

                float fy = (float) f.f((x + xOffset) * scale);

                float y = getHeight() - (fy / scale - yOffset);

                if (y > getHeight() - 1 || y < 0) {
                    reset = true;
                    continue;
                }

                if (reset) {
                    curve.moveTo(x, y);
                    reset = false;
                } else {
                    curve.lineTo(x, y);
                }
            }


            float x = (float) Math.pow(10, Math.floor(Math.log((getWidth()-tipSize + xOffset) * scale) / Math.log(10)));
            xTick = x / scale - xOffset;
            xTickLabel = "" + x;
            if (tickTextPaint != null)
                xTickLabelWidth = tickTextPaint.measureText(xTickLabel);

            float y = (float) Math.pow(10, Math.floor(Math.log((getHeight() - tipSize + yOffset) * scale) / Math.log(10)));

            yTick = getHeight() - (y / scale - yOffset);
            yTickLabel = "" + y;

            yAxisWidth = - xOffset;
            xAxisHeight = getHeight() + yOffset;
        }
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        if (f != null)
            event.getText().add("mathematical plot of " + f.getTextualDescription());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (f != null)
            info.setText("mathematical plot of " + f.getTextualDescription());
    }

    float lastX, lastY;

    private enum State { NONE, TRANSLATE, SCALE }
    State state = State.NONE;

    float lastDistance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                state = State.TRANSLATE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == State.TRANSLATE) {
                    yOffset += event.getY() - lastY;
                    lastY = event.getY();
                    xOffset -= event.getX() - lastX;
                    lastX = event.getX();
                    render();
                    invalidate();
                }
                if (state == State.SCALE) {
                    float currentDistance = (float) Math.sqrt(
                            Math.pow(event.getX(1) - event.getX(), 2) +
                                    Math.pow(event.getY(1) - event.getY(), 2));

                    scale = scale / currentDistance * lastDistance;
                    lastDistance = currentDistance;
                    render();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                state = State.NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (state == State.TRANSLATE) {
                    state = State.SCALE;

                    lastDistance = (float) Math.sqrt(
                            Math.pow(event.getX(1) - event.getX(), 2) +
                                    Math.pow(event.getY(1) - event.getY(), 2));
                } else {
                    state = State.NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                state = State.NONE;
                break;
        }
        return true;
    }


    // Zustand speichern und wiederherstellen: s.a. http://stackoverflow.com/a/3542895

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.xOffset = this.xOffset;
        ss.yOffset = this.yOffset;
        ss.scale = this.scale;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        this.xOffset = ss.xOffset;
        this.yOffset = ss.yOffset;
        this.scale = ss.scale;
    }

    static class SavedState extends BaseSavedState {
        float xOffset;
        float yOffset;
        float scale;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.xOffset = in.readFloat();
            this.yOffset = in.readFloat();
            this.scale = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(xOffset);
            out.writeFloat(yOffset);
            out.writeFloat(scale);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
