//
//  SolutionViewController.swift
//  KamiSolver
//
//  Created by Kevin Guthrie on 7/26/17.
//  Copyright © 2017 Rook And Pawn Industries, Inc. All rights reserved.
//

import UIKit

class SolutionViewController: UIViewController, UIPageViewControllerDataSource {
  
  var solutionTiles : [UIImage]?
  
  var pageController : UIPageViewController?
  
  var pageNumber : Int = 0
  
  @IBOutlet weak var pageViewContainer: UIView!
  
  override func viewDidLoad() {
    super.viewDidLoad()
  }

  func getItemController(_ index : Int) -> SolutionTileViewController? {
  
    let tileController = self.storyboard?.instantiateViewController(
        withIdentifier: "SolutionTileView") as! SolutionTileViewController
    
    tileController.imageTile = solutionTiles?[index]
    tileController.index = index
    return tileController
  }
  
  func pageViewController(_ pageViewController: UIPageViewController
      , viewControllerBefore viewController: UIViewController) -> UIViewController? {
    
    let idx = (viewController as! SolutionTileViewController).index!
    
    if idx == 0 {
      return nil
    }
    
    return getItemController(idx - 1)
  }
  
  func pageViewController(_ pageViewController: UIPageViewController
    , viewControllerAfter viewController: UIViewController) -> UIViewController? {
    
    let idx = (viewController as! SolutionTileViewController).index!
    
    if idx == (solutionTiles!.count - 1) {
      return nil
    }
    
    return getItemController(idx + 1)
  }
  
  func presentationCount(for pageViewController: UIPageViewController) -> Int {
    return (solutionTiles?.count)!
  }
  
  func presentationIndex(for pageViewController: UIPageViewController) -> Int {
    return pageNumber
  }
 
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if (segue.identifier == "Embed") {
      pageController = segue.destination as? UIPageViewController
      pageController?.dataSource = self
      
      let firstController = getItemController(0)
      let firstControllers = [firstController!]
      
      pageController?.setViewControllers(firstControllers
        , direction: UIPageViewControllerNavigationDirection.forward
        , animated: true
        , completion: nil)

      let appearance = UIPageControl.appearance()
      appearance.currentPageIndicatorTintColor = self.view.tintColor
      appearance.backgroundColor = self.view.backgroundColor
      appearance.pageIndicatorTintColor = UIColor.gray
    }
  }
  
  @IBAction func onFastForward(_ sender: Any) {
    pageNumber = (solutionTiles?.count)! - 1
    let firstController = getItemController(pageNumber)
    let firstControllers = [firstController!]
    
    pageController?.setViewControllers(firstControllers
      , direction: UIPageViewControllerNavigationDirection.forward
      , animated: true
      , completion: nil)

  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
    
}
