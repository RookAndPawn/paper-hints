package com.rookandpawn.kami.solver;

import java.util.Objects;

/**
 * representation of a action taken to get to a certain step
 */
public class Move {
  private final short node;
  private final String color;
  private final byte colorIndex;

  public Move(short node, String color, byte colorIndex) {
    this.node = node;
    this.color = color;
    this.colorIndex = colorIndex;
  }

  /**
   * @return the node
   */
  public short getNode() {
    return node;
  }

  /**
   * @return the color
   */
  public String getColor() {
    return color;
  }

  /**
   * @return the colorIndex
   */
  public byte getColorIndex() {
    return colorIndex;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + this.node;
    hash = 29 * hash + Objects.hashCode(this.color);
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
    final Move other = (Move) obj;
    if (this.node != other.node) {
      return false;
    }
    if (!Objects.equals(this.color, other.color)) {
      return false;
    }
    return true;
  }



  @Override
  public String toString() {
    return new StringBuilder()
        .append("Set ")
        .append(node)
        .append(" to ")
        .append(color)
        .toString();
  }

}
