package com.rookandpawn.kami.ui;

import java.util.Random;


/**
 * simple representation of an rgb color
 */
public class Color {

  public static final Color WHITE = new Color(255, 255, 255);
  public static final Color BLACK = new Color(0, 0, 0);
  public static final Color RED = new Color(255, 0, 0);

  private static double mod(double v, int n) {
    if (v >= 0 && v <= n) {
      return v;
    }
    if (v > n) {
      return mod(v - n, n);
    }
    return mod(v + n, n);
  }

  /**
   * @return a random color
   */
  public static final Color random() {
    Random rand = new Random();
    return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
  }

  /**
   * Get the standard deviation of all the pixels in the given image
   * @param image image to get std dev across
   * @return an array of the form [R-stdDev, G-stdDev, B-stdDev]
   */
  public static double[] getStdDev(Iterable<Color> image) {
    Color avg = average(image);

    return getStdDev(image, avg);
  }

  /**
   * Get the standard deviation of all the pixels in the given image
   * @param image image to get std dev across
   * @param avg precomputed average
   * @return an array of the form [R-stdDev, G-stdDev, B-stdDev]
   */
  public static double[] getStdDev(Iterable<Color> image, Color avg) {
    
    double[] result = new double[3];

    int count = 0;

    for (Color color : image) {
      if (color == null) {
        continue;
      }

      count++;

      double dr = color.r - avg.r;
      double dg = color.g - avg.g;
      double db = color.b - avg.b;

      result[0] += dr * dr;
      result[1] += dg * dg;
      result[2] += db * db;
    }

    for (int i = 0; i < 3; i++) {
      result[i] /= count;
    }

    return result;
  }

  /**
   * Produce a color that is the average of the given colors
   * @param colors
   * @return 
   */
  public static Color average(Iterable<Color> colors) {

    int count = 0;
    double rSum = 0;
    double gSum = 0;
    double bSum = 0;

    for (Color color : colors) {
      if (color == null) {
        continue;
      }

      count++;

      rSum += color.r;
      gSum += color.g;
      bSum += color.b;
    }

    return new Color((int)Math.round(rSum / count)
        , (int)Math.round(gSum / count)
        , (int)Math.round(bSum / count));
  }

  private static double pivotRgb(double n) {
    return (n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92) * 100.0;
  }

  private static double CubicRoot(double n) {
    return Math.pow(n, 1.0 / 3.0);
  }

  private static double pivotXyz(double n) {
    return n > epsilon ? CubicRoot(n) : (kappa * n + 16) / 116;
  }

  private static double epsilon = 0.008856; // Intent is 216/24389
  private static double kappa = 903.3; // Intent is 24389/27

  private static double whiteX = 95.047;
  private static double whiteY = 100;
  private static double whiteZ = 108.883;

  public static final short MAX_VALUE = 255;

  private final short r;
  private final short g;
  private final short b;

  private double L = -1;
  private double A;
  private double B;

  private double h = -1;
  private double s;
  private double v;

  public Color(int r, int g, int b) {
    this.r = (short)r;
    this.g = (short)g;
    this.b = (short)b;
  }

  /**
   * Create a new new color with hue saturation and value
   * @param h
   * @param s
   * @param v
   */
  public Color(double h, double s, double v) {
    if (h < 0) {
      h = 0;
    }
    else if (h > 360) {
      h = 360;
    }

    if (s < 0) {
      s = 0;
    }
    else if (s > 1) {
      s = 1;
    }

    if (v < 0) {
      v = 0;
    }
    else if (v > 1) {
      v = 1;
    }


    this.h = h;
    this.s = s;
    this.v = v;


    double c = v * s;
    double x = c * (1 - Math.abs(mod(h / 60, 2) - 1));
    double m = v - c;

    double rp = 0;
    double gp = 0;
    double bp = 0;

    if (h < 60) {
      rp = c;
      gp = x;
    }
    else if (h < 120) {
      rp = x;
      gp = c;
    }
    else if (h < 180) {
      gp = c;
      bp = x;
    }
    else if (h < 240) {
      gp = x;
      bp = c;
    }
    else if (h < 300) {
      rp = x;
      bp = c;
    }
    else {
      rp = c;
      bp = x;
    }

    r = (short)Math.round((rp + m) * 255);
    g = (short)Math.round((gp + m) * 255);
    b = (short)Math.round((bp + m) * 255);
  }

  /**
   * @return the r
   */
  public short getR() {
    return r;
  }

  /**
   * @return the g
   */
  public short getG() {
    return g;
  }

  /**
   * @return the b
   */
  public short getB() {
    return b;
  }

