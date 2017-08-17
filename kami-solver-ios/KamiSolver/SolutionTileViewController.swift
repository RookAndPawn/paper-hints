//
//  SolutionViewController.swift
//  KamiSolver
//
//  Created by Kevin Guthrie on 7/26/17.
//  Copyright © 2017 Rook And Pawn Industries, Inc. All rights reserved.
//

import UIKit

class SolutionTileViewController: UIViewController {

  var index : Int?
  var imageTile : UIImage?
  
  @IBOutlet weak var imgSolutionTile: UIImageView!
  
    override func viewDidLoad() {
      super.viewDidLoad()

      imgSolutionTile.image = imageTile
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
  

}
