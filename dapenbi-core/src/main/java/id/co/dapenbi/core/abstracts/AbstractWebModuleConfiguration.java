package id.co.dapenbi.core.abstracts;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public abstract class AbstractWebModuleConfiguration {

    public ServletRegistrationBean getServletRegistrationBean() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setApplicationContext(getApplicationContext());

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletRegistrationBean.setName(getServletName());
        servletRegistrationBean.addUrlMappings(getMapping());
        servletRegistrationBean.setAsyncSupported(true);

        return servletRegistrationBean;
    }

    public abstract WebApplicationContext getApplicationContext();

    protected abstract String getServletName();

    public abstract String[] getMapping();
}
