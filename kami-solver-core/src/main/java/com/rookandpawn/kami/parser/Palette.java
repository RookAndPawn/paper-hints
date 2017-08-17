package com.rookandpawn.kami.parser;

import java.util.List;

import com.google.common.collect.Lists;
import com.rookandpawn.kami.ui.KamiImage;
import com.rookandpawn.kami.ui.PaletteList;

/**
 * Collection of small images extracted from the hud that represents the set of
 * discrete colors/patterns that appear in the puzzle area
 */
public class Palette {

  public static Builder builder() {
    return new Builder();
  }

  private final List<KamiImage> samples;
  private final List<ImageCharacteristic> characteristics;

  private Palette(List<KamiImage> samples) {
    this.samples = samples;
    this.characteristics = Lists.newArrayList();

    for (KamiImage sample : samples) {
      characteristics.add(XMeansClusterer.cluster(sample));
    }
  }

  /**
   * Get the index of the palette sample that best matches the given image tile
   * @param image image to match against the samples in the palette
   * @param valueOffset shift in the color value of the current image
   * @return the index of the palette sample that matches the given image best
   */
  public int getBestMatch(KamiImage image, double valueOffset) {
    double minDist = Double.MAX_VALUE;
    int result = -1;

    ImageCharacteristic c = XMeansClusterer
        .cluster(image)
        .shiftColorValues(valueOffset);

    for (int i = 0; i < characteristics.size(); i++) {
      ImageCharacteristic currChar = characteristics.get(i);
      double dist = currChar.compareTo(c);

      if (dist < minDist) {
        minDist = dist;
        result = i;
      }
    }

    return result;
  }

  /**
   * @return the number of samples in the palette
   */
  public int size() {
    return samples.size();
  }

  /**
   * @param index index of the sample to get
   * @return the sample at the given index
   */
  public KamiImage getSample(int index) {
    return samples.get(index);
  }


  public PaletteList toPaletteList() {
    return new PaletteList(samples);
  }

  /**
   * Builder class as a of building fluidly
   */
  public static class Builder {
    private final List<KamiImage> samples = Lists.newArrayList();

    public Builder addSample(KamiImage sample) {
      this.samples.add(sample);
      return this;
    }

    public Palette build() {
      return new Palette(samples);
    }
  }

}
