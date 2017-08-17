//
//  IosImage.swift
//  KamiSolver
//
//  Created by Kevin Guthrie on 6/27/17.
//  Copyright © 2017 Rook And Pawn Industries, Inc. All rights reserved.
//

import CoreGraphics
import UIKit
import KamiSolverCore

class IosImage: NSObject {
  
  static let lineThicknessFraction : CGFloat = 4.0 / 750
  static let dashLengthFraction : CGFloat = 16.0 / 750
  static let dark = UIColor(red: 0.1, green: 0.1, blue: 0.1, alpha: 1.0).cgColor
  static let light = UIColor(red: 0.9, green: 0.9, blue: 0.9, alpha: 1.0).cgColor
  static let dotRadiusFraction : CGFloat = 8.0 / 750
  
  class func toUiImage(javaImage : RPUIKamiImage) -> UIImage {
    let width = Int(javaImage.getWidth())
    let height = Int(javaImage.getHeight())
    let colorSpace       = CGColorSpaceCreateDeviceRGB()
    let bytesPerPixel    = 4
    let bitsPerComponent = 8
    let bytesPerRow      = bytesPerPixel * width
    let bitmapInfo       = RGBA32.bitmapInfo
    
    let context = CGContext(data: nil, width: width, height: height, bitsPerComponent: bitsPerComponent, bytesPerRow: bytesPerRow, space: colorSpace, bitmapInfo: bitmapInfo)
    
    let buffer = context?.data
    
    
    let pixelBuffer = buffer?.bindMemory(to: RGBA32.self, capacity: width * height)
    
    for row in 0 ..< Int(height) {
        for column in 0 ..< Int(width) {
            pixelBuffer?[row * width + column]
                = RGBA32(color: javaImage.getPixelWith(jint(row), with: jint(column)))
        }
    }
    
    let outputCGImage = context?.makeImage()!
    let outputImage = UIImage(cgImage: outputCGImage!)
    
    return outputImage
  }
  
  class func cropToBounds(image: UIImage, width: Double, height: Double) -> UIImage {
    
    let contextImage: UIImage = UIImage(cgImage: image.cgImage!)
    
    let contextSize: CGSize = contextImage.size
    
    var posX: CGFloat = 0.0
    var posY: CGFloat = 0.0
    var cgwidth: CGFloat = CGFloat(width)
    var cgheight: CGFloat = CGFloat(height)
    
    // See what size is longer and create the center off of that
    if contextSize.width > contextSize.height {
      posX = ((contextSize.width - contextSize.height) / 2)
      posY = 0
      cgwidth = contextSize.height
      cgheight = contextSize.height
    } else {
      posX = 0
      posY = ((contextSize.height - contextSize.width) / 2)
      cgwidth = contextSize.width
      cgheight = contextSize.width
    }
    
    let rect = CGRect(x: posX, y: posY, width: cgwidth, height: cgheight)
    
    
    // Create bitmap image from context using the rect
    let imageRef: CGImage = contextImage.cgImage!.cropping(to: rect)!
    
    // Create a new image based on the imageRef and rotate back to the original orientation
    let image = UIImage(cgImage: imageRef, scale: image.scale, orientation: image.imageOrientation)
    
    return image
  }
  
  class func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
    let size = image.size
    
    let widthRatio  = targetSize.width  / image.size.width
    let heightRatio = targetSize.height / image.size.height
    
    // Figure out what our orientation is, and use that to form the rectangle
    var newSize: CGSize
    if(widthRatio > heightRatio) {
      newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
    } else {
      newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
    }
    
    // This is the rect that we've calculated out and this is what is actually used below
    let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
    
