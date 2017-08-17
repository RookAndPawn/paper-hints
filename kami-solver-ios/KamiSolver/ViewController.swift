//
//  ViewController.swift
//  KamiSolver
//
//  Created by Kevin Guthrie on 6/22/17.
//  Copyright © 2017 Rook And Pawn Industries, Inc. All rights reserved.
//

import UIKit
import Guava
import KamiSolverCore
import MBProgressHUD

class ViewController: UIViewController, RPUIKamiSolverView, RPUIHasInteger
    , UIImagePickerControllerDelegate, UINavigationControllerDelegate {
  
  let javaOpenButtonRef = RPUIHasClickHandlers()
  let javaSolveButtonRef = RPUIHasClickHandlers()
  let javaImageSelectHandler = RPUIHasImageSelectedHandlers()
  
  var presenter : RPKamiSolverPresenter!
  var eventBus : ComGoogleCommonEventbusAsyncEventBus!
  var solutionLength : jint = 0
  
  let imagePicker = UIImagePickerController()
  
  var hud : MBProgressHUD?
  var solutionImages = [UIImage]()
  
  @IBOutlet weak var imgView: UIImageView!
  @IBOutlet weak var btnOpen: UIBarButtonItem!
  @IBOutlet weak var btnSolve: UIBarButtonItem!
  @IBOutlet weak var lblMoves: UIBarButtonItem!
  @IBOutlet weak var lblInstructions1: UILabel!
  @IBOutlet weak var lblInstructions2: UILabel!

  @IBOutlet weak var stpSolutionLength: UIStepper!
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    imgView.contentMode = .scaleAspectFit
    solutionLength = jint(stpSolutionLength.value)
    setValueWith(solutionLength)
    
    let executor : JavaUtilConcurrentExecutor =
        JavaUtilConcurrentExecutors.newSingleThreadExecutor()
    
    let exceptionHandler = RPUIErrorHandler()
    
    eventBus = ComGoogleCommonEventbusAsyncEventBus(javaUtilConcurrentExecutor: executor, with: exceptionHandler)
    
    imagePicker.delegate = self
    
    presenter = RPKamiSolverPresenter()
    
    presenter.bind(with: self, with: eventBus)
    
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Boo Hoo
  }
  
  func showImageSelectionView() {
    imagePicker.allowsEditing = false
    imagePicker.sourceType = .photoLibrary
    
    present(imagePicker, animated: true, completion: nil)
  }
  
  func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
    
    if let pickedImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
      DispatchQueue.global().async {
        self.lblInstructions1.isHidden = true
        self.lblInstructions2.isHidden = true
        
        self.indicateWorking()
        self.setStatusWith("Loading Image", with: -1)
        
        self.javaImageSelectHandler?.selectImage(with: IosImage.toKamiImage(image: pickedImage))
      }
      
      
    }
    
    dismiss(animated: true, completion: nil)
  }
  
  func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
    dismiss(animated: true, completion: nil)
  }
  
  func getOpenButton() -> RPUIHasClickHandlers! {
    return javaOpenButtonRef
  }
  
  func getSolveButton() -> RPUIHasClickHandlers! {
    return javaSolveButtonRef
  }
  
  func getImageSelector() -> RPUIHasImageSelectedHandlers! {
    return javaImageSelectHandler
  }
  
  func indicateWorking() {
    DispatchQueue.main.async {
      self.hud = MBProgressHUD.showAdded(to: self.view, animated: true)
      self.hud?.mode = MBProgressHUDMode.indeterminate
      self.hud?.label.text = "Working..."
    }
  }
  
  func indicateNotWorking() {
    DispatchQueue.main.async {
      self.hud?.hide(animated: true)
    }
  }
  
  func showImage(with image: RPUIKamiImage!) {
    DispatchQueue.main.async {
      self.imgView.image = IosImage.toUiImage(javaImage: image)
    }
  }
  
  func showImage(image: UIImage!) {
    DispatchQueue.main.async {
      self.imgView.image = image
    }
  }
  
  func setStatusWith(_ status: String!, with progress: jdouble) {
    DispatchQueue.main.async {
      if (progress < 0) {
        self.hud?.mode = MBProgressHUDMode.indeterminate
      }
      else {
        self.hud?.mode = MBProgressHUDMode.annularDeterminate
        self.hud?.progress = Float(progress)
      }
      
      self.hud?.label.text = status
    }
  }
  
  func getSolutionLengthController() -> RPUIHasInteger! {
    return self
  }
  
  func getValue() -> JavaLangInteger! {
    return solutionLength as! JavaLangInteger
  }
  
  func setValueWith(_ value: jint) {
    lblMoves.title = "\(value) Moves"
  }
  
  func setSolutionWith(_ baseImage: RPUIKamiImage!
      , with solution: RPUISolutionGeometryList!
      , with palette: RPUIPaletteList!) {
    
    var paletteTiles = [UIImage]()
    
    for i in 0..<palette.size() {
      let unsquareImage = IosImage.toUiImage(javaImage: palette.getWith(i))
      
      let size = min(unsquareImage.size.height, unsquareImage.size.width)
      
      let unSizedImage = IosImage.cropToBounds(image: unsquareImage
          , width: Double(size)
          , height: Double(size))
      
      let sideLen = CGFloat(solution.getPaletteTileSize())
      
      let image = IosImage.resizeImage(image: unSizedImage
          , targetSize: CGSize(width: sideLen, height: sideLen))
      
      paletteTiles.append(image)
    }
    
    let imageCount = solution.size()
    
    for i in 0..<imageCount {
      let image = IosImage.renderSolutionImage(baseImage: baseImage
          , solutionGeometry: solution.getWith(i)
          , palette: paletteTiles)
      
      //showImage(image: image)
      solutionImages.append(image)
    }
  }
  
  func showSolution() {
    DispatchQueue.main.async {
      self.performSegue(withIdentifier: "ShowSolution", sender: self)
    }
  }
  
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    let solutionionView = segue.destination as! SolutionViewController
    solutionionView.solutionTiles = solutionImages
  }
  
  @IBAction func openButtonClicked(_ sender: Any) {
    javaOpenButtonRef?.click()
  }
  
  @IBAction func solveButtonClicked(_ sender: Any) {
    javaSolveButtonRef?.click()
  }
  
  @IBAction func solutionLengthChanged(_ sender: Any) {
    solutionLength = jint(stpSolutionLength.value)
    setValueWith(solutionLength)
  }
  
}

