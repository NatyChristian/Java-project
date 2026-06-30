// =====================================================================
//  BallWorld.java
//  La recette de conception (HtDP) appliquee a un "World" en Java (HtDC)
//
//  Librairies : javalib.funworld + javalib.worldimages + tester
//  Style "funworld" = pur : chaque gestionnaire RENVOIE un nouveau monde
//  (exactement comme on-tick / on-key renvoient un nouveau world-state
//   avec big-bang en Racket).
// =====================================================================

import tester.*;                 // la librairie de tests  (= check-expect)
import javalib.funworld.*;       // la classe abstraite World + bigBang
import javalib.worldimages.*;    // les images : CircleImage, TextImage...
import java.awt.Color;

// ---------------------------------------------------------------------
// 1) DEFINITION DE DONNEE
//    En Racket :  (define-struct ball (x y))
//    ; un Ball est (make-ball Number Number)
//    En Java : un struct devient une CLASSE avec des champs types.
// ---------------------------------------------------------------------
class Ball {
  int x;   // signature : le contrat devient le TYPE du champ
  int y;

  Ball(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /* GABARIT (template) de la classe Ball.
   * Tout ce qu'une methode de Ball peut utiliser :
   *    ... this.x ...   -- int
   *    ... this.y ...   -- int
   */

  // deplace la balle de dy vers le bas  ->  un NOUVEAU Ball
  // (fonction pure, comme en Racket : on ne modifie rien, on reconstruit)
  Ball moveDown(int dy) {
    return new Ball(this.x, this.y + dy);
  }

  // deplace horizontalement  ->  un NOUVEAU Ball
  Ball moveX(int dx) {
    return new Ball(this.x + dx, this.y);
  }

  // l'image de la balle
  WorldImage draw() {
    return new CircleImage(15, OutlineMode.SOLID, Color.RED);
  }

  // la balle est-elle sortie par le bas d'un ecran de hauteur h ?
  boolean offScreen(int h) {
    return this.y > h;
  }
}

// ---------------------------------------------------------------------
// 2) LE MONDE
//    En Racket, le world-state etait une donnee + des gestionnaires
//    passes a big-bang. En Java, on ETEND la classe World et on
//    REDEFINIT les gestionnaires qui nous interessent.
// ---------------------------------------------------------------------
class BallWorld extends World {
  int width = 300;
  int height = 400;
  Ball ball;            // le world-state vit ici

  BallWorld(Ball ball) {
    super();
    this.ball = ball;
  }

  /* GABARIT du monde :  ... this.ball ...  -- Ball
   *                     ... this.width ... ... this.height ... -- int  */

  // ----- to-draw  -->  makeScene() : renvoie une WorldScene -----
  public WorldScene makeScene() {
    return this.getEmptyScene()
               .placeImageXY(this.ball.draw(), this.ball.x, this.ball.y);
  }

  // ----- on-tick  -->  onTick() : renvoie un nouveau World -----
  public World onTick() {
    return new BallWorld(this.ball.moveDown(5));
  }

  // ----- on-key  -->  onKeyEvent(String) : renvoie un nouveau World -----
  public World onKeyEvent(String key) {
    if (key.equals("left"))  { return new BallWorld(this.ball.moveX(-10)); }
    if (key.equals("right")) { return new BallWorld(this.ball.moveX(10)); }
    return this;                       // touche ignoree : monde inchange
  }

  // ----- stop-when  -->  worldEnds() : renvoie un WorldEnd -----
  //   WorldEnd(true,  scene)  => le monde s'arrete, on affiche scene
  //   WorldEnd(false, scene)  => le monde continue
  public WorldEnd worldEnds() {
    if (this.ball.offScreen(this.height)) {
      return new WorldEnd(true,
        this.makeScene().placeImageXY(
          new TextImage("Fini !", Color.BLACK), 150, 200));
    }
    return new WorldEnd(false, this.makeScene());
  }

  public static void main(String[] args){
	BallWorld game = new BallWorld(new Ball(10, 10));
	game.bigBang(100, 100, 0.5);
  }
}

// ---------------------------------------------------------------------
// 3) EXEMPLES + TESTS
//    En Racket :  (check-expect (move-down (make-ball 150 0) 5) ...)
//    En Java   :  t.checkExpect(...)  via la librairie tester.
//    Astuce : tester compare les objets CHAMP PAR CHAMP automatiquement,
//    donc pas besoin d'ecrire equals() pour que checkExpect marche.
// ---------------------------------------------------------------------
class ExamplesBall {
  Ball b0 = new Ball(150, 0);
  BallWorld w0 = new BallWorld(this.b0);

  // tests des methodes "pures" : aussi simples qu'en Racket
  boolean testMoveDown(Tester t) {
    return t.checkExpect(this.b0.moveDown(5), new Ball(150, 5));
  }

  boolean testOnTick(Tester t) {
    return t.checkExpect(this.w0.onTick(), new BallWorld(new Ball(150, 5)));
  }

  boolean testOnKeyRight(Tester t) {
    return t.checkExpect(this.w0.onKeyEvent("right"),
                         new BallWorld(new Ball(160, 0)));
  }

  boolean testOffScreen(Tester t) {
    return t.checkExpect(new Ball(0, 401).offScreen(400), true)
        && t.checkExpect(new Ball(0, 399).offScreen(400), false);
  }

  // ----- lancer le jeu  =  (big-bang ...) en Racket -----
  //   bigBang(largeur, hauteur, dt-en-secondes)
  boolean testGame(Tester t) {
    BallWorld w = new BallWorld(new Ball(150, 0));
    return w.bigBang(300, 400, 0.1);
  }
}