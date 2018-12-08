package pairs.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

import scala.util.Properties

object Server {
  // Cria-se uma DIV tag, onde a biblioteca Phaser irá configurar e carregar o projeto.
  private val Index = """
    <html>
      <head>
        <title>Jogo da Memoria</title>
      </head>
      <body>
        <h1>Jogo da Memoria</h1>
        <div id="pairs-container"/>
        <script type="application/javascript" src="assets/phaser.min.js"></script>
        <script type="application/javascript" src="/js/pairs-client-fastopt.js"></script>
      </body>
    </html>
  """

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = {
      pathPrefix("js") { //fileName =>
        //getFromFile("../client/target/scala-2.11/" + fileName)
        getFromDirectory("./client/target/scala-2.12")
      } ~
      pathPrefix("assets") {
        getFromResourceDirectory("assets")
      } ~
      pathSingleSlash {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Index))
        } ~
        getFromResourceDirectory("")
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

    println(s"Servidor online, acesse http://localhost:9000/\nPressione ENTER para sair...")
    StdIn.readLine() // Roda até o usuário digitar ENTER
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // desliga o servidor ao terminar
  }
}
