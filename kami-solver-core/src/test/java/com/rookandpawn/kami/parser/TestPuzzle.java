
package com.rookandpawn.kami.parser;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.rookandpawn.kami.solver.Graph;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * enumeration of the test puzzles
 */
public enum TestPuzzle {

  K2_PU_0_IpadPro7(new Builder("k2-pu-0-ipadpro7.PNG")
      .withDevice(Device.IpadPro7)
      .withHudOrientation(HudOrientation.Side)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(3))

  , K2_PU_1_IphoneSE(new Builder("k2-pu-1-iphonese.PNG")
      .withDevice(Device.IphoneSE)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(5)
      .withClassifications("" +
          "4332242211\n" +
          "4432442211\n" +
          "2444444214\n" +
          "2244114444\n" +
          "2341111442\n" +
          "3342113422\n" +
          "3342233432\n" +
          "3342233433\n" +
          "3042233433\n" +
          "0044234 33\n" +
          "0334444  3\n" +
          "33334411  \n" +
          "233141111 \n" +
          "2211431120\n" +
          "2211433220\n" +
          "2211433220\n" +
          " 214033220\n" +
          "  44003244\n" +
          "4443300444\n" +
          "4433330411\n" +
          "1423310111\n" +
          "1422110211\n" +
          "142211 223\n" +
          "142211 223\n" +
          "34421  223\n" +
          "2244  4423\n" +
          "2224 11442\n" +
          "2214111141\n" +
          "3114311241"))

