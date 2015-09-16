package pairs.client

import scala.concurrent.duration._

import scala.scalajs.js
import js.annotation._
import org.scalajs.dom

import pairs.client.phaser._

object GameState {
  private class Square(val row: Int, val col: Int, val card: Int,
      val front: Sprite, val back: Sprite)

  private final val Rows = 4
  private final val Cols = 5
  private final val TileSize = 130

  private val AllCards =
    for (i <- 0 to 9; _ <- 1 to 2) yield i // two copies of each card

  private val AllPositions =
    for (row <- 0 until Rows; col <- 0 until Cols) yield (row, col)
}

@ScalaJSDefined
class GameState extends State {
  import GameState._

  private var firstClick: Option[Square] = None
  private var secondClick: Option[Square] = None
  private var hideDeadline: Option[Deadline] = None

  override def preload(): Unit = {
    load.image("back", "assets/back.png")
    for (i <- 0 to 9)
      load.image(i.toString(), s"assets/$i.png")
  }

  override def create(): Unit = {
    val shuffledCards = scala.util.Random.shuffle(AllCards)

    for ((row, col) <- AllPositions) yield {
      val card = shuffledCards(row * Cols + col)
      val (x, y) = (col * TileSize, row * TileSize)
      val front = game.add.sprite(x, y, key = card.toString())
      val back = game.add.sprite(x, y, key = "back")

      // Initially, the back is visible
      front.visible = false

      // Setup click event
      val square = new Square(row, col, card, front, back)
      back.inputEnabled = true
      back.events.onInputDown.add(sprite => doClick(square))
    }
  }

  private def doClick(square: Square): Unit = {
    (firstClick, secondClick) match {
      case (None, _) =>
        // First click of a pair
        firstClick = Some(square)

      case (Some(first), None) if first.card == square.card =>
        // Found a pair
        firstClick = None

      case (Some(_), None) =>
        // Missing a pair, need to hide it later
        secondClick = Some(square)
        hideDeadline = Some(1.second.fromNow)

      case (Some(_), Some(_)) =>
        // Third click, cancel (have to wait for the deadline to elapse)
        return
    }

    square.back.visible = false
    square.front.visible = true
  }

  override def update(): Unit = {
    if (hideDeadline.exists(_.isOverdue())) {
      assert(firstClick.isDefined && secondClick.isDefined)
      for (square <- Seq(firstClick.get, secondClick.get)) {
        square.front.visible = false
        square.back.visible = true
      }
      firstClick = None
      secondClick = None
      hideDeadline = None
    }
  }
}

object PairsClient extends js.JSApp {
  def main(): Unit = {
    val game = new Game(width = 800, height = 520, parent = "pairs-container")
    game.state.add("game", new GameState)
    game.state.start("game")
  }
}
