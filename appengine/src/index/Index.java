package index;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Index extends HttpServlet {
	int number = 1;
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
              throws IOException {
    	UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        if (user != null) {
            if(req.getParameter("q") != null){
	            if(req.getParameter("q").equals("getnumber")){
	            	resp.setContentType("application/javascript;charset=utf-8");
	            	resp.getWriter().println("{\"number\":"+number+"}");
	            	return;
	            }
	            resp.setContentType("text/html;charset=utf-8");
	            if(req.getParameter("q").equals("increment")){
	            	resp.getWriter().println("{\"number\":"+(++number)+"}");
	            } else if(req.getParameter("q").equals("reset")){
	            	number = 1;
	            	resp.getWriter().println("{\"number\":"+number+"}");
	            } else if(req.getParameter("q").equals("decrement")){
	            	if(number > 1)
	            		--number;
	            	resp.getWriter().println("{\"number\":"+number+"}");
	            }
        	}
            
            resp.getWriter().println("<br><button><a href='?q=decrement'>Decr</a></button><button><a href='?q=reset'>Start</a></button><button><a href='?q=increment'>Incr</a></button>");
            
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }
}