  public double getH() {
    ensureHsv();
    return h;
  }

  public double getS() {
    ensureHsv();
    return s;
  }

  public double getV() {
    ensureHsv();
    return v;
  }

  public Color plusValue(double deltaV) {
    ensureHsv();
    return new Color(h, s, v + deltaV);
  }

  private void ensureLab() {
    if (L > 0) {
      return;
    }

    double rr = pivotRgb(1 / 255.0 * r);
    double gg = pivotRgb(1 / 255.0 * g);
    double bb = pivotRgb(1 / 255.0 * b);

    // Observer. = 2°, Illuminant = D65
    double x = rr * 0.4124 + gg * 0.3576 + bb * 0.1805;
    double y = rr * 0.2126 + gg * 0.7152 + bb * 0.0722;
    double z = rr * 0.0193 + gg * 0.1192 + bb * 0.9505;

    double xx = pivotXyz(x / whiteX);
    double yy = pivotXyz(y / whiteY);
    double zz = pivotXyz(z / whiteZ);

    L = Math.max(0, 116 * yy - 16);
    A = 500 * (xx - yy);
    B = 200 * (yy - zz);
  }

  /**
   * Ensure that the hsv color for this color has been calculated
   */
  private void ensureHsv() {
    if (h >= 0) {
      return;
    }

    double rp = 1.0 / 255 * r;
    double gp = 1.0 / 255 * g;
    double bp = 1.0 / 255 * b;

    double cMin;
    double cMax;

    boolean redMax = false;
    boolean blueMax = false;

    if (rp > gp) {
      if (rp > bp) {
        cMax = rp;
        cMin = gp > bp ? bp : gp;
        redMax = true;
      }
      else {
        cMax = bp;
        cMin = gp;
        blueMax = true;
      }
    }
    else {
      if (gp > bp) {
        cMax = gp;
        cMin = rp > bp ? bp : rp;
      }
      else {
        cMax = bp;
        cMin = rp;
        blueMax = true;
      }
    }

    double delta = cMax - cMin;

    if (delta < 0.000001) {
      h = 0;
    }
    else if (redMax) {
      double f = (gp - bp) / delta;

      f = f < 0 ? 6 - f : f;
      h = 60 * f;
    }
    else if (blueMax) {
      h = 60 * ((rp - gp) / delta + 4);
    }
    else {
      h = 60 * ((bp - rp) / delta + 2);
    }

    s = cMax < 0.00001 ? 0 : delta / cMax;

    v = cMax;
  }

  @Override
  public String toString() {
    return new StringBuilder("#")
        .append(r < 16 ? "0" : "")
        .append(Integer.toString(r, 16))
        .append(g < 16 ? "0" : "")
        .append(Integer.toString(g, 16))
        .append(b < 16 ? "0" : "")
        .append(Integer.toString(b, 16))
        .toString();
  }

  public double getHsvDistanceSquaredTo(Color other) {
    this.ensureHsv();
    other.ensureHsv();

    double dx = s * Math.cos(h * Math.PI / 180)
        - other.s * Math.cos(other.h * Math.PI / 180);
    double dy = s * Math.sin(h * Math.PI / 180)
        - other.s * Math.sin(other.h * Math.PI / 180);

    double dv = v - other.v;

    return dx * dx + dy * dy + dv * dv;
  }

  public double getLabDistanceSquaredTo(Color other) {
    ensureLab();
    other.ensureLab();

    double dl = other.L - L;
    double da = other.A - A;
    double db = other.B - B;

    return dl * dl + da * da + db * db;
  }


  /**
   * @param otherColor the color to the get the distance^2 to
   * @return the distance squared to the given color in rgb space
   */
  public double getRgbDistanceSquaredTo(Color otherColor) {
    double rBar = 0.5 * (r + otherColor.r);
    int dr = r - otherColor.r;
    int dg = g - otherColor.g;
    int db = b - otherColor.b;

    return (2.0 + rBar / 256) * dr * dr
        + 4 * dg * dg 
        + (2.0 + (255.0 - rBar) / 256) * db * db;
  }

  /**
   * @param otherColor the color to get the distance to
   * @return the distance to the given color
   */
  public double getRgbDistanceTo(Color otherColor) {
    return Math.sqrt(getRgbDistanceSquaredTo(otherColor));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + this.r;
    hash = 59 * hash + this.g;
    hash = 59 * hash + this.b;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Color other = (Color) obj;
    if (this.r != other.r) {
      return false;
    }
    if (this.g != other.g) {
      return false;
    }
    return this.b == other.b;
  }
}
