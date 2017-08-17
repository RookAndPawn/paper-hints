//
//  RGBA32.swift
//  KamiSolver
//
//  Created by Kevin Guthrie on 6/28/17.
//  Copyright © 2017 Rook And Pawn Industries, Inc. All rights reserved.
//

import UIKit
import KamiSolverCore

struct RGBA32: Equatable {
  private var color: UInt32
  
  var redComponent: UInt8 {
    return UInt8((color >> 24) & 255)
  }
  
  var greenComponent: UInt8 {
    return UInt8((color >> 16) & 255)
  }
  
  var blueComponent: UInt8 {
    return UInt8((color >> 8) & 255)
  }
  
  var alphaComponent: UInt8 {
    return UInt8((color >> 0) & 255)
  }
  
  init(color : RPUIColor?) {
    
    if ((color) != nil) {
      self.init(red: UInt8((color?.getR())!)
        , green: UInt8((color?.getG())!)
        , blue: UInt8((color?.getB())!)
        , alpha: UInt8(255))
    }
    else {
      self.init(red: 0, green: 0, blue: 0, alpha: 255)
    }
  
  }
  
  init(red: UInt8, green: UInt8, blue: UInt8, alpha: UInt8) {
    color = (UInt32(red) << 24) | (UInt32(green) << 16) | (UInt32(blue) << 8) | (UInt32(alpha) << 0)
  }
  
  func toColor() -> RPUIColor {
    return RPUIColor(int: jint(redComponent), with: jint(greenComponent), with: jint(blueComponent))
  }
  
  static let red     = RGBA32(red: 255, green: 0,   blue: 0,   alpha: 255)
  static let green   = RGBA32(red: 0,   green: 255, blue: 0,   alpha: 255)
  static let blue    = RGBA32(red: 0,   green: 0,   blue: 255, alpha: 255)
  static let white   = RGBA32(red: 255, green: 255, blue: 255, alpha: 255)
  static let black   = RGBA32(red: 0,   green: 0,   blue: 0,   alpha: 255)
  static let magenta = RGBA32(red: 255, green: 0,   blue: 255, alpha: 255)
  static let yellow  = RGBA32(red: 255, green: 255, blue: 0,   alpha: 255)
  static let cyan    = RGBA32(red: 0,   green: 255, blue: 255, alpha: 255)
  
  static let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue | CGBitmapInfo.byteOrder32Little.rawValue
  
  static func ==(lhs: RGBA32, rhs: RGBA32) -> Bool {
    return lhs.color == rhs.color
  }
}