    // Actually do the resizing to the rect using the ImageContext stuff
    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
    image.draw(in: rect)
    let newImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    
    return newImage!
  }
  
  class func toCgPoint(point : RPUIPoint) -> CGPoint {
    return CGPoint(x: CGFloat(point.getCol()), y: CGFloat(point.getRow()))
  }
  
  class func renderSolutionImage(baseImage: RPUIKamiImage!
      , solutionGeometry: RPUISolutionGeometry!
      , palette: [UIImage]) -> UIImage {
    
    let width = CGFloat(baseImage.getWidth())
    let height = CGFloat(baseImage.getHeight())
    let size = CGSize(width: width, height: height)
    
    let lineThickness = lineThicknessFraction * width
    let dashLength = dashLengthFraction * width
    let dotRadius = dotRadiusFraction * width
    
    let background = toUiImage(javaImage: baseImage)
    
    UIGraphicsBeginImageContextWithOptions(size, true, 1.0)
    let context = UIGraphicsGetCurrentContext()
    
    background.draw(in: CGRect(x: 0, y: 0, width: width, height: height))
    
    // Draw borders
    for pathIdx in 0..<solutionGeometry.borderCount() {
      let pointList = solutionGeometry.getBorderWith(pathIdx)
      
      context?.beginPath()
            for pointIdx in 0..<Int((pointList?.size())!) {
        let point = pointList?.getWith(jint(pointIdx))
        
        let cgPoint = toCgPoint(point: point!)
        
        if (pointIdx == 0) {
          context?.move(to: cgPoint)
        }
        else {
          context?.addLine(to: cgPoint)
        }
      }
      
      context?.closePath()
      
      context?.setLineWidth(lineThickness)
      context?.setStrokeColor(dark)
      context?.setLineDash(phase: 0.0, lengths: [dashLength, dashLength])
      let path = context?.path
      context?.strokePath()
      context?.setStrokeColor(light)
      context?.setLineDash(phase: dashLength, lengths: [dashLength, dashLength])
      context?.addPath(path!)
      context?.strokePath()
    }
    
    context?.setLineDash(phase: 0.0, lengths: [])
    
    // Draw Arrows and label boxes
    for labelIdx in 0..<solutionGeometry.getLabelBoxCount() {
      let arrow = solutionGeometry.getArrowWith(labelIdx)
      let labelBox = solutionGeometry.getLabelBox(with: labelIdx)
      
      let labelCenter = toCgPoint(point: (arrow?.getWith(0))!)
      let tileCenter = toCgPoint(point: (arrow?.getWith(1))!)
      
      context?.beginPath()
      
      context?.move(to: labelCenter)
      context?.addLine(to: tileCenter)
      
      let arrowPath = context?.path
      
      context?.setLineWidth(lineThickness * 3)
      context?.setStrokeColor(light)
      context?.strokePath()
      
      context?.beginPath()
      
      var firstPoint : CGPoint?
      
      for ptIdx in 0..<4 {
        let pt = toCgPoint(point: (labelBox?.getWith(jint(ptIdx)))!)
        
        if (ptIdx == 0) {
          firstPoint = pt
          context?.move(to: pt)
        }
        else {
          context?.addLine(to: pt)
        }
      }
      
      context?.addLine(to: firstPoint!)
      context?.closePath()
      
      let boxPath = context?.path
      
      context?.setLineWidth(lineThickness * 2)
      context?.strokePath()
      
      context?.beginPath()
      
      context?.addArc(center: tileCenter
        , radius: dotRadius
        , startAngle: 0
        , endAngle: CGFloat(JavaLangMath_PI) * 2
        , clockwise: true)
      
      context?.closePath()
      
      let dotPath = context?.path
      
      context?.strokePath()
      
      context?.setFillColor(dark)
      context?.setStrokeColor(dark)
      context?.setLineWidth(lineThickness)
      
      context?.addPath(arrowPath!)
      context?.strokePath()
      
      context?.addPath(dotPath!)
      context?.fillPath()
      
      context?.addPath(boxPath!)
      context?.fillPath()
    }
    
    // Draw the individual tiles in the solution
    for tileIdx in 0..<solutionGeometry.getPaletteTileBoxCount() {
      let paletteTileNumber = solutionGeometry.getPaletteTileNumber(with: tileIdx)
      let paletteTileBox = solutionGeometry.getPaletteTileBox(with: tileIdx)
      let paletteImage = palette[Int(paletteTileNumber)]
      
      var topLeft : CGPoint?
      var bottomRight : CGPoint?
      
      context?.beginPath()
      
      for ptIndex in 0..<4 {
        let pt = toCgPoint(point: (paletteTileBox?.getWith(jint(ptIndex)))!)
        
        if ptIndex == 0 {
          topLeft = pt
          context?.move(to: pt)
        }
        else {
          if ptIndex == 2 {
            bottomRight = pt
          }
          context?.addLine(to: pt)
        }
      }
      
      context?.closePath()
      
      context?.setLineWidth(lineThickness * 2)
      context?.setStrokeColor(light)
      
      context?.strokePath()
      
      paletteImage.draw(in: CGRect(x: (topLeft?.x)!, y: (topLeft?.y)!
        , width: (bottomRight?.x)! - (topLeft?.x)!
        , height: (bottomRight?.y)! - (topLeft?.y)!))
      
    }
    
    
    // Drawing complete, retrieve the finished image and cleanup
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return image!
  }
  
  class func toKamiImage(image : UIImage) -> RPUIKamiImage {
    let width = (image.cgImage?.width)!
    let height = (image.cgImage?.height)!
    let colorSpace       = CGColorSpaceCreateDeviceRGB()
    let bytesPerPixel    = 4
    let bitsPerComponent = 8
    let bytesPerRow      = bytesPerPixel * width
    let bitmapInfo       = RGBA32.bitmapInfo
    
    let context = CGContext(data: nil, width: width, height: height, bitsPerComponent: bitsPerComponent, bytesPerRow: bytesPerRow, space: colorSpace, bitmapInfo: bitmapInfo)
    
    context?.draw(image.cgImage!, in: CGRect(x: 0, y: 0, width: width, height: height))
    
    let buffer = context?.data
    
    let pixelBuffer = buffer?.bindMemory(to: RGBA32.self, capacity: width * height)
    
    let result = RPUIKamiImage(int: jint(width), with: jint(height))
    
    for row in 0 ..< Int(height) {
      for column in 0 ..< Int(width) {
        result?.setPixelWith(jint(row)
          , with: jint(column)
          , with: (pixelBuffer?[row * width + column].toColor())!)
      }
    }
    
    return result!
  }
  
}

