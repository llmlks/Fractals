

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jmge.gif.Gif89Encoder;

/**
 * Servlet implementation class Animation
 */
@WebServlet("/Animation")
public class Animation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public Animation() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("s") == null || !request.getParameter("s").matches("\\d+")) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.println("<form>");
			out.println("<h2>Simple fractals</h2>");
			out.println("<h3>Inputs</h3>");
			out.println("Side of square: <input name='s' value='500'/><br>");
			out.println("Number of iterations: <input name='i' value='5'/><br>");
			out.println("Type of fractal: <input name='t' value='Sierpinski carpet'/>");
			out.println("<small>(Either Sierpinski carpet, Cantor, Vicsek or T-square)</small><br>");
			out.println("<input type='submit' value='Enter'/>");
			out.println("</form>");
		} else {
			try {
				int s = Integer.parseInt(request.getParameter("s"));
				int iteration = Integer.parseInt(request.getParameter("i")) - 1;
				String type = request.getParameter("t");
				Map<Point, Integer> sqr = new HashMap<>();
				if (type.substring(0,1).toUpperCase().equals("S"))
					sqr.put(new Point(s/3, s/3), s/3);
				else if (type.substring(0,1).toUpperCase().equals("T"))
					sqr.put(new Point(s/4, s/4), s/2);					
				else if (type.substring(0,1).toUpperCase().equals("C"))
					sqr.put(new Point(10, 10), s-20);
				else {
					sqr.put(new Point(0,0), s/3);
					sqr.put(new Point(s/3,s/3), s/3);
					sqr.put(new Point(0,s*2/3), s/3);
					sqr.put(new Point(s*2/3,0), s/3);
					sqr.put(new Point(s*2/3,s*2/3), s/3);
				}
				Gif89Encoder genc = new Gif89Encoder();
				for (int i=0;i<=iteration*5 + 4;i++){
					BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = image.createGraphics();
					drawFrame(g, s, i);
					if (type.substring(0,1).toUpperCase().equals("C"))
						drawRect(g, sqr);
					else
						drawSquare(g, sqr);
					genc.addFrame(image);
					g.dispose();
					if (i % 5 == 4){
						if (type.substring(0,1).toUpperCase().equals("S"))
							sqr = sierpinski(sqr);
						else if (type.substring(0,1).toUpperCase().equals("T"))
							sqr = tSquare(sqr);
						else if (type.substring(0,1).toUpperCase().equals("C"))
							sqr = cantor(sqr);
						else
							sqr = vicsek(sqr);
					}
				}
				genc.setUniformDelay(10);
				genc.setLoopCount(0);
				genc.encode(response.getOutputStream());
			}catch(Exception e){
				System.err.println(e);
			}	
		}
	}

    static void drawFrame(Graphics2D g, int s, int i) {
    	g.setColor(Color.blue);
    	g.fillRect(0, 0, s, s);
    }

    static void drawSquare(Graphics2D g, Map<Point, Integer> sqr) {
    	g.setColor(Color.white);
    	for (Point p : sqr.keySet()) {
    		g.fillRect(p.x, p.y, sqr.get(p), sqr.get(p));
    	}
    }
    
    static void drawRect(Graphics2D g, Map<Point, Integer> sqr) {
    	g.setColor(Color.white);
    	for (Point p : sqr.keySet()) {
    		g.fillRect(p.x, p.y, sqr.get(p), 20);
    	}
    }
    
    static Map<Point, Integer> tSquare(Map<Point, Integer> sqr) {
    	Map<Point, Integer> help = new HashMap<>();
    	help.putAll(sqr);
    	for (Point p : sqr.keySet()) {
    		help.put(new Point(p.x - sqr.get(p)/4, p.y - sqr.get(p)/4), sqr.get(p)/2);
    		help.put(new Point(p.x - sqr.get(p)/4, p.y + sqr.get(p)*3/4), sqr.get(p)/2);
    		help.put(new Point(p.x + sqr.get(p)*3/4, p.y - sqr.get(p)/4), sqr.get(p)/2);
    		help.put(new Point(p.x + sqr.get(p)*3/4, p.y + sqr.get(p)*3/4), sqr.get(p)/2);
    	}
    	return help;
    }
    
    static Map<Point, Integer> sierpinski(Map<Point, Integer> sqr) {
    	Map<Point, Integer> help = new HashMap<>();
    	help.putAll(sqr);
    	for (Point p : sqr.keySet()) {
    		help.put(new Point(p.x - sqr.get(p)*2/3, p.y - sqr.get(p)*2/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)/3, p.y - sqr.get(p)*2/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)*4/3, p.y - sqr.get(p)*2/3), sqr.get(p)/3);
    		help.put(new Point(p.x - sqr.get(p)*2/3, p.y + sqr.get(p)/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)/3, p.y + sqr.get(p)/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)*4/3, p.y + sqr.get(p)/3), sqr.get(p)/3);
    		help.put(new Point(p.x - sqr.get(p)*2/3, p.y + sqr.get(p)*4/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)/3, p.y + sqr.get(p)*4/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)*4/3, p.y + sqr.get(p)*4/3), sqr.get(p)/3);
    	}
    	return help;
    }
    
    static Map<Point, Integer> cantor(Map<Point, Integer> sqr) {
    	Map<Point, Integer> help = new HashMap<>();
    	help.putAll(sqr);
    	for (Point p : sqr.keySet()) {
    		help.put(new Point(p.x, p.y + 30), sqr.get(p)/2 - sqr.get(p)/10);
    		help.put(new Point(p.x + sqr.get(p)/2 + sqr.get(p)/10, p.y + 30), sqr.get(p)/2 - sqr.get(p)/10);
    	}
    	return help;
    }
    
    static Map<Point, Integer> vicsek(Map<Point, Integer> sqr) {
    	Map<Point, Integer> help = new HashMap<>();
    	for (Point p : sqr.keySet()) {
    		help.put(new Point(p.x, p.y), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)/3, p.y + sqr.get(p)/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)*2/3, p.y), sqr.get(p)/3);
    		help.put(new Point(p.x, p.y + sqr.get(p)*2/3), sqr.get(p)/3);
    		help.put(new Point(p.x + sqr.get(p)*2/3, p.y + sqr.get(p)*2/3), sqr.get(p)/3);
    	}
    	return help;
    }
}
