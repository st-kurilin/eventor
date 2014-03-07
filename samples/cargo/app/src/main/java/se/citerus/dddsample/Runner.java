package se.citerus.dddsample;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Runner {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        WebAppContext context = new WebAppContext();
        context.setDescriptor("WEB-INF/web.xml");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        context.setResourceBase("samples/cargo/app/src/main/webapp");
        context.setDescriptor("samples/cargo/app/src/main/webapp/WEB-INF/web.xml");
        server.start();
        server.join();
    }
}
