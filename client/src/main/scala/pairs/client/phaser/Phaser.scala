//Esse arquivo .scala é necessário para "traduzir" algumas classes nativas da biblioteca Phaser de JavaScript para Scala
//Phaser possui uma grande quantidade de classes e objetos, logo "traduzimos" apenas as que usamos e são necessárias,
//Sem isso o compilador não reconhece Phaser e suas subclasses.
package pairs.client.phaser

import scala.scalajs.js //Importa Scala.js pois Phaser é uma API de JS, não de Scala
import js.annotation._  //É necessário para realizar as anotações @
import js.|
import org.scalajs.dom.html

@js.native //Indica que Phaser é nativo de uma biblioteca JavaScript
@JSGlobal  //Indica que esse objeto é uma variável global em JavaScript.
object Phaser extends js.Object {
  val AUTO: Int = js.native
}

@js.native
@JSGlobal("Phaser.Game")  
//Quando criamos uma classe com o nome diferente do que ela possui em JavaScript
//é passado uma String com argumento da anotação @JSGlobal, indicando qual o nome dessa classe em JS.
class Game(
    width: Double | String = 800,
    height: Double | String = 600,
    renderer: Int = Phaser.AUTO,
    parent: String | html.Element = "") extends js.Object {

  val state: StateManager = js.native

  val add: GameObjectFactory = js.native
}

@js.native
@JSGlobal("Phaser.StateManager")
class StateManager(val game: Game) extends js.Object {
  def add(key: String, state: State,
      autoStart: Boolean = false): Unit = js.native

  def start(key: String): Unit = js.native
}

@js.native
@JSGlobal("Phaser.State")
abstract class State extends js.Object {
  protected final def game: Game = js.native

  protected final def load: Loader = js.native

  def preload(): Unit = js.native

  def create(): Unit = js.native
}

@js.native
@JSGlobal("Phaser.Loader")
class Loader extends js.Object {
  def image(key: String, url: String = js.native,
      overwrite: Boolean = false): this.type = js.native
}

@js.native
@JSGlobal("Phaser.GameObjectFactory")
class GameObjectFactory(game: Game) extends js.Object {
  def sprite(x: Double = 0, y: Double = 0,
      key: String = js.native): Sprite = js.native

  def graphics(x: Double = 0, y: Double = 0): Graphics = js.native
}

@js.native
@JSGlobal("Phaser.Sprite")
class Sprite protected () extends pixi.Sprite
    with ComponentCore with InputEnabled {

}

@js.native
trait ComponentCore extends js.Object {
  val events: Events = js.native
}

@js.native
trait InputEnabled extends js.Object {
  def inputEnabled: Boolean = js.native
  def inputEnabled_=(value: Boolean): Unit = js.native
}

@js.native
@JSGlobal("Phaser.Events")
class Events(sprite: Sprite) extends js.Object {
  val onInputDown: Signal[js.Function1[Sprite, _]] = js.native
}

@js.native
@JSGlobal("Phaser.Signal")
class Signal[ListenerType <: js.Function] extends js.Object {
  def add(listener: ListenerType): Unit = js.native
}

@js.native
@JSGlobal("Phaser.Graphics")
class Graphics protected () extends js.Object {
  def clear(): Unit = js.native
  def beginFill(color: Int): Unit = js.native
  def endFill(): Unit = js.native
  def drawPolygon(path: js.Array[PointLike]): Unit = js.native
}

trait PointLike extends js.Object {
  def x: Double
  def y: Double
}
