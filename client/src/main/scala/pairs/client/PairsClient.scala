package pairs.client

import scala.scalajs.js
import org.scalajs.dom

import pairs.client.phaser._

object PairsClient extends js.JSApp {
  def main(): Unit = {
    val game = new Game(800, 520, Phaser.AUTO, "pairs-container")
  }
}
