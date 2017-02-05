import javax.servlet.*
import javax.servlet.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.util.resource.*

import javax.management.remote.JMXConnectorFactory as JmxFactory
import javax.management.remote.JMXServiceURL as JmxUrl


@Grapes([
    @Grab('org.eclipse.jetty.aggregate:jetty-server:8.1.2.v20120308'),
    @Grab('org.eclipse.jetty.aggregate:jetty-servlet:8.1.2.v20120308'),
    @Grab(group='javax.servlet', module='javax.servlet-api', version='3.0.1')
])
class Main extends HttpServlet {
	
	def serverUrl = 'service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi'
	def beanName = "com.example:type=Hello"

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("AddNumbers") != null) {
			def server
			def data
			
			def selectedEnvironment = request.getParameter('environment') as String
			def firstNumber = request.getParameter('numberOne') as Integer
			def secondNumber = request.getParameter('numberTwo') as Integer
		
			try{
				server = JmxFactory.connect(new JmxUrl(serverUrl))
				def MBeanServer = server.MBeanServerConnection
				def bean = new GroovyMBean(MBeanServer, beanName)
				def calculationResult = bean.add(firstNumber,secondNumber) as String
				
				data = calculationResult + ';' + selectedEnvironment
				
				println 'Result of calling bean add operation: ' + calculationResult
				println 'Environment: ' + selectedEnvironment
				
				response.setContentType("text/plain")
				response.setCharacterEncoding("UTF-8")
				response.getWriter().write(data)
			}
			finally{
				server.close()
			}
		}
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response)
    }

    public static startServer() {
		def jetty = new Server(9091)
		ClassLoader cl = Main.class.getClassLoader();
		// We look for a file, as ClassLoader.getResource() is not
		// designed to look for directories (we resolve the directory later)
		URL f = cl.getResource("test.html");
		if (f == null)
		{
			throw new RuntimeException("Unable to find resource directory");
		}
		URI webRootUri = f.toURI().resolve("./").normalize();
		System.err.println("WebRoot is " + webRootUri);
		 
		def context = new ServletContextHandler(jetty, '/', ServletContextHandler.SESSIONS)
		
		context.setBaseResource(Resource.newResource(webRootUri));
		context.setWelcomeFiles(["test.html"].toListString());
		context.addServlet(this, '/start') 
		
		ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
		holderPwd.setInitParameter("dirAllowed","false");
		context.addServlet(holderPwd,"/");

		jetty.setHandler(context);
		jetty.start()
		jetty.join()
    }
}
Main.startServer()