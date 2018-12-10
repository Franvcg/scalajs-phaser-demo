package pairs.client.phaser.pixi

import scala.scalajs.js
import js.annotation._

@js.native  //Indica que Phaser é nativo de uma biblioteca JavaScript
@JSGlobal("PIXI.Sprite")  //Quando criamos uma classe com o nome diferente do que ela possui em JavaScript
//é passado uma String com argumento da anotação @JSGlobal, indicando qual o nome dessa classe em JS.
class Sprite protected () extends js.Object {
  var x: Double = js.native
  var y: Double = js.native

  var visible: Boolean = js.native
}
