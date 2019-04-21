package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class DrawAlgorithms {
    private GraphicsContext gc;
    private Color background;
    public static final String ALG_CANONIC = "Каноническое уравнение";
    public static final String ALG_PARAMETRIC = "Параметрическое уравнение";
    public static final String ALG_BRESENHAM = "Алгоритм Брезенхема";
    public static final String ALG_MIDDLE_POINT = "Алгоритм средней точки";
    public static final String ALG_LIB = "Библиотечный";

    public DrawAlgorithms(GraphicsContext gc, Color background) {
        this.gc = gc;
        this.background = background;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    private void drawSymmetricPixels(PixelWriter writer, int xc, int yc, int curX, int curY, Color color) {
        writer.setColor(xc + curX, yc + curY, color);
        writer.setColor(xc + curX, yc - curY, color);
        writer.setColor(xc - curX, yc + curY, color);
        writer.setColor(xc - curX, yc - curY, color);
//        System.out.println((xc + curX) + " " + (yc + curY));
//        System.out.println((xc + curX) + " " + (yc - curY));
//        System.out.println((xc - curX) + " " + (yc + curY));
//        System.out.println((xc - curX) + " " + (yc - curY));
    }

    // Канонический эллипс полностью перекрывает каноническую окружность при a = b = radius

    private void canonicSingleCircle(int xc, int yc, int radius, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        int curY = radius;
        for (int curX = 0; curX <= curY; curX++) {
            curY = round(Math.sqrt(radius * radius - curX * curX));
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
            drawSymmetricPixels(writer, xc, yc, curY, curX, color);
        }
    }

    private void canonicSingleEllipse(int xc, int yc, int a, int b, Color color) {
        if (a <= 0 || b <= 0) {
            return;
        }

        PixelWriter writer = gc.getPixelWriter();
        int a2 = a * a;
        int b2 = b * b;
        for (int curX = 0; curX < a + 1; curX++) {
            int curY = round(b * Math.sqrt(1.0 - (double) curX * curX / a2));
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
        }

        for (int curY = 0; curY < b + 1; curY++) {
            int curX = round(a * Math.sqrt(1.0 - (double) curY * curY / b2));
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
        }
    }

    // Параметрический эллипс и каноническая окружность могут различаться в небольшом числе точек
    private void parametricSingleCircle(int xc, int yc, int radius, Color color) {
        if (radius <= 0) {
            return;
        }
        PixelWriter writer = gc.getPixelWriter();
        double oct = Math.PI * radius / 4.0;
        double delta = 1.0 / radius;
        for (double theta = 0; theta < oct + delta / 2.0; theta += delta) {
            int curX = round(radius * Math.cos(theta));
            int curY = round(radius * Math.sin(theta));
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
            drawSymmetricPixels(writer, xc, yc, curY, curX, color);
        }
    }

    private void parametricSingleEllipse(int xc, int yc, int a, int b, Color color) {
        if (a <= 0 || b <= 0) {
            return;
        }

        PixelWriter writer = gc.getPixelWriter();
        int max = Math.max(a, b);
        double quart = Math.PI * max / 2.0;
        double delta = 1.0 / max;
        for (double theta = 0; theta < quart + delta / 2.0; theta += delta) {
            int curX = round(a * Math.cos(theta));
            int curY = round(b * Math.sin(theta));
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
        }
    }

    private void bresenhamSingleCircle(int xc, int yc, int radius, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        int curX = 0;
        int curY = radius;
        int d = ((1 - radius) << 1);
        while (curY >= curX) {
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);
            drawSymmetricPixels(writer, xc, yc, curY, curX, color);
            if (d < 0) {
                int buf = ((d + curY) << 1) - 1;
                curX++;
                if (buf <= 0) {
                    d += (curX << 1) + 1;
                } else {
                    curY--;
                    d += ((curX - curY + 1) << 1);
                }
                continue;
            }

            if (d > 0) {
                int buf = ((d - curX) << 1) - 1;
                curY--;
                if (buf > 0) {
                    d -= ((curY << 1) - 1);
                } else {
                    curX++;
                    d += ((curX - curY + 1) << 1);
                }
                continue;
            }

            curX++;
            curY--;
            d += ((curX - curY + 1) << 1);
        }
    }

    private void bresenhamSingleEllipse(int xc, int yc, int a, int b, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        int curX = 0;
        int curY = b;
        int b2 = b * b;
        int a2 = a * a;
        int d = b2 + a2 * (1 - (curY << 1));
        while (curY >= 0) {
            drawSymmetricPixels(writer, xc, yc, curX, curY, color);

            if (d < 0) {
                int buf = ((d + a2 * curY) << 1) - 1;
                curX++;
                if (buf <= 0) {
                    d += b2 * ((curX << 1) + 1);
                } else {
                    curY--;
                    d += b2 * ((curX << 1) + 1) + a2 * (1 - (curY << 1));
                }
                continue;
            }

            if (d > 0) {
                int buf = (d << 1) - b2 * ((curX << 1) - 1);
                curY--;

                if (buf > 0) {
                    d += a2 * (1 - (curY << 1));
                } else {
                    curX++;
                    d += b2 * ((curX << 1) + 1) + a2 * (1 - (curY << 1));
                }
                continue;
            }
            curX++;
            curY--;
            d += b2 * ((curX << 1) + 1) + a2 * (1 - (curY << 1));
        }
    }


    // Полностью сходятся между собой при a = b = radius
    private void middleSingleCircle(int xc, int yc, int radius, Color color) {
        if (radius < 0) {
            return;
        }

        PixelWriter writer = gc.getPixelWriter();
        int curX = radius, curY = 0;

        writer.setColor(xc + curX, curY + yc, color);
        if (radius > 0) {
            writer.setColor(xc - curX, yc - curY, color);
            writer.setColor(xc + curY, yc - curX, color);
            writer.setColor(xc - curY, yc + curX, color);
        }

        int P = (int) (1.25 - radius);
        while (curX >= curY) {
            curY++;

            if (P <= 0) {
                P += (curY << 1) + 1;
            } else {
                curX--;
                P += ((curY - curX) << 1) + 1;
            }

            drawSymmetricPixels(writer, xc, yc, curX, curY, color);

            if (curX != curY) {
                drawSymmetricPixels(writer, xc, yc, curY, curX, color);
            }
        }
    }

    private void middleSingleEllipse(int xc, int yc, int a, int b, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        double dx, dy, d, curX, curY;
        curX = 0;
        curY = b;
        int b2 = b * b;
        int a2 = a * a;
        int b22 = b2 << 1;
        int a22 = a2 << 1;
        d = b2 + a2 * (0.25 - b);
        dx = b22 * curX;
        dy = a22 * curY;

        while (dx < dy) {
            int curXInt = (int) curX;
            int curYInt = (int) curY;

            drawSymmetricPixels(writer, xc, yc, curXInt, curYInt, color);
            if (d < 0) {
                curX++;
                dx += b22;
                d += dx + b2;
            } else {
                curX++;
                curY--;
                dx += b22;
                dy -= a22;
                d += dx - dy + b2;
            }
        }

        d = b2 * (curX + 0.5) * (curX + 0.5) + a2 * (curY - 1) * (curY - 1) - a2 * b2;
        while (curY >= 0) {
            int curXInt = (int) curX;
            int curYInt = (int) curY;

            drawSymmetricPixels(writer, xc, yc, curXInt, curYInt, color);

            if (d > 0) {
                curY--;
                dy -= a22;
                d += a2 - dy;
            } else {
                curY--;
                curX++;
                dx += b22;
                dy -= a22;
                d += dx - dy + a2;
            }
        }
    }


    // библиотечные, разумеется, пересекаются между собой
    private void libSingleCircle(int xc, int yc, int radius, Color color) {
        gc.setStroke(color);
        gc.strokeOval(xc - radius, yc - radius, 2 * radius, 2 * radius);
    }

    private void libSingleEllipse(int xc, int yc, int a, int b, Color color) {
        gc.setStroke(color);
        gc.strokeOval(xc - a, yc - b, 2 * a, 2 * b);
    }

    public void runSingleCircle(String alg, int xc, int yc, int radius, Color color) {
        switch (alg) {
            case ALG_CANONIC:
                canonicSingleCircle(xc, yc, radius, color);
                break;
            case ALG_PARAMETRIC:
                parametricSingleCircle(xc, yc, radius, color);
                break;
            case ALG_BRESENHAM:
                bresenhamSingleCircle(xc, yc, radius, color);
                break;
            case ALG_MIDDLE_POINT:
                middleSingleCircle(xc, yc, radius, color);
                break;
            case ALG_LIB:
                libSingleCircle(xc, yc, radius, color);
                break;
        }
    }

    public void runMultiCircle(String alg, int xc, int yc, int rb, int re, int count, Color color) {
        double step = (double) (re - rb) / (count - 1);
        for (int r = rb; r < re + step / 2; r += step) {
            runSingleCircle(alg, xc, yc, r, color);
        }
    }

    public void runSingleEllipse(String alg, int xc, int yc, int a, int b, Color color) {
        switch (alg) {
            case ALG_CANONIC:
                canonicSingleEllipse(xc, yc, a, b, color);
                break;
            case ALG_PARAMETRIC:
                parametricSingleEllipse(xc, yc, a, b, color);
                break;
            case ALG_BRESENHAM:
                bresenhamSingleEllipse(xc, yc, a, b, color);
                break;
            case ALG_MIDDLE_POINT:
                middleSingleEllipse(xc, yc, a, b, color);
                break;
            case ALG_LIB:
                libSingleEllipse(xc, yc, a, b, color);
                break;
        }
    }

    public void runMultiEllipse(String alg, int xc, int yc, int d, int aBeg, int bBeg, int count, Color color) {
        for (int i = 0; i < count; i++) {
            runSingleEllipse(alg, xc, yc, aBeg, bBeg, color);
            if (aBeg > bBeg) {
                aBeg += d;
                bBeg += ((double) d * bBeg) / aBeg;
            } else {
                aBeg += ((double) d * aBeg) / bBeg;
                bBeg += d;
            }
        }
    }
}
