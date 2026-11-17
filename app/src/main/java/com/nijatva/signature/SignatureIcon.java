package com.nijatva.signature;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * The four line-art glyphs the signature screens need, drawn on a canvas.
 *
 * <p>They are drawn rather than shipped as drawables so the kit stays a set of
 * source files: no res/ folder to merge, nothing to collide with an app's own
 * icon set, and the same weight at every size.
 */
public final class SignatureIcon extends View {

    public static final int BACK = 0;
    public static final int PIN = 1;
    public static final int SHARE = 2;
    public static final int MAIL = 3;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final int kind;
    private int colour;

    public SignatureIcon(Context context, int kind, int colour) {
        super(context);
        this.kind = kind;
        this.colour = colour;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setColour(int colour) {
        this.colour = colour;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float size = Math.min(w, h);
        if (size <= 0) {
            return;
        }
        paint.setColor(colour);
        paint.setStrokeWidth(size * 0.085f);
        path.reset();

        switch (kind) {
            case BACK:
                path.moveTo(w * 0.60f, h * 0.24f);
                path.lineTo(w * 0.34f, h * 0.50f);
                path.lineTo(w * 0.60f, h * 0.76f);
                canvas.drawPath(path, paint);
                break;

            case PIN:
                path.addCircle(w * 0.5f, h * 0.40f, w * 0.21f, Path.Direction.CW);
                canvas.drawPath(path, paint);
                path.reset();
                path.moveTo(w * 0.32f, h * 0.55f);
                path.lineTo(w * 0.5f, h * 0.88f);
                path.lineTo(w * 0.68f, h * 0.55f);
                canvas.drawPath(path, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(w * 0.5f, h * 0.40f, w * 0.055f, paint);
                paint.setStyle(Paint.Style.STROKE);
                break;

            case SHARE:
                canvas.drawCircle(w * 0.30f, h * 0.50f, w * 0.075f, paint);
                canvas.drawCircle(w * 0.72f, h * 0.26f, w * 0.075f, paint);
                canvas.drawCircle(w * 0.72f, h * 0.74f, w * 0.075f, paint);
                canvas.drawLine(w * 0.37f, h * 0.45f, w * 0.65f, h * 0.31f, paint);
                canvas.drawLine(w * 0.37f, h * 0.55f, w * 0.65f, h * 0.69f, paint);
                break;

            case MAIL:
            default:
                path.addRoundRect(w * 0.14f, h * 0.26f, w * 0.86f, h * 0.74f,
                        w * 0.08f, h * 0.08f, Path.Direction.CW);
                canvas.drawPath(path, paint);
                path.reset();
                path.moveTo(w * 0.16f, h * 0.30f);
                path.lineTo(w * 0.50f, h * 0.56f);
                path.lineTo(w * 0.84f, h * 0.30f);
                canvas.drawPath(path, paint);
                break;
        }
    }
}
