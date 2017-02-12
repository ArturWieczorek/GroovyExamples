
import javax.servlet.*
import javax.servlet.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.util.resource.*

import utils.ClientRepository
import utils.CSVUtils

@Grapes([
    @Grab('org.eclipse.jetty.aggregate:jetty-server:8.1.2.v20120308'),
    @Grab('org.eclipse.jetty.aggregate:jetty-servlet:8.1.2.v20120308'),
    @Grab(group='javax.servlet', module='javax.servlet-api', version='3.0.1')
])
class Main extends HttpServlet {
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/csv")
		response.setHeader("Content-Disposition", "attachment;filename=clients.csv")

		try
		{
			OutputStream outputStream = response.getOutputStream();
			
			String numbers = request.getParameter('clients_numbers')
			String[] numbersArray = numbers.split(',')

			def intList = []
			numbersArray.each{
				intList.push(it.toInteger())
			}
			
			int[] intArray = new int[intList.size()]
			
			CSVUtils.writeLine(Arrays.asList(CSVUtils.CSV_HEADER))
			List<String> clients = ClientRepository.getClientsNumber(intList.toArray(intArray))
			clients.stream().forEach({ clientId -> 
            	try {
            		CSVUtils.writeLine(Arrays.asList("$clientId", "2017-01-12", "2017-01-24", "repayment", "30", "Promotion"))

            	} catch (IOException e) {
                	e.printStackTrace()
            	}

        	});       
	
			outputStream.write(CSVUtils.extract().getBytes())
			outputStream.flush()
			outputStream.close()

			CSVUtils.clearBuffer()
		}
		catch(Exception e)
		{
			System.out.println(e.toString())
		}
    }

    public static startServer() {
		def jetty = new Server(9091)
		ClassLoader cl = Main.class.getClassLoader()
		// We look for a file, as ClassLoader.getResource() is not
		// designed to look for directories (we resolve the directory later)
		URL f = cl.getResource("test.html")
		if (f == null)
		{
			throw new RuntimeException("Unable to find resource directory")
		}
		URI webRootUri = f.toURI().resolve("./").normalize()
		System.err.println("WebRoot is " + webRootUri)
		 
		def context = new ServletContextHandler(jetty, '/', ServletContextHandler.SESSIONS)
		
		context.setBaseResource(Resource.newResource(webRootUri))
		context.setWelcomeFiles(["test.html"].toListString())
		context.addServlet(this, '/start') 
		
		ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class)
		holderPwd.setInitParameter("dirAllowed","false")
		context.addServlet(holderPwd,"/")

		jetty.setHandler(context)
		jetty.start()
		jetty.join()
    }
}
Main.startServer()