  , K2_PU_2_Iphone7(new Builder("k2-pu-2-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(5)
      .withClassifications("" +
          "3333333333\n" +
          "3233223323\n" +
          "3223223223\n" +
          "3223223223\n" +
          "2221111222\n" +
          "2421111242\n" +
          "2111401112\n" +
          "2113224112\n" +
          "1142222311\n" +
          "1022112241\n" +
          "1221110221\n" +
          "4244330020\n" +
          "1242231021\n" +
          "3242231024\n" +
          "1241111321\n" +
          "4231044323\n" +
          "1231044321\n" +
          "0233003324\n" +
          "1223222221\n" +
          "1422222201\n" +
          "1132222411\n" +
          "2114223112\n" +
          "2111041112\n" +
          "2421111242\n" +
          "2221111222\n" +
          "3223223223\n" +
          "3223223223\n" +
          "3233223323\n" +
          "3333333333"))

  , K2_PU_3_Iphone7(new Builder("k2-pu-3-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(5))

  , K2_PU_4_Iphone7(new Builder("k2-pu-4-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(5))

  , K2_P4_6_Iphone7(new Builder("k2-p4_6-iphone7.png")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(4)
      .withClassifications("" +
          "1220112201\n" +
          "1220112201\n" +
          "1220110201\n" +
          "1020110001\n" +
          "1000110001\n" +
          "1000110201\n" +
          "3000110221\n" +
          "3300110221\n" +
          "1330110221\n" +
          "1233110021\n" +
          "1223310001\n" +
          "1220330001\n" +
          "3220132001\n" +
          "3320112201\n" +
          "2300112201\n" +
          "2000112203\n" +
          "2000210203\n" +
          "2200230001\n" +
          "1220233001\n" +
          "1220223201\n" +
          "1220222231\n" +
          "1020222233\n" +
          "1000122223\n" +
          "1000112222\n" +
          "1020110222\n" +
          "1022110022\n" +
          "1022110002\n" +
          "1022110001\n" +
          "1002110001"))

  , K2_P10_5_Iphone7(new Builder("k2-p10_5-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(4))

  , K2_P11_6_Iphone7(new Builder("k2-p11_6-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_2)
      .withPaletteSize(3))

  , K1_P1_5_IpadPro7(new Builder("k1-p1_5-ipadpro7.PNG")
      .withDevice(Device.IpadPro7)
      .withHudOrientation(HudOrientation.Side)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(3)
      .withClassifications(""
          + "0001101100\n"
          + "0011000110\n"
          + "0110020011\n"
          + "1100222001\n"
          + "1002202200\n"
          + "1022010220\n"
          + "1002202200\n"
          + "1100222001\n"
          + "0110020011\n"
          + "0011000110\n"
          + "0001101100\n"
          + "2000111000\n"
          + "2200010002\n"
          + "0220000022\n"
          + "0022000220\n"
          + "0002202200"
      ))

  , k1_P1_6_IphoneSe(new Builder("k1-p1_6-iphonese.PNG")
      .withDevice(Device.IphoneSE)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(3)
      .withClassifications(
          "1122200022\n" +
          "1122200022\n" +
          "1122200022\n" +
          "1122200000\n" +
          "1222201100\n" +
          "1001101100\n" +
          "1001101100\n" +
          "1001100000\n" +
          "1221100000\n" +
          "0021101122\n" +
          "0021101122\n" +
          "0021101122\n" +
          "0112202211\n" +
          "0112202211\n" +
          "0112202211\n" +
          "0000002211")
      .withGraph(Graph.builder()
          .withNode(1, "1")
          .withNode(2, "2")
          .withNode(3, "0")
          .withNode(4, "2")
          .withNode(5, "1")
          .withNode(6, "0")
          .withNode(7, "1")
          .withNode(8, "2")
          .withNode(9, "1")
          .withNode(10, "2")
          .withNode(11, "1")
          .withNode(12, "2")
          .withNode(13, "2")
          .withNode(14, "1")
          .withEdges(1, 2, 3, 6, 8)
          .withEdges(2, 3, 6, 7)
          .withEdges(3, 4, 5, 7, 8, 9, 10, 11, 12, 13)
          .withEdges(6, 7, 8)
          .withEdges(7, 8, 12)
          .withEdges(8, 11)
          .withEdges(9, 10, 13)
          .withEdges(10, 14)
          .withEdges(11, 12)
          .withEdges(13, 14)
      ))

  , k1_P2_9_Iphone7(new Builder("k1-p2_9-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(4)
      .withClassifications(
          "2222220203\n" +
          "2113003110\n" +
          "2113003112\n" +
          "2332002330\n" +
          "2111220002\n" +
          "1102002000\n" +
          "0002002002\n" +
          "3303113022\n" +
          "3303113022\n" +
          "0002002002\n" +
          "1102002000\n" +
          "2111220002\n" +
          "2332002330\n" +
          "2113003112\n" +
          "2113003110\n" +
          "2222220203"))

  , k1_P8_7_Iphone7(new Builder("k1-p8_7-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(5)
      .withClassifications(
          "0223143311\n" +
          "3132201022\n" +
          "1310140210\n" +
          "3031014100\n" +
          "0400234233\n" +
          "4032101121\n" +
          "3220033310\n" +
          "1124133322\n" +
          "4443313022\n" +
          "4432231002\n" +
          "3311211102\n" +
          "1133221100\n" +
          "1113322130\n" +
          "0004422310\n" +
          "0033442310\n" +
          "3331304400"))

  , k1_P8_8_Iphone7(new Builder("k1-p8_8-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(5)
  )

  , k1_P8_9_Iphone7(new Builder("k1-p8_9-iphone7.PNG")
      .withDevice(Device.Iphone7)
      .withHudOrientation(HudOrientation.Bottom)
      .withVersion(KamiVersion.VERSION_1)
      .withPaletteSize(5)
  )


  ;

  public final String resourceName;
  public final Device device;
  public final HudOrientation hudOrientation;
  public final KamiVersion version;
  public final int paletteSize;
  public final String classifications;
  public final Graph graph;

  public KamiImage loadImage() {
    try {
      BufferedImage image = ImageIO.read(this.getClass().getClassLoader()
          .getResource(resourceName));
      return new BufferedJavaImage(image).toKamiImage();
    }
    catch (IOException ex) {
      throw new RuntimeException("Failed to load " + resourceName, ex);
    }
  }

  private TestPuzzle(Builder builder) {
    this.resourceName = builder.resourceName;
    this.device = builder.device;
    this.hudOrientation = builder.hudOrientation;
    this.version = builder.version;
    this.paletteSize = builder.paletteSize;
    this.classifications = builder.classifications;
    this.graph = builder.graph;
  }

  /**
   * Mechanism for fluidly building a test puzzle instance
   */
  private static class Builder {

    final String resourceName;
    Device device;
    HudOrientation hudOrientation;
    KamiVersion version;
    int paletteSize;
    String classifications;
    Graph graph;

    Builder(String resourceName) {
      this.resourceName = resourceName;
    }

    Builder withDevice(Device device) {
      this.device = device;
      return this;
    }

    Builder withHudOrientation(HudOrientation hudOrientation) {
      this.hudOrientation = hudOrientation;
      return this;
    }

    Builder withVersion(KamiVersion version) {
      this.version = version;
      return this;
    }

    Builder withPaletteSize(int paletteSize) {
      this.paletteSize = paletteSize;
      return this;
    }

    Builder withClassifications(String classifications) {
      this.classifications = classifications;
      return this;
    }

    Builder withGraph(Graph.Builder graphBuilder) {
      this.graph = graphBuilder.build();
      return this;
    }
  }

}
