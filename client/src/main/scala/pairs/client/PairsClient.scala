package pairs.client

import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import org.scalajs.dom

import pairs.client.phaser._

class Quadro(val linha: Int, val col: Int, val carta: Int,
    val frente: Sprite, val costas: Sprite)
// Essa classe é responsável por controlar o fluxo do jogo, com métodos capazes de gerenciar o funcionamento do mesmo e da sua renderização.
class EstadoJogo extends State {
  private var primeiroClique: Option[Quadro] = None
  private var segundoClique: Option[Quadro] = None

  private var pontuacao: Int = 0
  private var pontuacaoTexto: js.Dynamic = null
  private var pontuacaoGrafica: Graphics = null
    
//Pré-carrega os recursos essenciais antes do início do jogo, os assets(sons,imagens,sprites*...)
//*Sprites são objetos gráficos em 2D
  override def preCarregamento(): Unit = {
    load.image("back", "assets/back.png")
    for (i <- 0 to 9)
      load.image(i.toString(), s"assets/$i.png")
  }
 //Utilizado para criar o jogo propriamente dito, esse método é chamado quando o jogo é carregado, após o preCarregamento
  override def criar(): Unit = {
    val todasCartas =
      for (i <- 0 to 9; _ <- 1 to 2) yield i // duas copias de cada carta
    val cartasEmbaralhadas = scala.util.Random.shuffle(todasCartas)

    val todosQuadros =
      for (linha <- 0 until 4; col <- 0 until 5) yield (linha, col)

    for (((linha, col), carta) <- todosQuadros zip cartasEmbaralhadas) yield {
      val tamQuadro = 130
      val (x, y) = (col * tamQuadro, linha * tamQuadro)
      val frente = game.add.sprite(x, y, key = carta.toString())
      val costas = game.add.sprite(x, y, key = "back")

      // Ao iniciar o jogo, as costas da carta são mostradas, desse modo, a frente não é visível
      frente.visible = false

      // Configura o evento do clique do mouse
      val quadro = new Quadro(linha, col, carta, frente, costas)
      costas.inputEnabled = true
      costas.events.onInputDown.add((sprite: Sprite) => clique(quadro))
    }

    pontuacaoTexto = game.asInstanceOf[js.Dynamic].add.text(
        660, 20, "Pontuacao: 0",
        js.Dynamic.literal(fontSize = "24px", fill = "#fff"))

    pontuacaoGrafica = game.add.graphics(660, 50)
  }

  private def clique(quadro: Quadro): Unit = {
    (primeiroClique, segundoClique) match {
      case (None, _) =>
        // Primeiro clique, após ele, aguardamos até o segundo para verificar se as cartas selecionadas são iguais ou não
        primeiroClique = Some(quadro)

      case (Some(first), None) if first.carta == quadro.carta =>
        // Encontrou uma carta igual a anterior
        primeiroClique = None
        pontuacao += 50

      case (Some(_), None) =>
        // Encontrou cartas diferentes, logo, é necessário virar as cartas de volta
        segundoClique = Some(quadro)
        pontuacao -= 5
        js.timers.setTimeout(1000) {
          assert(primeiroClique.isDefined && segundoClique.isDefined)
          for (quadro <- Seq(primeiroClique.get, segundoClique.get)) {
            quadro.frente.visible = false
            quadro.costas.visible = true
          }
          primeiroClique = None
          segundoClique = None
        }

      case (Some(_), Some(_)) =>
        // Terceiro clique, cancela (precisa esperar um certo tempo passar para permitir clicar novamente)
        return
    }

    quadro.costas.visible = false
    quadro.frente.visible = true

    pontuacaoTexto.text = s"Pontuacao: $pontuacao"  // Mostra a pontuação

    pontuacaoGrafica.clear()
    for (i <- 0 until pontuacao / 100) {
      val offset = i * 24
      def pt(x0: Double, y0: Double): PointLike = new PointLike {
        val x = x0
        val y = y0
      }

      val points = for (i <- (0 until 10).toJSArray) yield {
        val angle = 2*Math.PI/10 * i + Math.PI/2
        val len = if (i % 2 == 0) 10 else 4
        pt(offset + 10 + len*Math.cos(angle), 10 - len*Math.sin(angle))
      }

      pontuacaoGrafica.beginFill(0xFFD700)
      pontuacaoGrafica.drawPolygon(points)
      pontuacaoGrafica.endFill()
    }
  }
}

object PairsClient {
  def main(args: Array[String]): Unit = {
    //Aqui criamos um objeto do Jogo, que irá configurar o framework e funcionamento do jogo
    //Informamos a largura, altura e o id da DIV tag, criado no código HTML.
    //É possível deixar esse parâmetro vazio, mas especificando a DIV tag, isso nos fornece maior controle sobre o posicionamento do jogo na página HTML. 
    val game = new Game(width = 800, height = 520, parent = "pairs-container")
    //Adiciona o objeto State criado a lista de estados disponíveis no jogo
    //Nesse caso, só há um estado disponível, já que não foi criado um estado para o menu, instruções...
    game.state.add("game", new EstadoJogo)
    //Iniciamos o estado do jogo com o objeto add nele acima, sem a necessidade de especificar o estado a ser iniciado, já que só há um
    //Serão chamadas as funções preCarregamento, criar nessa sequência
    //Se não houvesse a lógica do jogo, nesse momento, seria criada uma tela preta com as dimensões informadas acima
    game.state.start("game")
  }
}
