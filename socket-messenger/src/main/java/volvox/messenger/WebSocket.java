package volvox.messenger;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.management.AkkaManagement;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.stream.Materializer;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import volvox.messenger.server.WebSocketServer;

import java.util.concurrent.CompletionStage;


public class WebSocket {
    private static Logger logger = LoggerFactory.getLogger(WebSocket.class);

    private static final String akkaNameConfig = "akka.name";
    private static final String wsHostConfig = "ws.host";
    private static final String wsPortConfig = "ws.port";

    public static void main(String[] args) {
        final var config = ConfigFactory.load();
        final ActorSystem system = ActorSystem.create(config.getString(akkaNameConfig));

        //See https://github.com/akka/akka-management/issues/282
        if (system.settings().config().getStringList("akka.cluster.seed-nodes").size() == 0) {
            // Akka Management hosts the HTTP routes used by bootstrap
            AkkaManagement.get(system).start();
            // Starting the bootstrap process needs to be done explicitly
            ClusterBootstrap.get(system).start();
        }

        try {
            logger.info("Akka stared successfully ...");

            final String hostName = config.getString(wsHostConfig);
            final int port = config.getInt(wsPortConfig);

            final Http http = Http.get(system);
            Materializer materializer = ActorMaterializer.create(system);

            //In order to access all directives we need an instance where the routes are define.
            WebSocketServer app = new WebSocketServer(system);

            final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                    app.createRoute().flow(system, materializer);
            final CompletionStage<ServerBinding> binding =
                    http.bindAndHandle(
                            routeFlow,
                            ConnectHttp.toHost(hostName, port),
                            materializer);

//            binding
//                    .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
//                    .thenAccept(unbound -> { /*NOOP!*/});

            logger.info("Listening HTTP on " + hostName + ":" + port);

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
